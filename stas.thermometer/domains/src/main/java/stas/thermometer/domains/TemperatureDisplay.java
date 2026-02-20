package stas.thermometer.domains;


public interface TemperatureDisplay {
    void displayTemperature(Mesure mesure);

    void displayHumidity(Mesure mesure);

    void displayAlert(String alertType, double expectedTemperature, double difference);

    void displayAlertHumidity(String alertType, double expectedTemperature, double difference);

     void displayAlertReturn (String condition);

     void displayAlertReturnHumidity();

    //void displayHumidityAlert(String alertType, double expectedHumidity, double difference);

    void displayMessageDB(String message);


}


