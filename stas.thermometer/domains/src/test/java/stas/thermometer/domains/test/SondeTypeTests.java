package stas.thermometer.domains.test;

import stas.thermometer.domains.SondeType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SondeTypeTests {

    @Test
    void shouldReturnCorrectNumberOfEnumValues() {
        // Act & Assert
        assertEquals(2, SondeType.values().length, "Il devrait y avoir exactement 2 types de sondes");
    }

    @Test
    void shouldValidatePresenceOfTemperatureEnum() {
        // Act & Assert
        assertNotNull(SondeType.valueOf("TEMPERATURE"), "TEMPERATURE devrait être un type de sonde valide");
    }

    @Test
    void shouldValidatePresenceOfHumidityEnum() {
        // Act & Assert
        assertNotNull(SondeType.valueOf("HUMIDITY"), "HUMIDITY devrait être un type de sonde valide");
    }

    @Test
    void shouldReturnCorrectEnumForTemperature() {
        // Act & Assert
        assertSame(SondeType.TEMPERATURE, SondeType.valueOf("TEMPERATURE"), "La méthode valueOf devrait renvoyer TEMPERATURE");
    }

    @Test
    void shouldReturnCorrectEnumForHumidity() {
        // Act & Assert
        assertSame(SondeType.HUMIDITY, SondeType.valueOf("HUMIDITY"), "La méthode valueOf devrait renvoyer HUMIDITY");
    }

    @Test
    void shouldReturnCorrectStringRepresentation() {
        // Act & Assert
        assertEquals("TEMPERATURE", SondeType.TEMPERATURE.toString(), "La méthode toString devrait renvoyer 'TEMPERATURE' pour TEMPERATURE");
        assertEquals("HUMIDITY", SondeType.HUMIDITY.toString(), "La méthode toString devrait renvoyer 'HUMIDITY' pour HUMIDITY");
    }
}
