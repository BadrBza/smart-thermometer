package stas.thermometer.domains;

import java.time.LocalDateTime;

public record Alert(String type, double value, LocalDateTime timestamp, int measurementId) {
}
