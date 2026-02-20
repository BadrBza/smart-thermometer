using System.Collections.ObjectModel;
using Stas.Monitor.Domains;

namespace Stas.Monitor.Presentations;

public class MeasurementDataHolder : IMeasurementDataHolder
{
    public IList<CombinedTemperatureDto> SelectedTemperatureEvents { get; }

    public IList<CombinedHumidityDto> SelectedHumidityEvents { get; }

    public MeasurementDataHolder()
    {
        SelectedTemperatureEvents = new ObservableCollection<CombinedTemperatureDto>();
        SelectedHumidityEvents = new ObservableCollection<CombinedHumidityDto>();
    }

    public void ClearTemperatureEvents()
    {
        SelectedTemperatureEvents.Clear();
    }

    public void ClearHumidityEvents()
    {
        SelectedHumidityEvents.Clear();
    }

    public void AddTemperatureEvent(CombinedTemperatureDto eventItem)
    {
        SelectedTemperatureEvents.Add(eventItem);
    }

    public void AddHumidityEvent(CombinedHumidityDto eventItem)
    {
        SelectedHumidityEvents.Add(eventItem);
    }
}
