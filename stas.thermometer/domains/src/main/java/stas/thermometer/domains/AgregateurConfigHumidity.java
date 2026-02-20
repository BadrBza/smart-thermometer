package stas.thermometer.domains;

import java.util.List;

public record AgregateurConfigHumidity(List<Jalon> jalons, String thermometerName) {}
