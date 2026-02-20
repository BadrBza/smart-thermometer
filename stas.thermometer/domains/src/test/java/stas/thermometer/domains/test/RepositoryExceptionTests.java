package stas.thermometer.domains.test;

import stas.thermometer.domains.RepositoryException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RepositoryExceptionTests {

    @Test
    void shouldReturnCorrectMessageWhenConstructedWithMessage() {
        Exception exception = assertThrows(RepositoryException.class, () -> {
            throw new RepositoryException("Test exception message", null);
        });
        assertEquals("Test exception message", exception.getMessage());
    }

    @Test
    void shouldContainCauseWhenConstructedWithCause() {
        Throwable cause = new RuntimeException("Cause of exception");
        Exception exception = assertThrows(RepositoryException.class, () -> {
            throw new RepositoryException("Test exception with cause", cause);
        });

        assertEquals("Test exception with cause", exception.getMessage());
        assertSame(cause, exception.getCause());
    }

}
