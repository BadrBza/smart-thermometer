package stas.thermometer.infrastructures;

import stas.thermometer.domains.*;

import java.sql.*;

public class MesureDataMapper  implements IMesureDataMapper {
    private final DatabaseConnector connector;
    private final TemperatureDisplay view;

    public MesureDataMapper(DatabaseConnector connector, TemperatureDisplay view) {
        this.connector = connector;
        this.view = view;
    }

    public <T> int executeDatabaseAction(DatabaseAction<T> action, String tableNameAndField, T data)  {
        try (Connection connection = connector.getConnection()) {
            if(connection == null) {
                view.displayMessageDB(" stas thermometer : unable to connect to the database");
                return -1;
            }
            connection.setAutoCommit(false);
            int id = action.perform(connection, tableNameAndField, data);

            if (id <= 0) {
                connection.rollback();
            }

            connection.commit();
            return id;
        } catch (SQLException e) {
            //throw new RepositoryException("stas thermometer : unable to insert data", e);
            view.displayMessageDB("stas thermometer : unable to insert data");

        }

        return 0;
    }

    @Override
    public int insertTemperature(Mesure mesure) throws RepositoryException {
        return executeDatabaseAction(this::insertMesureAction, "Temperature (thermometer_name, temperature, timestamp)", mesure);
    }

    @Override
    public int insertHumidity(Mesure mesure) throws RepositoryException {
        return executeDatabaseAction(this::insertMesureAction, "Humidity (thermometer_name, humidity, timestamp)", mesure);
    }

    @Override
    public int insertHumidityAlert(Alert mesureAlerte) throws RepositoryException {
        return executeDatabaseAction(this::insertAlert, "AlertsHumidity (type, difference, timestamp,humidity_id)", mesureAlerte);
    }

    @Override
    public int insertTemperatureAlert(Alert mesureAlerte) throws RepositoryException {
        return executeDatabaseAction(this::insertAlert, "AlertsTemperature (type, difference, timestamp,measurement_id)", mesureAlerte);
    }


    private int insertMesureAction(Connection connection, String tableNameAndField, Mesure mesure) throws SQLException {
        String sql = "INSERT INTO " + tableNameAndField + " VALUES (?, ?, ?)";
        //System.out.println(sql);
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, mesure.ThermometerName());
            statement.setDouble(2, mesure.temperature());
            statement.setTimestamp(3, Timestamp.valueOf(mesure.moment()));
            statement.executeUpdate();
            return getIdFromGeneratedKeys(statement);
        }
    }

    public int insertAlert(Connection connection, String tableName, Alert alert) throws SQLException {
        String sql = "INSERT INTO " + tableName + " VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, alert.type());
            statement.setDouble(2, alert.value());
            statement.setTimestamp(3, Timestamp.valueOf(alert.timestamp()));
            statement.setInt(4, alert.measurementId());
            statement.executeUpdate();
             return getIdFromGeneratedKeys(statement);
        }
    }


    private int getIdFromGeneratedKeys(PreparedStatement statement) throws SQLException {
        try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            } else {
                throw new SQLException("Creating measure failed, no ID obtained.");
            }
        }
    }
}
