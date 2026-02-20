using System;
using System.IO;
using System.Linq;
using Avalonia;
using Avalonia.Controls.ApplicationLifetimes;
using Avalonia.Markup.Xaml;
using Avalonia.Threading;
using Serilog;
using Stas.Monitor.Domains;
using Stas.Monitor.Infrastructures;
using Stas.Monitor.Presentations;
using Stas.Monitor.Views;

namespace Stas.Monitor.App
{
    public partial class App : Application
    {
        private MainWindow? _mainWindow;

        private DispatcherTimer? _fileUpdateTimer;

        public override void Initialize()
        {
            AvaloniaXamlLoader.Load(this);

            var log = new LoggerConfiguration()
                .WriteTo.Console()
                .CreateLogger();

            Log.Logger = log; // Définit le log dans un singleton (Beurk)
        }

        public override void OnFrameworkInitializationCompleted()
        {
            if (ApplicationLifetime is IClassicDesktopStyleApplicationLifetime desktop)
            {
                MainPresenter? presenter = SetupApp(desktop?.Args ?? Array.Empty<string>());
                desktop.MainWindow = _mainWindow;

                _fileUpdateTimer = new DispatcherTimer(TimeSpan.FromSeconds(5), DispatcherPriority.Background, (sender, args) =>
                {
                    Log.Logger.Information("Checking for file updates");
                    presenter?.UpdateMeasurements();
                    presenter?.UpdateMeasurementsHumidity();
                });
                _fileUpdateTimer.Start();
                desktop.Exit += OnApplicationExit;
            }

            base.OnFrameworkInitializationCompleted();
        }

        private void OnApplicationExit(object? sender, ControlledApplicationLifetimeExitEventArgs e)
        {
            if (_fileUpdateTimer != null)
            {
                _fileUpdateTimer.Stop();
                _fileUpdateTimer = null;
            }
        }

        private MainPresenter? SetupApp(string[] args)
        {
            if (args.Length < 2)
            {
                Log.Error("monitor: missing configuration file argument");
                return null;
            }

            string configFilePath = args[1];

            if (!File.Exists(configFilePath))
            {
                Log.Error("monitor: configuration file not found");
                return null;
            }

            try
            {
                using (FileStream fileStream = new FileStream(configFilePath, FileMode.Open))
                {
                    IniConfigurationReader reader = new IniConfigurationReader(fileStream);
                    Configuration config = reader.GetThermometerConfig();

                   /* if (string.IsNullOrWhiteSpace(config))
                    {
                        Log.Error("monitor: missing required properties measurements file path");
                        return null;
                    }*/

                    if (config.ConfigThermometer == null || !config.ConfigThermometer.Any())
                    {
                        Log.Error("monitor: missing required section thermometers");
                        return null;
                    }

                    string dbUrl = reader.Get("Database_0", "Url");
                    string dbUser = reader. Get("Database_0", "User");
                    string dbPassword = reader. Get("Database_0", "Mdp");

                    try
                    {
                        using (var dbConnection = new DatabaseConnection(dbUrl, dbUser, dbPassword).CreateConnection())
                        {
                            dbConnection.Open();
                        }
                    }
                    catch (Exception)
                    {
                        Log.Error("stas monitor: unable to connect to the database");
                        return null;
                    }

                    var databaseConnectionFactory = new DatabaseConnection(dbUrl, dbUser, dbPassword);
                    IUiState uiState = new UiState();
                    MainPresenter presenter = new MainPresenter(config,databaseConnectionFactory,uiState);
                    _mainWindow = new MainWindow(presenter);

                    return presenter;
                }
            }
            catch (Exception ex)
            {
                Log.Error($"monitor: {ex.Message}");
                return null;
            }
        }
    }
}
