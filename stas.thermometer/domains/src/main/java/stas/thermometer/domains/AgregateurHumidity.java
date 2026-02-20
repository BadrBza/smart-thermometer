package stas.thermometer.domains;
import java.time.LocalDateTime;
import java.util.List;

public class AgregateurHumidity extends AbstractAggregator {

    private  final String thermometerName;
    private final IMesureDataMapper mesureDataMapper;


    public AgregateurHumidity(TemperatureDisplay display,IMesureDataMapper mesureDataMapper,AgregateurConfig config) {
        super(display,config);
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
                getDisplay().displayHumidity(average);

                getHumidityAlert().setLastMeasurementHumidityId(mesureDataMapper.insertHumidity(average));
                getHumidityAlert().checkHumidity(average.temperature(), getExpectedValue(moment.toLocalTime()));

                measurements.clear();
            }
    }
}
