package stas.thermometer.domains;

import java.time.LocalDateTime;


public record Mesure(double temperature, LocalDateTime moment, String ThermometerName) {
}
