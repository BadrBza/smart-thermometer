package stas.thermometer.domains;

import java.util.List;

public record AgregateurConfig(List<Jalon> jalons,
                               String thermometerName,
                               HumidityAlert humidityAlert,
                               TemperatureAlert temperatureAlert) {}
