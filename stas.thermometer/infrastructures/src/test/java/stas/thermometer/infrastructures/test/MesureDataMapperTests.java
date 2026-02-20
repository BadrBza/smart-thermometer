package stas.thermometer.infrastructures.test;

import org.junit.jupiter.api.*;
import stas.thermometer.domains.*;
import stas.thermometer.infrastructures.DatabaseConnector;
import stas.thermometer.infrastructures.MesureDataMapper;

import java.sql.*;
import java.time.LocalDateTime;


class MesureDataMapperTests {
    private DatabaseConnector connector;
    private MesureDataMapper mapper;
    private TemperatureDisplay view;

    @BeforeEach
    void setup() throws SQLException, RepositoryException {
        // Connexion à la base de données Derby
        String url = "jdbc:derby:../DbTest";
        connector = new DatabaseConnector(url, "root", "root");
        mapper = new MesureDataMapper(connector, view);

        // Création des tables
        try (Connection connection = connector.getConnection()) {
            Statement statement = connection.createStatement();
            // Création de la table Temperature
            String key = "id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, ";
            statement.execute("CREATE TABLE Temperature (" +
                    key +
                    "thermometer_name VARCHAR(255), " +
                    "temperature DECIMAL(5, 2), " +
                    "timestamp TIMESTAMP)");

            // Création de la table Humidity
            statement.execute("CREATE TABLE Humidity (" +
                    key +
                    "thermometer_name VARCHAR(255), " +
                    "humidity DECIMAL(5, 2), " +
                    "timestamp TIMESTAMP)");

            // Création de la table AlertsTemperature
            statement.execute("CREATE TABLE AlertsTemperature (" +
                    key +
                    "type VARCHAR(50), " +
                    "difference DECIMAL(5, 2), " +
                    "timestamp TIMESTAMP, " +
                    "measurement_id INT, " +
                    "CONSTRAINT fk_alerts_temperature_measurement_id " +
                    "FOREIGN KEY (measurement_id) REFERENCES Temperature(id) ON DELETE CASCADE)");

            // Création de la table AlertsHumidity
            statement.execute("CREATE TABLE AlertsHumidity (" +
                    key +
                    "type VARCHAR(50), " +
                    "difference DECIMAL(5, 2), " +
                    "timestamp TIMESTAMP, " +
                    "humidity_id INT, " +
                    "CONSTRAINT fk_alerts_humidity_humidity_id " +
                    "FOREIGN KEY (humidity_id) REFERENCES Humidity(id) ON DELETE CASCADE)");
        }

    }


    @Test
    void shouldInsertTemperatureSuccessfully() throws RepositoryException {
        Mesure mesure = new Mesure(25.0, LocalDateTime.of(2023, 1, 1, 10, 0), "budokai");
        int id = mapper.insertTemperature(mesure);
        Assertions.assertTrue(id > 0);
    }

    @Test
    void shouldInsertHumiditySuccessfully() throws RepositoryException {
        Mesure mesure = new Mesure(50.0, LocalDateTime.of(2023, 1, 1, 10, 0), "senkaimon");
        int id = mapper.insertHumidity(mesure);
        Assertions.assertTrue(id > 0);
    }

    @Test
    void shouldInsertHumidityAlertSuccessfully() throws RepositoryException {
        Mesure mesureHumidity = new Mesure(50.0, LocalDateTime.of(2023, 1, 1, 10, 0), "Thermometer");
        int humidityId = mapper.insertHumidity(mesureHumidity);

        Alert alert = new Alert("Humidity", 60.0,LocalDateTime.of(2023, 1, 1, 10, 0), humidityId);
        int alertId = mapper.insertHumidityAlert(alert);
        Assertions.assertTrue(alertId > 0);
    }

    @Test
    void shouldInsertTemperatureAlertSuccessfully() throws RepositoryException {
        Mesure mesureTemp = new Mesure(25.0, LocalDateTime.now(), "Test");
        int temperatureId = mapper.insertTemperature(mesureTemp);

        Alert alert = new Alert("Temperature", 30.0, LocalDateTime.now(), temperatureId);
        int alertId = mapper.insertTemperatureAlert(alert);
        Assertions.assertTrue(alertId > 0);
    }

    @Test
    void shouldThrowExceptionWhenInsertTemperatureWithNullMeasure() {
        Mesure mesure = null;
        Assertions.assertThrows(NullPointerException.class, () -> mapper.insertTemperature(mesure));
    }



    @AfterEach
    void tearDown() throws SQLException {
        // Nettoyage de la base de données
        try (Connection connection = connector.getConnection()) {
            Statement statement = connection.createStatement();
            // Suppression des tables AlertsTemperature, AlertsHumidity, Temperature, Humidity
            statement.execute("DROP TABLE AlertsTemperature");
            statement.execute("DROP TABLE AlertsHumidity");
            statement.execute("DROP TABLE Temperature");
            statement.execute("DROP TABLE Humidity");
        }
    }
}
