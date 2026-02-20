using Stas.Monitor.Domains;

namespace Stas.Monitor.Presentations;

public interface IMeasurementDataHolder
{
    IList<CombinedTemperatureDto> SelectedTemperatureEvents { get; }

    IList<CombinedHumidityDto> SelectedHumidityEvents { get; }

    void ClearTemperatureEvents();

    void ClearHumidityEvents();

    void AddTemperatureEvent(CombinedTemperatureDto eventItem);

    void AddHumidityEvent(CombinedHumidityDto eventItem);
}
