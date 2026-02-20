package stas.thermometer.domains.test;

import stas.thermometer.domains.*;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.junit.jupiter.api.Assertions.*;

class AgregateurConfigTests {

    @Test
    void shouldCreateAgregateurConfigWithGivenValues() {
        // Arrange
        List<Jalon> jalons = List.of(new Jalon(LocalTime.now(), 25.0));
        String thermometerName = "TestThermometer";
        HumidityAlert humidityAlert = mock(HumidityAlert.class);
        TemperatureAlert temperatureAlert = mock(TemperatureAlert.class);

        // Act
        AgregateurConfig config = new AgregateurConfig(jalons, thermometerName, humidityAlert, temperatureAlert);

        // Assert
        assertAll(
                () -> assertEquals(jalons, config.jalons(), "Jalons should match the provided values"),
                () -> assertEquals(thermometerName, config.thermometerName(), "Thermometer name should match the provided value"),
                () -> assertEquals(humidityAlert, config.humidityAlert(), "HumidityAlert should match the provided object"),
                () -> assertEquals(temperatureAlert, config.temperatureAlert(), "TemperatureAlert should match the provided object")
        );
    }

    @Test
    void shouldEqualAnotherConfigWithSameData() {
        // Arrange
        List<Jalon> jalons = List.of(new Jalon(LocalTime.now(), 25.0));
        String thermometerName = "TestThermometer";
        HumidityAlert humidityAlert = mock(HumidityAlert.class);
        TemperatureAlert temperatureAlert = mock(TemperatureAlert.class);
        AgregateurConfig config1 = new AgregateurConfig(jalons, thermometerName, humidityAlert, temperatureAlert);
        AgregateurConfig config2 = new AgregateurConfig(jalons, thermometerName, humidityAlert, temperatureAlert);

        // Act & Assert
        assertAll(
                () -> assertEquals(config1, config2, "Two configs with the same data should be equal"),
                () -> assertEquals(config1.hashCode(), config2.hashCode(), "Hashcodes of two equal objects should be the same")
        );
    }

    @Test
    void shouldReturnCorrectToStringRepresentation() {
        // Arrange
        List<Jalon> jalons = List.of(new Jalon(LocalTime.now(), 25.0));
        String thermometerName = "TestThermometer";
        HumidityAlert humidityAlert = mock(HumidityAlert.class);
        TemperatureAlert temperatureAlert = mock(TemperatureAlert.class);
        AgregateurConfig config = new AgregateurConfig(jalons, thermometerName, humidityAlert, temperatureAlert);

        // Act
        String expectedString = "AgregateurConfig[jalons=" + jalons +
                ", thermometerName=" + thermometerName +
                ", humidityAlert=" + humidityAlert +
                ", temperatureAlert=" + temperatureAlert + "]";

        // Assert
        assertEquals(expectedString, config.toString(), "ToString should return the correct representation of the object");
    }
}
