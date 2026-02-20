package stas.thermometer.domains;


import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public abstract class AbstractAggregator implements TemperatureObserver {
    private final List<Mesure> measurements = new ArrayList<>();
    private final TemperatureDisplay display;
    private final Object lock = new Object();
    //private final List<Jalon> jalons;
    //private final TemperatureAlert temperatureAlert;
    //private final HumidityAlert humidityAlert;
    private final AgregateurConfig config;


    protected AbstractAggregator(TemperatureDisplay display, AgregateurConfig config) {
        this.display = display;
        this.config = config;
        //this.jalons = jalons;
        //this.temperatureAlert = temperatureAlert;
        //this.humidityAlert = humidityAlert;


        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                synchronized (lock) {
                    try {
                        calculateAverageMeasurement();
                    } catch (RepositoryException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }, 0, 2000);
    }

    @Override
    public void update(Mesure m) {
        synchronized (lock) {
            measurements.add(m);
        }
    }

    protected abstract void calculateAverageMeasurement() throws RepositoryException;

    /*protected void calculateAverageMeasurement() {
        synchronized (lock) {
            if (measurements.isEmpty()) {
                return;
            }

            double sum = measurements.stream().mapToDouble(Mesure::temperature).sum();
            double average = sum / measurements.size();
            LocalDateTime moment = measurements.get(0).moment();

            Mesure averageMesure = new Mesure(average, moment);
            displayMeasurement(averageMesure); // Appel à la méthode abstraite
            //double expectedValue = getExpectedValue(moment.toLocalTime());
            checkAlert(averageMesure);
            measurements.clear();
        }
    }*/

    //public TemperatureAlert getTemperatureAlert() {
      //  return temperatureAlert;
    //}

    //public TemperatureDisplay getDisplay() {
      //  return display;
   // }

    //protected abstract void displayMeasurement(Mesure mesure);

    //protected abstract void checkAlert(Mesure mesure);



    public double getExpectedValue(LocalTime time) {
        int currentJalonIndex = getCurrentJalonIndex(time);
        double relativePos = getRelativePosition(time);

        // Récupérer les jalons actuel et suivant
        Jalon currentJalon = config.jalons().get(currentJalonIndex);
        Jalon nextJalon = config.jalons().get((currentJalonIndex + 1) % config.jalons().size());

        return calculateExpectedTemperature(relativePos, currentJalon, nextJalon);
    }

    private int getCurrentJalonIndex(LocalTime time) {
        int totalJalons = config.jalons().size();
        double hoursPerJalon = 24.0 / totalJalons;
        return (int)(time.getHour() / hoursPerJalon);
    }

    private double getRelativePosition(LocalTime time) {
        double hoursPerJalon = 24.0 / config.jalons().size();
        return (time.getHour() % hoursPerJalon) / hoursPerJalon + (double) time.getMinute() / 60 / hoursPerJalon;
    }

    private double calculateExpectedTemperature(double relativePos, Jalon currentJalon, Jalon nextJalon) {
        double hoursPerJalon = 24.0 / config.jalons().size();
        double pente = (nextJalon.temperature() - currentJalon.temperature()) / hoursPerJalon;

        //Random rand = new Random();
        //expectedTemp += (rand.nextDouble() - 0.5) * 1;

        return currentJalon.temperature() + pente * relativePos * hoursPerJalon;
    }

    public List<Mesure> getMeasurements() {
        return measurements;
    }

    public TemperatureDisplay getDisplay() {
        return display;
    }

    public TemperatureAlert getTemperatureAlert() {
        return config.temperatureAlert();
    }

    public HumidityAlert getHumidityAlert() {
        return config.humidityAlert();
    }

    public Object getLock() {
        return lock;
    }
}
