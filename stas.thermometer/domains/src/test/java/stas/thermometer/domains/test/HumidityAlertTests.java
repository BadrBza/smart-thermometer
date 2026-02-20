package stas.thermometer.domains.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import stas.thermometer.domains.*;
import static org.mockito.Mockito.*;

class HumidityAlertTests {

    private HumidityAlert alert;
    private TemperatureDisplay mockDisplay;

    @BeforeEach
    void setUp() {
        mockDisplay = mock(TemperatureDisplay.class);
        IMesureDataMapper mockDataMapper = mock(IMesureDataMapper.class);
        alert = new HumidityAlert(mockDisplay, mockDataMapper);
    }

    @Test
    void shouldNotDisplayAlertWhenHumidityIsWithinNormalRange() throws RepositoryException {
        // Arrange:
        double actualTemperature = 50.0;
        double expectedTemperature = 49.75;

        // Act:
        alert.checkHumidity(actualTemperature, expectedTemperature);

        // Assert:
        verify(mockDisplay, never()).displayAlert(anyString(), anyDouble(), anyDouble());
        verify(mockDisplay, never()).displayAlertReturn(anyString());
    }

    @Test
    void shouldActivateAlertWhenHumidityExceedsThreshold() throws RepositoryException {
        // Arrange
        double actualHumidity = 70.0;
        double expectedHumidity = 50.0;

        // Act:
        alert.checkHumidity(actualHumidity, expectedHumidity);

        // Assert:
        verify(mockDisplay).displayAlertHumidity("Trop Humide", expectedHumidity, 20.0);
    }

    @Test
    void shouldResolveAlertWhenTemperatureReturnsToNormal() throws RepositoryException {
        // Arrange:
        alert.checkHumidity(60.0, 50.0);
        reset(mockDisplay);
        double actualTemperature = 51.0;
        double expectedTemperature = 50.0;

        // Act:
        alert.checkHumidity(actualTemperature, expectedTemperature);

        // Assert:
        verify(mockDisplay).displayAlertReturnHumidity();
    }
}
