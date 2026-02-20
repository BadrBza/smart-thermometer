package stas.thermometer.infrastructures.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import stas.thermometer.infrastructures.DatabaseConnector;

import java.sql.Connection;


class DatabaseConnectorTests {

    private final String root = "root";
    private final String validDbUrl = "jdbc:derby:../DbTest";
    private final String invalidDbUrl = "jdbc:derby:invalidPath/DbTest";

    @Test
    void shouldEstablishConnectionWhenUrlIsValid() {
        // Arrange
        DatabaseConnector connector = new DatabaseConnector(validDbUrl, root, root);

        // Act & Assert
        Assertions.assertDoesNotThrow(() -> {
            try (Connection conn = connector.getConnection()) {
                Assertions.assertNotNull(conn, "La connexion doit être établie.");
            }
        });
    }

    @Test
    void shouldThrowRepositoryExceptionWhenUrlIsInvalid() {
        // Arrange
        DatabaseConnector connector = new DatabaseConnector(invalidDbUrl, root, root);

        // Act & Assert
        Assertions.assertDoesNotThrow(() -> {
            try (Connection conn = connector.getConnection()) {
                Assertions.assertNull(conn, "La connexion doit échoué.");
            }
        });
    }
}
