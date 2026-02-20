package stas.thermometer.domains.test;

import org.junit.jupiter.api.Test;
import stas.thermometer.domains.Mesure;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class MesureTests {

    @Test
    void shouldCorrectlyAssignValuesWhenConstructed() {
        // Arrange
        double expectedTemperature = 25.0;
        LocalDateTime expectedMoment = LocalDateTime.now();

        // Act
        Mesure mesure = new Mesure(expectedTemperature, expectedMoment,null);

        // Assert
        assertEquals(expectedTemperature, mesure.temperature());
        assertEquals(expectedMoment, mesure.moment());
    }

    @Test
    void shouldEqualAnotherInstanceWithSameValues() {
        // Arrange
        double temperature = 25.0;
        LocalDateTime moment = LocalDateTime.now();
        Mesure mesure1 = new Mesure(temperature, moment,null);
        Mesure mesure2 = new Mesure(temperature, moment,null);

        // Act & Assert
        assertEquals(mesure1, mesure2);

    }

    @Test
    void shouldGenerateSameHashCodeForEqualObjects() {
        // Arrange
        double temperature = 25.0;
        LocalDateTime moment = LocalDateTime.now();
        Mesure mesure1 = new Mesure(temperature, moment,null);
        Mesure mesure2 = new Mesure(temperature, moment,null);

        // Act & Assert
        assertEquals(mesure1.hashCode(), mesure2.hashCode());
    }
}

