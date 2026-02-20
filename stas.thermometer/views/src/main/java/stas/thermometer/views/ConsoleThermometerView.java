package stas.thermometer.views;

import stas.thermometer.presentations.ThermometerView;



public class ConsoleThermometerView implements ThermometerView {

   // private static final Logger LOG = LogManager.getLogger("stas");
    public ConsoleThermometerView() {
    }
    @Override
    public void display(String data) {
        System.out.println(data);
    }


    @Override
    public void displayAlert(String alertType, double expectedTemperature, double difference) {
        String message = String.format("Alerte: %s. Température attendue: %.2f, écart: %.2f", alertType, expectedTemperature, difference);
        display(message);
    }

    @Override
    public void displayAlertHumidity(String alertType, double expectedTemperature, double difference) {
        String message = String.format("Alerte: %s. Humidite attendue: %.2f, écart: %.2f", alertType, expectedTemperature, difference);
        display(message);
    }






}
