package stas.thermometer.domains;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class AbstractSonde implements Runnable, TemperaturePublisher {
    private final List<TemperatureObserver> observers = new CopyOnWriteArrayList<>();
    private double adjustment = 0.0;

    @Override
    public void registerObserver(TemperatureObserver observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(TemperatureObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(Mesure mesure) {
        for (TemperatureObserver observer : observers) {
            observer.update(mesure);
        }
    }

    public void adjustValue(double adjustment) {
        this.adjustment += adjustment;
    }

    public double getAdjustment() {
        return adjustment;
    }


}
