package stas.thermometer.domains;

import java.util.List;


public record Thermometer(String name, String temperatureFormat, String datetimeFormat, List<Jalon> profiles) {

}
