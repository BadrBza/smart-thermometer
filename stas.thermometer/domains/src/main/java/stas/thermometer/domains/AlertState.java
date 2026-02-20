package stas.thermometer.domains;

public record AlertState(boolean alertActive, double initialMeasurement, String alertType) { }
