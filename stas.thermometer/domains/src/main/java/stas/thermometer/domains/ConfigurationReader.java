package stas.thermometer.domains;

import java.util.List;


public interface ConfigurationReader   {


    //void loadFromConfig(Scanner scanner) throws FileNotFoundException;

    String get(String section, String key);


    public Thermometer toThermometer();

   // public List<Jalon> getJalons();

    //public List<JalonHumidity> getHumidityJalons();
    List<Jalon> getJalons(String sectionName);
}
