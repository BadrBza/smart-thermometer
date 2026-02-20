package stas.thermometer.domains;

public interface TemperatureObserver {
    void update(Mesure mesure);
}