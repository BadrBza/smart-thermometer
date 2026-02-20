namespace Stas.Monitor.Domains;

public record CombinedTemperatureDto(
    string ThermometerName,
    double Temperature,
    DateTime Timestamp,
    double? Difference);
