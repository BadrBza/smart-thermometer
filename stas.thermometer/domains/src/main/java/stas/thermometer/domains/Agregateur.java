package stas.thermometer.domains;
import java.time.LocalDateTime;
import java.util.List;

public class Agregateur extends AbstractAggregator {


    private  final String thermometerName;
    private final IMesureDataMapper mesureDataMapper;
    public Agregateur(TemperatureDisplay display,AgregateurConfig config,IMesureDataMapper mesureDataMapper) {
        super(display, config);
        this.mesureDataMapper =  mesureDataMapper;
        this.thermometerName = config.thermometerName();
    }

   @Override
   public void calculateAverageMeasurement() throws RepositoryException {
        synchronized (getLock()) {
            List<Mesure> measurements = getMeasurements();
            if (measurements.isEmpty()) {
                return;
            }

            double sum = measurements.stream().mapToDouble(Mesure::temperature).sum();
            double averageTemperature = sum / measurements.size();
            LocalDateTime moment = measurements.get(0).moment();

            Mesure average = new Mesure(averageTemperature, moment,thermometerName);
            getDisplay().displayTemperature(average);
            //double expectedTemperature = getExpectedValue(moment.toLocalTime());
            //int id = mesureDataMapper.insertTemperature(average);
            getTemperatureAlert().setLastMeasurementId(mesureDataMapper.insertTemperature(average));
            getTemperatureAlert().checkTemperature(average.temperature(), getExpectedValue(moment.toLocalTime()));
            measurements.clear();
        }
    }
}
