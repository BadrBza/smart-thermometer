namespace Stas.Monitor.Domains;

public record CombinedHumidityDto(
    string ThermometerName,
    double Humidity,
    DateTime Timestamp,
    double? AlertDifference);
