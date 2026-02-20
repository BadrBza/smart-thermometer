namespace Stas.Monitor.Presentations;

public interface IUiState
{
    string SelectedThermometerName { get; set; }

    bool ShowTemperatureMeasurements { get; set; }

    bool ShowHumidityMeasurements { get; set; }
}


