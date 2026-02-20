[CmdletBinding()]
param(
    [switch]$KeepExistingInstances
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

function Write-Step {
    param([string]$Message)
    Write-Host "[+] $Message"
}

function Wait-ListenPort {
    param(
        [int]$Port,
        [int]$TimeoutSeconds = 30
    )

    $deadline = (Get-Date).AddSeconds($TimeoutSeconds)
    while ((Get-Date) -lt $deadline) {
        $listener = Get-NetTCPConnection -State Listen -LocalPort $Port -ErrorAction SilentlyContinue
        if ($listener) {
            return $listener | Select-Object -First 1
        }

        Start-Sleep -Milliseconds 500
    }

    return $null
}

function Ensure-IniValue {
    param(
        [string]$Path,
        [string]$Key,
        [string]$Value
    )

    if (-not (Test-Path $Path)) {
        return
    }

    $lines = Get-Content $Path
    $pattern = "^(?i)\s*$([Regex]::Escape($Key))\s*="
    $updated = $false

    for ($i = 0; $i -lt $lines.Count; $i++) {
        if ($lines[$i] -match $pattern) {
            $lines[$i] = "$Key=$Value"
            $updated = $true
        }
    }

    if (-not $updated) {
        $lines += "$Key=$Value"
    }

    [System.IO.File]::WriteAllLines($Path, $lines, [System.Text.Encoding]::ASCII)
}

$repoRoot = $PSScriptRoot
$mysqlBin = "C:\Program Files\MySQL\MySQL Server 8.0\bin"
$mysqldExe = Join-Path $mysqlBin "mysqld.exe"
$mysqlExe = Join-Path $mysqlBin "mysql.exe"

if (-not (Test-Path $mysqldExe)) {
    throw "mysqld.exe introuvable: $mysqldExe"
}

if (-not (Test-Path $mysqlExe)) {
    throw "mysql.exe introuvable: $mysqlExe"
}

$mysqlPort = 13306
$mysqlDataDir = Join-Path $env:USERPROFILE "mysql-local-thermo"
$mysqlOutLog = Join-Path $mysqlDataDir "mysql.out.log"
$mysqlErrLog = Join-Path $mysqlDataDir "mysql.err.log"
$dbUrl = "jdbc:mysql://127.0.0.1:13306/Q220251"

Write-Step "Mise a jour des configs vers la DB locale"
Ensure-IniValue -Path (Join-Path $repoRoot "stas.monitor\Stas.Monitor.App\config.ini") -Key "Url" -Value $dbUrl
Ensure-IniValue -Path (Join-Path $repoRoot "stas.monitor\Stas.Monitor.App\bin\Debug\net6.0\config.ini") -Key "Url" -Value $dbUrl
Ensure-IniValue -Path (Join-Path $repoRoot "stas.thermometer\app\src\main\resources\salon.ini") -Key "url" -Value $dbUrl

if (-not (Test-Path $mysqlDataDir)) {
    New-Item -ItemType Directory -Path $mysqlDataDir | Out-Null
}

$listener = Wait-ListenPort -Port $mysqlPort -TimeoutSeconds 1
if (-not $listener) {
    Write-Step "Demarrage de MySQL local sur 127.0.0.1:$mysqlPort"

    if (-not (Test-Path (Join-Path $mysqlDataDir "mysql"))) {
        & $mysqldExe "--initialize-insecure" "--datadir=$mysqlDataDir" | Out-Null
    }

    Start-Process -FilePath $mysqldExe `
        -ArgumentList "--datadir=$mysqlDataDir", "--port=$mysqlPort", "--bind-address=127.0.0.1", "--console" `
        -RedirectStandardOutput $mysqlOutLog `
        -RedirectStandardError $mysqlErrLog | Out-Null

    $listener = Wait-ListenPort -Port $mysqlPort -TimeoutSeconds 40
    if (-not $listener) {
        $errTail = if (Test-Path $mysqlErrLog) { Get-Content $mysqlErrLog -Tail 50 } else { @("Aucun log d'erreur MySQL") }
        throw "MySQL local n'a pas demarre. Dernieres erreurs:`n$($errTail -join "`n")"
    }
}

