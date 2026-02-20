package stas.thermometer.domains.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import stas.thermometer.domains.*;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AbstractSondeTest {

    private AbstractSonde sonde;
    private TemperatureObserver mockObserver;

    @BeforeEach
    void setUp() {
        // Arrange
        sonde = new ConcreteSonde();
        mockObserver = mock(TemperatureObserver.class);
    }

    @Test
    void shouldNotifyRegisteredObserver() {
        // Arrange
        sonde.registerObserver(mockObserver);
        Mesure mesure = new Mesure(25.0, null, "Test");

        // Act
        sonde.notifyObservers(mesure);

        // Assert
        verify(mockObserver).update(mesure);
    }

    @Test
    void shouldNotNotifyRemovedObserver() {
        // Arrange
        sonde.registerObserver(mockObserver);
        sonde.removeObserver(mockObserver);
        Mesure mesure = new Mesure(25.0, null, "Test");

        // Act
        sonde.notifyObservers(mesure);

        // Assert
        verify(mockObserver, never()).update(mesure);
    }

    @Test
    void shouldCorrectlyAdjustValue() {
        // Act
        sonde.adjustValue(1.0);

        // Assert
        assertEquals(1.0, sonde.getAdjustment());

        // Act
        sonde.adjustValue(-0.5);

        // Assert
        assertEquals(0.5, sonde.getAdjustment());
    }

    static class ConcreteSonde extends AbstractSonde {
        @Override
        public void run() {
        }
    }
}




