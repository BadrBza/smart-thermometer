namespace Stas.Monitor.Presentations;

using Serilog;
using Stas.Monitor.Domains;
using Stas.Monitor.Infrastructures;

    public class MainPresenter
    {
        private readonly Configuration _configuration;

        public TimeSpan SelectedDuration { get; set; } = TimeSpan.FromMinutes(1);

        private readonly IDatabaseConnectionFactory _databaseConnectionFactory;

        private readonly IUiState _uiState;

        private readonly IMeasurementDataHolder _measurementDataHolder;

        public MainPresenter(Configuration configuration,IDatabaseConnectionFactory databaseConnectionFactory,IUiState uiState,IMeasurementDataHolder measurementDataHolder = null)
        {
            _configuration = configuration;
            _databaseConnectionFactory = databaseConnectionFactory;
            _measurementDataHolder = measurementDataHolder ?? new MeasurementDataHolder();
            _uiState = uiState;
            FirstNameThermometer();
            SelectedDuration = TimeSpan.FromMinutes(1);
        }

        public IEnumerable<string> ThermometerNames => _configuration.ConfigThermometer.Select(t => t.Name);

        public IList<CombinedTemperatureDto> SelectedTemperatureEvents => _measurementDataHolder.SelectedTemperatureEvents;

        public IList<CombinedHumidityDto> SelectedHumidityEvents => _measurementDataHolder.SelectedHumidityEvents;

        public string SelectedThermometerName
        {
            get =>_uiState.SelectedThermometerName;
            set
            {
                if (string.IsNullOrWhiteSpace(value))
                {
                    return;
                }

                if (_uiState.SelectedThermometerName != value)
                {
                    _uiState.SelectedThermometerName = value;
                    UpdateMeasurements();
                    UpdateMeasurementsHumidity();
                }
            }
        }

        private void FirstNameThermometer()
        {
            var firstThermometerName = ThermometerNames.FirstOrDefault();
            if (!string.IsNullOrEmpty(firstThermometerName))
            {
                _uiState.SelectedThermometerName = firstThermometerName;
                UpdateMeasurements();
                UpdateMeasurementsHumidity();
            }
            else
            {
                _uiState.SelectedThermometerName = string.Empty;
            }
        }

        public bool ShowTemperatureMeasurements
        {
            get => _uiState.ShowTemperatureMeasurements;
            set
            {
                if (_uiState.ShowTemperatureMeasurements != value)
                {
                    _uiState.ShowTemperatureMeasurements = value;
                    ToggleVisibilityForTemperatureSeries();
                }
            }
        }

        public bool ShowHumidityMeasurements
        {
            get => _uiState.ShowHumidityMeasurements;
            set
            {
                if (_uiState.ShowHumidityMeasurements != value)
                {
                    _uiState.ShowHumidityMeasurements = value;
                    ToggleVisibilityForHumiditySeries();
                }
            }
        }

        public void ToggleVisibilityForTemperatureSeries()
        {
            if (ShowTemperatureMeasurements)
            {
                UpdateMeasurements();
            }
            else
            {
                _measurementDataHolder.ClearTemperatureEvents();
            }
        }

        public void ToggleVisibilityForHumiditySeries()
        {
            if (ShowHumidityMeasurements)
            {
                UpdateMeasurementsHumidity();
            }
            else
            {
                _measurementDataHolder.ClearHumidityEvents();
            }
        }

        public void UpdateMeasurements()
        {
            if (string.IsNullOrWhiteSpace(_uiState.SelectedThermometerName))
            {
                _measurementDataHolder.ClearTemperatureEvents();
                return;
            }

            try
            {
                using (var connection = _databaseConnectionFactory.CreateConnection())
                {
                    connection.Open();
                    var measurementSql = new MeasurementSql();
                    var combinedData = measurementSql.GetCombinedTemperatureData(connection, _uiState.SelectedThermometerName, SelectedDuration);
                    _measurementDataHolder.ClearTemperatureEvents();
                    foreach (var data in combinedData)
                    {
                        _measurementDataHolder.AddTemperatureEvent(data);
                    }
                }
            }
            catch (Exception ex)
            {
                Log.Error(ex, "stas monitor: unable to read data");
            }
        }

        public void UpdateMeasurementsHumidity()
        {
            if (string.IsNullOrWhiteSpace(_uiState.SelectedThermometerName))
            {
                _measurementDataHolder.ClearHumidityEvents();
                return;
            }

            try
            {
                using (var connection = _databaseConnectionFactory.CreateConnection())
                {
                    connection.Open();
                    var measurementSql = new MeasurementSql();
                    var combinedData = measurementSql.GetCombinedHumidityData(connection, _uiState.SelectedThermometerName, SelectedDuration);
                    _measurementDataHolder.ClearHumidityEvents();
                    foreach (var data in combinedData)
                    {
                        _measurementDataHolder.AddHumidityEvent(data);
                    }
                }
            }
            catch (Exception ex)
            {
                Log.Error(ex, "stas monitor: unable to read data");
            }
        }

        public TimeSpan SelectedDurationTime
        {
            get => SelectedDuration;
            set
            {
                if (SelectedDuration != value)
                {
                    SelectedDuration = value;
                    UpdateMeasurements();
                    UpdateMeasurementsHumidity();
                }
            }
        }
   }

