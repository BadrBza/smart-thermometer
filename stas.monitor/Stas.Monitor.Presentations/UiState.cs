namespace Stas.Monitor.Presentations;

public class UiState : IUiState
{
    private string _selectedThermometerName;
    private bool _showTemperatureMeasurements = true;
    private bool _showHumidityMeasurements = true;

    public string SelectedThermometerName
    {
        get { return _selectedThermometerName; }
        set { _selectedThermometerName = value; }
    }

    public bool ShowTemperatureMeasurements
    {
        get { return _showTemperatureMeasurements; }
        set { _showTemperatureMeasurements = value; }
    }

    public bool ShowHumidityMeasurements
    {
        get { return _showHumidityMeasurements; }
        set { _showHumidityMeasurements = value; }
    }
}
