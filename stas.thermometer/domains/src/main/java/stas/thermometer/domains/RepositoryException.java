package stas.thermometer.domains;

public class RepositoryException extends Exception {
    public RepositoryException(String message, Throwable e) {
        super(message, e);
    }
}