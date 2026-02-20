package stas.thermometer.infrastructures;

import java.sql.Connection;
import java.sql.SQLException;

@FunctionalInterface
public interface DatabaseAction<T> {
    int perform(Connection connection, String tableNameAndField, T data) throws SQLException;
}