Write-Step "Initialisation du schema local (idempotent)"
$initSql = @"
CREATE DATABASE IF NOT EXISTS Q220251;
CREATE USER IF NOT EXISTS 'Q220251'@'localhost' IDENTIFIED BY '0251';
ALTER USER 'Q220251'@'localhost' IDENTIFIED BY '0251';
GRANT ALL PRIVILEGES ON Q220251.* TO 'Q220251'@'localhost';
CREATE USER IF NOT EXISTS 'Q220251'@'127.0.0.1' IDENTIFIED BY '0251';
ALTER USER 'Q220251'@'127.0.0.1' IDENTIFIED BY '0251';
GRANT ALL PRIVILEGES ON Q220251.* TO 'Q220251'@'127.0.0.1';
FLUSH PRIVILEGES;
USE Q220251;
CREATE TABLE IF NOT EXISTS Temperature (
  id INT PRIMARY KEY AUTO_INCREMENT,
  thermometer_name VARCHAR(255) NOT NULL,
  temperature DECIMAL(10,4),
  timestamp DATETIME NOT NULL
);
CREATE TABLE IF NOT EXISTS Humidity (
  id INT PRIMARY KEY AUTO_INCREMENT,
  thermometer_name VARCHAR(255) NOT NULL,
  humidity DECIMAL(10,4),
  timestamp DATETIME NOT NULL
);
CREATE TABLE IF NOT EXISTS AlertsTemperature (
  id INT PRIMARY KEY AUTO_INCREMENT,
  type VARCHAR(50) NOT NULL,
  difference DECIMAL(10,4),
  timestamp DATETIME NOT NULL,
  measurement_id INT,
  CONSTRAINT fk_alerts_temperature_measurement_id FOREIGN KEY (measurement_id) REFERENCES Temperature(id) ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS AlertsHumidity (
  id INT PRIMARY KEY AUTO_INCREMENT,
  type VARCHAR(50) NOT NULL,
  difference DECIMAL(10,4),
  timestamp DATETIME NOT NULL,
  humidity_id INT,
  CONSTRAINT fk_alerts_humidity_humidity_id FOREIGN KEY (humidity_id) REFERENCES Humidity(id) ON DELETE CASCADE
);
"@

$initSql | & $mysqlExe -h 127.0.0.1 -P $mysqlPort -u root | Out-Null

$thermoRoot = Join-Path $repoRoot "stas.thermometer"
$thermoRegex = [Regex]::Escape($thermoRoot)
$monitorExe = Join-Path $repoRoot "stas.monitor\Stas.Monitor.App\bin\Debug\net6.0\Stas.Monitor.App.exe"
$monitorWorkDir = Split-Path -Parent $monitorExe

if (-not (Test-Path $monitorExe)) {
    throw "Executable monitor introuvable: $monitorExe. Lance d'abord: dotnet build .\\stas.monitor\\Stas.Monitor.App\\Stas.Monitor.App.csproj -c Debug"
}

if (-not $KeepExistingInstances) {
    Write-Step "Arret des anciennes instances monitor/thermometer"
    Get-Process -Name "Stas.Monitor.App" -ErrorAction SilentlyContinue | Stop-Process -Force

    $thermoProcesses = Get-CimInstance Win32_Process | Where-Object {
        ($_.Name -eq "cmd.exe" -and $_.CommandLine -match $thermoRegex -and $_.CommandLine -match "gradlew\.bat") -or
        ($_.Name -eq "java.exe" -and $_.CommandLine -match "stas\.thermometer\.app\.App")
    }

    foreach ($proc in $thermoProcesses) {
        Stop-Process -Id $proc.ProcessId -Force -ErrorAction SilentlyContinue
    }
}

Write-Step "Demarrage de stas.thermometer"
$thermoCmdArgs = '/c ""gradlew.bat" :app:run --args="--config-file salon.ini" --no-daemon --console=plain"'
$thermoWrapper = Start-Process -FilePath "cmd.exe" -ArgumentList $thermoCmdArgs -WorkingDirectory $thermoRoot -PassThru

$thermoPid = $null
for ($i = 0; $i -lt 60; $i++) {
    Start-Sleep -Milliseconds 500
    $thermoApp = Get-CimInstance Win32_Process -Filter "name='java.exe'" | Where-Object {
        $_.CommandLine -match "stas\.thermometer\.app\.App\s+--config-file\s+salon\.ini"
    } | Select-Object -First 1

    if ($thermoApp) {
        $thermoPid = $thermoApp.ProcessId
        break
    }
}

Write-Step "Demarrage de stas.monitor"
$monitorProcess = Start-Process -FilePath $monitorExe -ArgumentList "--config-file", "config.ini" -WorkingDirectory $monitorWorkDir -PassThru

$mysqlPid = (Wait-ListenPort -Port $mysqlPort -TimeoutSeconds 2).OwningProcess

Write-Host ""
Write-Host "Demarrage termine:"
Write-Host "  MySQL local      : 127.0.0.1:$mysqlPort (PID $mysqlPid)"
Write-Host "  stas.monitor     : PID $($monitorProcess.Id)"
if ($thermoPid) {
    Write-Host "  stas.thermometer : PID $thermoPid (wrapper PID $($thermoWrapper.Id))"
}
else {
    Write-Host "  stas.thermometer : wrapper PID $($thermoWrapper.Id) (app Java non detectee immediatement)"
}
Write-Host ""
Write-Host "Astuce: reexecution sans fermer les apps existantes => .\\start-all-local.ps1 -KeepExistingInstances"
