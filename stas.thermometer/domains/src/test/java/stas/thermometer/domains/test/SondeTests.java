package stas.thermometer.domains.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import stas.thermometer.domains.Sonde;
import stas.thermometer.domains.TemperatureObserver;
import stas.thermometer.domains.Mesure;
import stas.thermometer.domains.Jalon;

import java.time.LocalTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class SondeTests {

    private Sonde sonde;
    private TemperatureObserver mockObserver;

    @BeforeEach
    void setUp() {
        Jalon jalon = new Jalon(LocalTime.now(), 20.0);
        sonde = new Sonde(Arrays.asList(jalon));
        mockObserver = mock(TemperatureObserver.class);
        sonde.registerObserver(mockObserver);
    }

    @Test
    void shouldNotifyObserversWithCorrectlyAdjustedTemperature() {
        // Arrange
        double adjustment = 2.0;
        sonde.adjustValue(adjustment);

        // Act
        sonde.run();

        // Assert
        ArgumentCaptor<Mesure> mesureCaptor = ArgumentCaptor.forClass(Mesure.class);
        verify(mockObserver, atLeastOnce()).update(mesureCaptor.capture());

        Mesure capturedMesure = mesureCaptor.getValue();
        double capturedTemperature = capturedMesure.temperature();
        assertTrue(capturedTemperature >= (20.0 - 0.5 + adjustment) && capturedTemperature <= (20.0 + 0.5 + adjustment),
                "La température ajustée doit être dans la plage attendue");
    }
}
