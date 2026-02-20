package stas.thermometer.domains.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import stas.thermometer.domains.*;
import static org.mockito.Mockito.*;

class TemperatureAlertTests {

    private TemperatureAlert alert;
    private TemperatureDisplay mockDisplay;

    @BeforeEach
    void setUp() {
        mockDisplay = mock(TemperatureDisplay.class);
        IMesureDataMapper mockDataMapper = mock(IMesureDataMapper.class);
        alert = new TemperatureAlert(mockDisplay, mockDataMapper);
    }

    @Test
    void shouldNotDisplayAlertWhenTemperatureIsWithinNormalRange() throws RepositoryException {
        // Arrange:
        double actualTemperature = 25.0;
        double expectedTemperature = 24.75;  

        // Act:
        alert.checkTemperature(actualTemperature, expectedTemperature);

        // Assert:
        verify(mockDisplay, never()).displayAlert(anyString(), anyDouble(), anyDouble());
        verify(mockDisplay, never()).displayAlertReturn(anyString());
    }

    @Test
    void shouldActivateAlertWhenTemperatureExceedsThreshold() throws RepositoryException {
        // Arrange
        double actualTemperature = 28.0;
        double expectedTemperature = 25.0;  

        // Act:
        alert.checkTemperature(actualTemperature, expectedTemperature);

        // Assert:
        verify(mockDisplay).displayAlert("Surchauffe", 25.0, 3.0);
    }

    @Test
    void shouldResolveAlertWhenTemperatureReturnsToNormal() throws RepositoryException {
        // Arrange:
        alert.checkTemperature(28.0, 25.0);  /
        reset(mockDisplay);  
        double actualTemperature = 25.5; 
        double expectedTemperature = 25.0;

        // Act:
        alert.checkTemperature(actualTemperature, expectedTemperature);

        // Assert:
        verify(mockDisplay).displayAlertReturn("Condition d'alerte résolue pour la température");
    }
}
