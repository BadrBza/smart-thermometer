package stas.thermometer.domains.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import stas.thermometer.domains.SondeHumidity;
import stas.thermometer.domains.TemperatureObserver;
import stas.thermometer.domains.Mesure;
import stas.thermometer.domains.Jalon;

import java.time.LocalTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class SondeHumidityTests {

    private SondeHumidity sondeHumidity;
    private TemperatureObserver mockObserver;

    @BeforeEach
    void setUp() {
        Jalon jalon = new Jalon(LocalTime.now(), 50.0);
        sondeHumidity = new SondeHumidity(Arrays.asList(jalon));
        mockObserver = mock(TemperatureObserver.class);
        sondeHumidity.registerObserver(mockObserver);
    }

    @Test
    void shouldNotifyObserversWithCorrectlyAdjustedHumidityValue() {
        // Arrange
        double adjustment = 5.0;
        sondeHumidity.adjustValue(adjustment);

        // Act
        sondeHumidity.run();

        // Assert
        ArgumentCaptor<Mesure> mesureCaptor = ArgumentCaptor.forClass(Mesure.class);
        verify(mockObserver, atLeastOnce()).update(mesureCaptor.capture());

        Mesure capturedMesure = mesureCaptor.getValue();
        double capturedHumidity = capturedMesure.temperature();
        assertTrue(capturedHumidity >= (50.0 - 5.0 + adjustment) && capturedHumidity <= (50.0 + 5.0 + adjustment),
                "L'humidité ajustée doit être dans la plage attendue");
    }
}
