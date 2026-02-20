
using Microsoft.Data.Sqlite;
using System.Data;
using SQLitePCL;

namespace Stas.Monitor.Infrastructures.Tests
{
    [TestFixture]
    public class MeasurementSqlTests
    {
        private IDbConnection _connection;
        private MeasurementSql _measurementSql;

        [OneTimeSetUp]
        public void GlobalSetup()
        {
            Batteries_V2.Init();
        }

        [SetUp]
        public void Setup()
        {
            _connection = new SqliteConnection("DataSource=:memory:");
            _connection.Open();

            CreateTestDatabase(_connection);

            _measurementSql = new MeasurementSql();
        }

        private void CreateTestDatabase(IDbConnection connection)
        {
            using (var command = connection.CreateCommand())
            {
                command.CommandText =
                    @"CREATE TABLE Temperature (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        thermometer_name TEXT NOT NULL,
                        temperature REAL,
                        timestamp TEXT NOT NULL
                      );
                      INSERT INTO Temperature (thermometer_name, temperature, timestamp) VALUES
                      ('ThermoTest', 23.5, '2021-01-01T12:34:56'),
                      ('ThermoTest', 24.5, '2021-01-02T13:00:00');

                      CREATE TABLE Humidity (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        thermometer_name TEXT NOT NULL,
                        humidity REAL,
                        timestamp TEXT NOT NULL
                      );
                      INSERT INTO Humidity (thermometer_name, humidity, timestamp) VALUES
                      ('ThermoTest', 50.0, '2021-01-01T14:00:00'),
                      ('ThermoTest', 55.0, '2021-01-02T15:30:00');

                      CREATE TABLE AlertsTemperature (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        type TEXT NOT NULL,
                        difference REAL,
                        timestamp TEXT NOT NULL,
                        measurement_id INTEGER,
                        FOREIGN KEY (measurement_id) REFERENCES Temperature(id) ON DELETE CASCADE
                      );
                      INSERT INTO AlertsTemperature (type, difference, timestamp, measurement_id) VALUES
                      ('temperature', 2.5, '2021-01-02T13:00:00', 2);

                      CREATE TABLE AlertsHumidity (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        type TEXT NOT NULL,
                        difference REAL,
                        timestamp TEXT NOT NULL,
                        humidity_id INTEGER,
                        FOREIGN KEY (humidity_id) REFERENCES Humidity(id) ON DELETE CASCADE
                      );
                      INSERT INTO AlertsHumidity (type, difference, timestamp, humidity_id) VALUES
                      ('humidity', 5.0, '2021-01-02T15:30:00', 2);";
                command.ExecuteNonQuery();
            }
        }

        [TearDown]
        public void Teardown()
        {
            _connection.Close();
        }

        [Test]
        public void Should_GetLatestTemperatureTimestamp_ForGivenThermometer()
        {
            DateTime result = _measurementSql.GetLatestTimestamp(_connection, "ThermoTest");
            Assert.AreEqual(new DateTime(2021, 1, 2, 13, 0, 0), result);
        }

        [Test]
        public void Should_GetLatestHumidityTimestamp_ForGivenThermometer()
        {
            DateTime result = _measurementSql.GetLatestHumidityTimestamp(_connection, "ThermoTest");
            Assert.AreEqual(new DateTime(2021, 1, 2, 15, 30, 0), result);
        }

        [Test]
        public void Should_GetCombinedTemperatureData_ForGivenThermometerAndDuration()
        {
            TimeSpan duration = TimeSpan.FromDays(2);
            var result = _measurementSql.GetCombinedTemperatureData(_connection, "ThermoTest", duration);
            Assert.AreEqual(2, result.Count);
        }

        [Test]
        public void Should_GetCombinedHumidityData_ForGivenThermometerAndDuration()
        {
            TimeSpan duration = TimeSpan.FromDays(2);
            var result = _measurementSql.GetCombinedHumidityData(_connection, "ThermoTest", duration);
            Assert.AreEqual(2, result.Count);
        }

        [Test]
        public void Should_ReturnMinValue_ForNonExistentThermometer()
        {
            DateTime result = _measurementSql.GetLatestTimestamp(_connection, "NonExistentThermo");
            Assert.AreEqual(DateTime.MinValue, result);
        }

        [Test]
        public void Should_ReturnEmpty_ForNegativeDurationInCombinedTemperatureData()
        {
            TimeSpan duration = TimeSpan.FromDays(-1);
            var result = _measurementSql.GetCombinedTemperatureData(_connection, "ThermoTest", duration);
            Assert.IsEmpty(result);
        }

        public void Should_ReturnEmpty_ForNegativeDurationInCombinedHumidityData()
        {
            TimeSpan duration = TimeSpan.FromDays(-1);
            var result = _measurementSql.GetCombinedHumidityData(_connection, "ThermoTest", duration);
            Assert.IsEmpty(result);
        }
    }
}
