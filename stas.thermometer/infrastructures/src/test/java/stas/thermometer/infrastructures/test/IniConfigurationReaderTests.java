package stas.thermometer.infrastructures.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import stas.thermometer.domains.Jalon;
import stas.thermometer.domains.Thermometer;
import stas.thermometer.infrastructures.IniConfigurationReader;


import static org.junit.jupiter.api.Assertions.*;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.time.LocalTime;
import java.util.List;

public class IniConfigurationReaderTests {

    private IniConfigurationReader iniReader;

    @BeforeEach
    public void setUp() throws FileNotFoundException {

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("LivingRoomTest.ini");
        iniReader = new IniConfigurationReader(inputStream);
    }

    @Test
    public void get_whenKeyExists_returnsValue() {
        assertEquals("Living Room 1", iniReader.get("general", "name"));
        assertEquals("00.00°", iniReader.get("format", "temperature"));
        assertEquals("dd/MM/YYYY à HH :mm :ss", iniReader.get("format", "datetime"));
    }

    @Test
    public void toThermometer_createsValidThermometer() {
        Thermometer thermometer = iniReader.toThermometer();
        assertEquals("Living Room 1", thermometer.name());
        assertEquals("00.00°", thermometer.temperatureFormat());
        assertEquals("dd/MM/YYYY à HH :mm :ss", thermometer.datetimeFormat());

        List<Jalon> expectedJalons = List.of(
                new Jalon(LocalTime.of(0, 0), 14),
                new Jalon(LocalTime.of(1, 0), 10),
                new Jalon(LocalTime.of(2, 0), 30)
        );
        assertEquals(expectedJalons, thermometer.profiles());
    }

    @Test
    public void get_whenKeyDoesNotExist_returnsNull() {
        assertNull(iniReader.get("invalidSection", "invalidKey"));
    }





}

