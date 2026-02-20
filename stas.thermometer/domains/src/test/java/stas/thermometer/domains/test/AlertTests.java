package stas.thermometer.domains.test;

import stas.thermometer.domains.Alert;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class AlertTests {

    private final String thermoName = "Temperature";

    @Test
    void shouldCreateAlertWithCorrectValues() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        double value = 37.5;
        int measurementId = 1;

        // Act
        Alert alert = new Alert(thermoName, value, now, measurementId);

        // Assert
        assertEquals(thermoName, alert.type());
        assertEquals(value, alert.value());
        assertEquals(now, alert.timestamp());
        assertEquals(measurementId, alert.measurementId());
    }

    @Test
    void shouldConsiderTwoAlertsEqualWithSameData() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        Alert alert1 = new Alert(thermoName, 37.5, now, 1);
        Alert alert2 = new Alert(thermoName, 37.5, now, 1);

        // Act & Assert
        assertEquals(alert1, alert2);
    }

    @Test
    void shouldGenerateSameHashCodeForEqualAlerts() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        Alert alert1 = new Alert(thermoName, 37.5, now, 1);
        Alert alert2 = new Alert(thermoName, 37.5, now, 1);

        // Act & Assert
        assertEquals(alert1.hashCode(), alert2.hashCode());
    }
}
