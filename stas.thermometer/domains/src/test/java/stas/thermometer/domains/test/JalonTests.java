package stas.thermometer.domains.test;

import org.junit.jupiter.api.Test;
import stas.thermometer.domains.Jalon;

import java.time.LocalTime;
import static org.junit.jupiter.api.Assertions.*;

class JalonTests {

    @Test
    void shouldAssignValuesCorrectly_WhenConstructed() {
        // Arrange
        LocalTime expectedTime = LocalTime.of(10, 30);  // Utiliser une heure fixe
        double expectedTemperature = 22.5;

        // Act
        Jalon jalon = new Jalon(expectedTime, expectedTemperature);

        // Assert
        assertEquals(expectedTime, jalon.time());
        assertEquals(expectedTemperature, jalon.temperature());
    }

    @Test
    void shouldEqual_WhenValuesAreTheSame() {
        // Arrange
        LocalTime time = LocalTime.of(10, 30);
        Jalon jalon1 = new Jalon(time, 22.5);
        Jalon jalon2 = new Jalon(time, 22.5);

        // Act & Assert
        assertEquals(jalon1, jalon2, "Deux jalons avec les mêmes valeurs doivent être égaux");
    }

    @Test
    void shouldNotEqual_WhenValuesAreDifferent() {
        // Arrange
        Jalon jalon1 = new Jalon(LocalTime.of(10, 30), 22.5);
        Jalon jalon2 = new Jalon(LocalTime.of(11, 30), 23.0);  // Valeurs différentes

        // Act & Assert
        assertNotEquals(jalon1, jalon2, "Deux jalons avec des valeurs différentes ne doivent pas être égaux");
    }

    @Test
    void shouldGenerateEqualHashCodes_ForEqualObjects() {
        // Arrange
        LocalTime time = LocalTime.of(10, 30);
        Jalon jalon1 = new Jalon(time, 22.5);
        Jalon jalon2 = new Jalon(time, 22.5);

        // Act & Assert
        assertEquals(jalon1.hashCode(), jalon2.hashCode(), "Deux jalons égaux doivent avoir le même hashCode");
    }
}

