package stas.thermometer.domains;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Random;

public class Sonde extends AbstractSonde {
    private final Random random = new Random();
    private final List<Jalon> jalons;


    public Sonde(List<Jalon> jalons) {
        this.jalons = jalons;
    }

    @Override
    public void run() {
        double baseTemperature = getExpectedTemperature(LocalTime.now());
        double temperatureVariation = -0.5 + random.nextDouble() * 1.0;
        double currentTemperature = baseTemperature + temperatureVariation + getAdjustment();

        LocalDateTime moment = LocalDateTime.now();
        notifyObservers(new Mesure(currentTemperature, moment,null));

        //temperatureAlert.checkTemperature(currentTemperature, baseTemperature);
    }

    public double getExpectedTemperature(LocalTime time) {
        int currentJalonIndex = getCurrentJalonIndex(time);
        double relativePos = getRelativePosition(time);

        // Récupérer les jalons actuel et suivant
        Jalon currentJalon = jalons.get(currentJalonIndex);
        Jalon nextJalon = jalons.get((currentJalonIndex + 1) % jalons.size());

        return calculateExpectedTemperature(relativePos, currentJalon, nextJalon);
    }

    private int getCurrentJalonIndex(LocalTime time) {
        int totalJalons = jalons.size();
        double hoursPerJalon = 24.0 / totalJalons;
        return (int)(time.getHour() / hoursPerJalon);
    }

    private double getRelativePosition(LocalTime time) {
        double hoursPerJalon = 24.0 / jalons.size();
        return (time.getHour() % hoursPerJalon) / hoursPerJalon + (double) time.getMinute() / 60 / hoursPerJalon;
    }

    private double calculateExpectedTemperature(double relativePos, Jalon currentJalon, Jalon nextJalon) {
        double hoursPerJalon = 24.0 / jalons.size();
        double pente = (nextJalon.temperature() - currentJalon.temperature()) / hoursPerJalon;

        //Random rand = new Random();
        //expectedTemp += (rand.nextDouble() - 0.5) * 1;

        return currentJalon.temperature() + pente * relativePos * hoursPerJalon;
    }

}
