# Intelligent Thermometer

Projet compose de 2 applications:

- `stas.thermometer` (Java/Gradle): simule des mesures de temperature/humidite et ecrit en base.
- `stas.monitor` (.NET/Avalonia): lit la base et affiche les mesures/alertes.

Un script de demarrage est fourni pour tout lancer en local:

- `start-all-local.ps1`

## Prerequis

- Windows + PowerShell
- Java 17
- .NET SDK (8.x accepte ce projet `net6.0`)
- MySQL Server 8.0 installe ici:
  - `C:\Program Files\MySQL\MySQL Server 8.0\bin\mysqld.exe`
  - `C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe`

## Demarrage rapide (tout en une commande)

Depuis la racine du repo:

```powershell
powershell -ExecutionPolicy Bypass -File .\start-all-local.ps1
```

Le script:

1. force les configs vers la DB locale `127.0.0.1:13306`
2. demarre MySQL local si besoin
3. cree la base `Q220251` et les tables (`Temperature`, `Humidity`, `AlertsTemperature`, `AlertsHumidity`)
4. lance `stas.thermometer`
5. lance `stas.monitor`

Option utile:

```powershell
.\start-all-local.ps1 -KeepExistingInstances
```

Utilise cette option pour relancer sans fermer les instances deja actives.

## Lancer sur differents thermometres

## 1) Cote monitor (selection)

Dans l'UI `stas.monitor`, la liste des thermometres vient de:

- `stas.monitor/Stas.Monitor.App/config.ini`

Thermometres actuellement definis:

- `Minazuki`
- `MontFuji`
- `LaCalvitieIniesta`
- `LeBronx`
- `Hueco Mundo`

## 2) Cote thermometer (production de donnees)

`stas.thermometer` ecrit avec le nom defini dans:

- `stas.thermometer/app/src/main/resources/salon.ini`
- cle: `name=...` dans la section `[general]`

Pour alimenter un thermometre precis:

1. mettre `name=` a la meme valeur que dans `config.ini` monitor (ex: `MontFuji`)
2. relancer `.\start-all-local.ps1`

## 3) Plusieurs thermometres en parallele

Tu peux lancer plusieurs producteurs en meme temps:

1. dupliquer `salon.ini` dans `stas.thermometer/app/src/main/resources/` (ex: `montfuji.ini`, `lebronx.ini`)
2. ajuster `name=` dans chaque fichier
3. lancer une instance par fichier dans des terminaux differents:

```powershell
cd .\stas.thermometer
.\gradlew.bat :app:run --args="--config-file montfuji.ini" --no-daemon --console=plain
```

Puis dans le monitor, selectionner le thermometre voulu dans la liste.

## Demarrage manuel (sans script)

## Thermometer

```powershell
cd .\stas.thermometer
.\gradlew.bat :app:run --args="--config-file salon.ini" --no-daemon --console=plain
```

## Monitor

```powershell
dotnet build .\stas.monitor\Stas.Monitor.App\Stas.Monitor.App.csproj -c Debug
cd .\stas.monitor\Stas.Monitor.App\bin\Debug\net6.0
.\Stas.Monitor.App.exe --config-file config.ini
```

## Arret des applications

```powershell
Get-Process -Name Stas.Monitor.App -ErrorAction SilentlyContinue | Stop-Process -Force
Get-CimInstance Win32_Process | Where-Object {
  ($_.Name -eq "cmd.exe" -and $_.CommandLine -match "stas\.thermometer" -and $_.CommandLine -match "gradlew\.bat") -or
  ($_.Name -eq "java.exe" -and $_.CommandLine -match "stas\.thermometer\.app\.App")
} | ForEach-Object { Stop-Process -Id $_.ProcessId -Force }
```

## Depannage rapide

- `missing configuration file argument`
  - verifier que `--config-file ...` est bien passe au lancement.
- `stas monitor: unable to read data`
  - verifier que MySQL local tourne sur `127.0.0.1:13306`
  - verifier que `name=` dans le INI thermometer correspond a un thermometre defini dans `stas.monitor/Stas.Monitor.App/config.ini`.
