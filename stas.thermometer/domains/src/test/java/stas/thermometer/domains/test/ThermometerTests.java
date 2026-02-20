package stas.thermometer.domains.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import stas.thermometer.domains.Jalon;
import stas.thermometer.domains.Thermometer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

public class ThermometerTests {

    private Thermometer thermometer;
    private String testName;
    private String testTemperatureFormat;
    private String testDatetimeFormat;

    @BeforeEach
    public void setUp() {
        // Arrange:
        testName = "Test Thermometer";
        testTemperatureFormat = "Celsius";
        testDatetimeFormat = "yyyy-MM-dd HH:mm:ss";
        List<Jalon> testProfiles = Arrays.asList(
                new Jalon(LocalTime.of(0, 0), 20.0),
                new Jalon(LocalTime.of(12, 0), 25.5)
        );
        thermometer = new Thermometer(testName, testTemperatureFormat, testDatetimeFormat, testProfiles);
    }

    @Test
    public void shouldReturnCorrectName_whenGetNameIsCalled() {
        // Act:
        String resultName = thermometer.name();

        // Assert:
        assertEquals(testName, resultName, "Le nom retourné doit être identique au nom configuré.");
    }

    @Test
    public void shouldReturnCorrectTemperatureFormat_whenGetTemperatureFormatIsCalled() {
        // Act:
        String resultFormat = thermometer.temperatureFormat();

        // Assert:
        assertEquals(testTemperatureFormat, resultFormat, "Le format de température retourné doit être identique au format configuré.");
    }

    @Test
    public void shouldReturnCorrectDatetimeFormat_whenGetDatetimeFormatIsCalled() {
        // Act
        String resultDatetimeFormat = thermometer.datetimeFormat();

        // Assert
        assertEquals(testDatetimeFormat, resultDatetimeFormat, "Le format de date et heure retourné doit être identique au format configuré.");
    }
}
