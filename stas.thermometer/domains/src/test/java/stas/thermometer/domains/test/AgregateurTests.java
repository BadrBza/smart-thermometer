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

class AgregateurTests {

    private Agregateur agregateur;

    @Mock
    private TemperatureDisplay mockDisplay;

    @Mock
    private TemperatureAlert mockAlert;

    @Mock
    private HumidityAlert mockAlertHumidity;

    @Mock
    private IMesureDataMapper mockMesureDataMapper;

    private final List<Jalon> jalons = Collections.singletonList(new Jalon(LocalTime.now(), 25.0));
    private final String thermometerName = "TestThermometer";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        AgregateurConfig config = new AgregateurConfig(jalons, thermometerName, mockAlertHumidity, mockAlert);
        agregateur = new Agregateur(mockDisplay, config, mockMesureDataMapper);
    }

    @Test
    void shouldDoNothingWhenCalculateAverageMeasurementWithNoMeasurements() throws RepositoryException {

        // Act
        agregateur.calculateAverageMeasurement();

        // Assert
        verify(mockDisplay, never()).displayTemperature(any());
        verify(mockAlert, never()).checkTemperature(anyDouble(), anyDouble());
        verify(mockMesureDataMapper, never()).insertTemperature(any());
    }

    @Test
    void shouldDisplayAndCheckTemperatureWhenCalculateAverageMeasurementWithMeasurements() throws RepositoryException {
        // Arrange
        Mesure mockMesure = new Mesure(22.0, LocalDateTime.now(), thermometerName);
        agregateur.update(mockMesure);
        when(mockMesureDataMapper.insertTemperature(any())).thenReturn(1);

        // Act
        agregateur.calculateAverageMeasurement();

        // Assert
        verify(mockDisplay).displayTemperature(mockMesure);
        verify(mockAlert).checkTemperature(eq(22.0), anyDouble());
        verify(mockMesureDataMapper).insertTemperature(mockMesure);
    }
}
