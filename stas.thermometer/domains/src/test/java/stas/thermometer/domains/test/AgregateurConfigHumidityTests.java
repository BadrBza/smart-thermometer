package stas.thermometer.domains.test;

import stas.thermometer.domains.AgregateurConfigHumidity;
import stas.thermometer.domains.Jalon;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AgregateurConfigHumidityTests {

    @Test
    void shouldCreateAgregateurConfigHumidityWithGivenValues() {
        // Arrange
        List<Jalon> jalons = List.of(new Jalon(LocalTime.now(), 25.0));
        String thermometerName = "TestThermometer";

        // Act
        AgregateurConfigHumidity config = new AgregateurConfigHumidity(jalons, thermometerName);

        // Assert
        assertAll(
                () -> assertEquals(jalons, config.jalons(), "Jalons should be the same as provided"),
                () -> assertEquals(thermometerName, config.thermometerName(), "Thermometer name should be the same as provided")
        );
    }

    @Test
    void shouldEqualAnotherConfigWithSameData() {
        // Arrange
        List<Jalon> jalons = List.of(new Jalon(LocalTime.now(), 25.0));
        String thermometerName = "TestThermometer";
        AgregateurConfigHumidity config1 = new AgregateurConfigHumidity(jalons, thermometerName);
        AgregateurConfigHumidity config2 = new AgregateurConfigHumidity(jalons, thermometerName);

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
        AgregateurConfigHumidity config = new AgregateurConfigHumidity(jalons, thermometerName);

        // Act
        String expectedString = "AgregateurConfigHumidity[jalons=" + jalons + ", thermometerName=" + thermometerName + "]";

        // Assert
        assertEquals(expectedString, config.toString(), "ToString should return the correct representation of the object");
    }
}
