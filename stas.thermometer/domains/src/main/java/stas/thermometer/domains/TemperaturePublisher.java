package stas.thermometer.domains;



public interface TemperaturePublisher {
    void registerObserver(TemperatureObserver observer);
    void removeObserver(TemperatureObserver observer);
    void notifyObservers(Mesure mesure);
}

