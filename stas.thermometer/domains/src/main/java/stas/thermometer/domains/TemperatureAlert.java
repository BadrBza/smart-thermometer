package stas.thermometer.domains;

import java.time.LocalDateTime;

public class TemperatureAlert {
    private static final double THRESHOLD_PERCENTAGE = 0.1;
    private AlertState alertState = new AlertState(false, 0.0, null);
    private final TemperatureDisplay view;
    private int lastMeasurementId;
    private final IMesureDataMapper mesureDataMapper;

    public TemperatureAlert(TemperatureDisplay view, IMesureDataMapper mesureDataMapper) {
        this.view = view;
        this.mesureDataMapper = mesureDataMapper;
    }

    public void setLastMeasurementId(int lastMeasurementId) {
        this.lastMeasurementId = lastMeasurementId;
    }

    public void checkTemperature(double actualTemperature, double expectedTemperature) throws RepositoryException {
        double difference = actualTemperature - expectedTemperature;

        if (!alertState.alertActive()) {
            checkForAlertActivation(actualTemperature, expectedTemperature, difference);
        } else {
            handleActiveAlert(actualTemperature, expectedTemperature, difference);
            createAndInsertAlertTemperature("temperature", difference, LocalDateTime.now());
        }
    }

    private void checkForAlertActivation(double actualTemperature, double expectedTemperature, double difference) {
        double threshold = expectedTemperature * THRESHOLD_PERCENTAGE;

        if (Math.abs(difference) > threshold) {
            String alertType = (actualTemperature > expectedTemperature) ? "Surchauffe" : "Refroidissement";
            alertState = new AlertState(true, expectedTemperature, alertType);
            view.displayAlert(alertState.alertType(), expectedTemperature, difference);
        }
    }

    private void handleActiveAlert(double actualTemperature, double expectedTemperature, double difference) {
        double threshold = expectedTemperature * THRESHOLD_PERCENTAGE;

        if (Math.abs(actualTemperature - alertState.initialMeasurement()) <= threshold) {
            alertState = new AlertState(false, alertState.initialMeasurement(), alertState.alertType());
            view.displayAlertReturn("Condition d'alerte résolue pour la température");
        } else {
            view.displayAlert(alertState.alertType(), expectedTemperature, difference);
        }
    }

    private void createAndInsertAlertTemperature(String type, double value, LocalDateTime timestamp) throws RepositoryException {
        Alert alert = new Alert(type, value, timestamp, lastMeasurementId);
        try {
            mesureDataMapper.insertTemperatureAlert(alert);
        } catch (RepositoryException e) {
            view.displayMessageDB("stas thermometer : unable to insert data");
        }
    }
}
