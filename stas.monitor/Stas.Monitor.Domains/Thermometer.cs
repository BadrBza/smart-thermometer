namespace Stas.Monitor.Domains;

public record Thermometer(string Name, string DateTimeFormat, string TemperatureFormat, Dictionary<string, int> Profile);
