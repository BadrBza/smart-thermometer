package stas.thermometer.domains.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import stas.thermometer.domains.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

class AgregateurHumidityTests {

    private AgregateurHumidity agregateurHumidity;

    @Mock
    private TemperatureDisplay mockDisplay;

    @Mock
    private TemperatureAlert mockAlert;

    @Mock
    private HumidityAlert mockAlertHumidity;

    @Mock
    private IMesureDataMapper mockMesureDataMapper;

    @Mock
    private TemperatureAlert mockTemperatureAlert;

    @Mock
    private HumidityAlert mockHumidityAlert;




    private final String thermometerName = "TestHumidityThermometer";
    private final List<Jalon> jalonsHumidity = Collections.singletonList(new Jalon(LocalTime.now(), 50.0));

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        AgregateurConfig config = new AgregateurConfig(jalonsHumidity, thermometerName,mockHumidityAlert,mockTemperatureAlert);
        agregateurHumidity = new AgregateurHumidity(mockDisplay,mockMesureDataMapper , config);
    }

    @Test
    void shouldDoNothing_whenCalculateAverageMeasurementWithNoMeasurements() throws RepositoryException {

        // Act
        agregateurHumidity.calculateAverageMeasurement();

        // Assert
        verify(mockDisplay, never()).displayHumidity(any());
        verify(mockAlert, never()).checkTemperature(anyDouble(), anyDouble());
        verify(mockMesureDataMapper, never()).insertHumidity(any());
    }

    @Test
    void shouldDisplayAndCheckHumidity_whenCalculateAverageMeasurementWithMeasurements() throws RepositoryException {
        // Arrange
        Mesure mockMesure = new Mesure(55.0, LocalDateTime.now(), thermometerName);
        agregateurHumidity.update(mockMesure);
        when(mockMesureDataMapper.insertHumidity(any())).thenReturn(1);

        // Act
        agregateurHumidity.calculateAverageMeasurement();

        // Assert
        verify(mockDisplay).displayHumidity(mockMesure);
        verify(mockMesureDataMapper).insertHumidity(mockMesure);
    }
}

