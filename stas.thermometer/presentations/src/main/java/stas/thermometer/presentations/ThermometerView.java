package stas.thermometer.presentations;



public interface ThermometerView {

     void display(String data);

    void displayAlert(String alertType, double expectedTemperature, double difference);

    void displayAlertHumidity(String alertType, double expectedTemperature, double difference);
}
