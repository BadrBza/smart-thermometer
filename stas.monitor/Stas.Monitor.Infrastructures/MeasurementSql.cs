namespace Stas.Monitor.Infrastructures;
using System.Data;
using Stas.Monitor.Domains;

    public class MeasurementSql
    {
        public DateTime GetLatestTimestamp(IDbConnection connection, string thermometerName)
        {
            string query = @"SELECT MAX(timestamp) FROM Temperature WHERE thermometer_name = @ThermometerName";

            using (IDbCommand command = connection.CreateCommand())
            {
                command.CommandText = query;
                var parameter = command.CreateParameter();
                parameter.ParameterName = "@ThermometerName";
                parameter.Value = thermometerName;
                command.Parameters.Add(parameter);

                var result = command.ExecuteScalar();
                return result != DBNull.Value ? Convert.ToDateTime(result) : DateTime.MinValue;
            }
        }

        public DateTime GetLatestHumidityTimestamp(IDbConnection connection, string thermometerName)
        {
            string query = @"SELECT MAX(timestamp) FROM Humidity WHERE thermometer_name = @ThermometerName";

            using (IDbCommand command = connection.CreateCommand())
            {
                command.CommandText = query;
                var parameter = command.CreateParameter();
                parameter.ParameterName = "@ThermometerName";
                parameter.Value = thermometerName;
                command.Parameters.Add(parameter);

                var result = command.ExecuteScalar();
                return result != DBNull.Value ? Convert.ToDateTime(result) : DateTime.MinValue;
            }
        }

        public IList<CombinedTemperatureDto> GetCombinedTemperatureData(IDbConnection connection, string thermometerName, TimeSpan duration)
        {
            var combinedData = new List<CombinedTemperatureDto>();
            var latestTimestamp = GetLatestTimestamp(connection, thermometerName);
            if (latestTimestamp == DateTime.MinValue)
            {
                return combinedData;
            }

            var startTime = latestTimestamp - duration;

            string query = @"SELECT t.thermometer_name, t.temperature, t.timestamp, a.type, a.difference, a.measurement_id
                     FROM Temperature t LEFT JOIN AlertsTemperature a ON t.id = a.measurement_id  WHERE t.thermometer_name LIKE @ThermometerName AND t.timestamp >= @StartTime ORDER BY t.timestamp DESC";

            using (IDbCommand command = connection.CreateCommand())
            {
                command.CommandText = query;
                AddParameter(command, "@ThermometerName", $"%{thermometerName}%");
                AddParameter(command, "@StartTime", startTime);

                using (IDataReader reader = command.ExecuteReader())
                {
                    while (reader.Read())
                    {
                        var temperature = reader["temperature"] != DBNull.Value ? Convert.ToDouble(Convert.ToDecimal(reader["temperature"])) : 0.0;

                        var temperatureData = new CombinedTemperatureDto(
                            reader["thermometer_name"].ToString(),
                            temperature,
                            Convert.ToDateTime(reader["timestamp"]),
                            reader.IsDBNull(reader.GetOrdinal("difference")) ? null : Convert.ToDouble(Convert.ToDecimal(reader["difference"])));
                        combinedData.Add(temperatureData);
                    }
                }
            }

            return combinedData;
        }

        public IList<CombinedHumidityDto> GetCombinedHumidityData(IDbConnection connection, string thermometerName, TimeSpan duration)
        {
            var combinedData = new List<CombinedHumidityDto>();
            var latestTimestamp = GetLatestHumidityTimestamp(connection, thermometerName);
            if (latestTimestamp == DateTime.MinValue)
            {
                return combinedData;
            }

            var startTime = latestTimestamp - duration;

            string query = @"SELECT h.thermometer_name, h.humidity, h.timestamp, ah.type AS alert_type, ah.difference AS alert_difference
                     FROM Humidity h LEFT JOIN AlertsHumidity ah ON h.id = ah.humidity_id WHERE h.thermometer_name LIKE @ThermometerName AND h.timestamp >= @StartTime ORDER BY h.timestamp DESC";

            using (IDbCommand command = connection.CreateCommand())
            {
                command.CommandText = query;
                AddParameter(command, "@ThermometerName", $"%{thermometerName}%");
                AddParameter(command, "@StartTime", startTime);

                using (IDataReader reader = command.ExecuteReader())
                {
                    while (reader.Read())
                    {
                        var humidity = reader["humidity"] != DBNull.Value ? Convert.ToDouble(Convert.ToDecimal(reader["humidity"])) : 0.0;
                        var alertDifference = reader.IsDBNull(reader.GetOrdinal("alert_difference")) ? null : (double?)Convert.ToDouble(Convert.ToDecimal(reader["alert_difference"]));

                        var humidityData = new CombinedHumidityDto(
                            reader["thermometer_name"].ToString(),
                            humidity,
                            Convert.ToDateTime(reader["timestamp"]),
                            alertDifference);
                        combinedData.Add(humidityData);
                    }
                }
            }

            return combinedData;
        }

        private void AddParameter(IDbCommand command, string parameterName, object value)
        {
            var parameter = command.CreateParameter();
            parameter.ParameterName = parameterName;
            parameter.Value = value;
            command.Parameters.Add(parameter);
        }
    }


