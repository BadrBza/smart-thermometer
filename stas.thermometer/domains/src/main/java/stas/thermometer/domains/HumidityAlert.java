package stas.thermometer.domains;

import java.time.LocalDateTime;

public class HumidityAlert {
    private static final double THRESHOLD_PERCENTAGE = 0.1; // Seuil de 10%
    private AlertState alertState = new AlertState(false, 0.0, null);
    private final TemperatureDisplay view;
    private int lastMeasurementId;
    private final IMesureDataMapper mesureDataMapper;

    public HumidityAlert(TemperatureDisplay view, IMesureDataMapper mesureDataMapper) {
        this.view = view;
        this.mesureDataMapper = mesureDataMapper;
    }

    public void setLastMeasurementHumidityId(int lastMeasurementId) {
        this.lastMeasurementId = lastMeasurementId;
    }

    public void checkHumidity(double actualHumidity, double expectedHumidity) throws RepositoryException {
        double difference = actualHumidity - expectedHumidity;

        if (!alertState.alertActive()) {
            checkForHumidityAlertActivation(actualHumidity, expectedHumidity, difference);
        } else {
            handleActiveHumidityAlert(actualHumidity, expectedHumidity, difference);
            createAndInsertAlertHumidity("humidity", difference, LocalDateTime.now());
        }
    }

    private void checkForHumidityAlertActivation(double actualHumidity, double expectedHumidity, double difference) {
        double threshold = expectedHumidity * THRESHOLD_PERCENTAGE;

        if (Math.abs(difference) > threshold) {
            String alertType = (actualHumidity > expectedHumidity) ? "Trop Humide" : "Trop Sec";
            alertState = new AlertState(true, expectedHumidity, alertType);
            view.displayAlertHumidity(alertState.alertType(), expectedHumidity, difference);
        }
    }

    private void handleActiveHumidityAlert(double actualHumidity, double expectedHumidity, double difference) {
        double threshold = expectedHumidity * THRESHOLD_PERCENTAGE;

        if (Math.abs(actualHumidity - alertState.initialMeasurement()) <= threshold) {
            alertState = new AlertState(false, alertState.initialMeasurement(), alertState.alertType());
            view.displayAlertReturnHumidity();
        } else {
            view.displayAlertHumidity(alertState.alertType(), expectedHumidity, difference);
        }
    }

    private void createAndInsertAlertHumidity(String type, double value, LocalDateTime timestamp) throws RepositoryException {
        Alert alert = new Alert(type, value, timestamp, lastMeasurementId);
        try {
            mesureDataMapper.insertHumidityAlert(alert);
        } catch (RepositoryException e) {
            view.displayMessageDB("stas thermometer : unable to insert data");
        }
    }
}
