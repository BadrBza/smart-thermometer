package stas.thermometer.domains;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Random;

public class SondeHumidity extends AbstractSonde {
    private final Random random = new Random();
    private final List<Jalon> jalonsHumidity;


    public SondeHumidity(List<Jalon> jalonsHumidity) {
        this.jalonsHumidity = jalonsHumidity;
    }

    @Override
    public void run() {
        double expectedHumidity = getExpectedHumidity(LocalTime.now());
        double humidityVariation = -5 + random.nextDouble() * 10; // Variation de -5% à +5%
        double currentHumidity = expectedHumidity + humidityVariation + getAdjustment();

        LocalDateTime moment = LocalDateTime.now();
        notifyObservers(new Mesure(currentHumidity, moment,null));

        //temperatureAlert.checkHumidity(currentHumidity, expectedHumidity);
    }

    public double getExpectedHumidity(LocalTime time) {
        int currentJalonIndex = getCurrentJalonIndex(time);
        double relativePos = getRelativePosition(time);

        // Récupérer les jalons actuel et suivant
        Jalon currentJalon = jalonsHumidity.get(currentJalonIndex);
        Jalon nextJalon = jalonsHumidity.get((currentJalonIndex + 1) % jalonsHumidity.size());

        return calculateExpectedTemperature(relativePos, currentJalon, nextJalon);
    }

    private int getCurrentJalonIndex(LocalTime time) {
        int totalJalons = jalonsHumidity.size();
        double hoursPerJalon = 24.0 / totalJalons;
        return (int)(time.getHour() / hoursPerJalon);
    }

    private double getRelativePosition(LocalTime time) {
        double hoursPerJalon = 24.0 / jalonsHumidity.size();
        return (time.getHour() % hoursPerJalon) / hoursPerJalon + (double) time.getMinute() / 60 / hoursPerJalon;
    }

    private double calculateExpectedTemperature(double relativePos, Jalon currentJalon, Jalon nextJalon) {
        double hoursPerJalon = 24.0 / jalonsHumidity.size();
        double pente = (nextJalon.temperature() - currentJalon.temperature()) / hoursPerJalon;

        //Random rand = new Random();
        //expectedTemp += (rand.nextDouble() - 0.5) * 1;

        return currentJalon.temperature() + pente * relativePos * hoursPerJalon;
    }

}
