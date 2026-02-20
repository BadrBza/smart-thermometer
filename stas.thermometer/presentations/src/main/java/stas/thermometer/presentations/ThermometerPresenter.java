package stas.thermometer.presentations;

import stas.thermometer.domains.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.format.DateTimeFormatter;


public class ThermometerPresenter implements TemperatureDisplay {
    private final  Thermometer thermometer;
    private final ThermometerView view;
    private  Sonde sonde;
    private SondeType selectedSondeType = SondeType.TEMPERATURE;
    private SondeHumidity sondeHumidity;


    public ThermometerPresenter(Thermometer thermometer, ThermometerView view,Sonde sonde, SondeHumidity sondeHumidity) {
        this.thermometer = thermometer;
        this.view = view;
        this.sonde = sonde;
       // new TemperatureAlert(this);
        this.sondeHumidity = sondeHumidity;
    }


    public String presentName() {
        StringBuilder builder = new StringBuilder();
        builder.append(thermometer.name()).append("\n");
       // builder.append("Température : ").append((thermometer.getTemperatureFormat())).append(" °C\n");
        //builder.append("Date/heure : ").append((thermometer.getDatetimeFormat())).append("\n");
      //  builder.append("Profils : \n").append((thermometer.getProfiles()));
        return builder.toString();
    }


    public void runMainLoop() {
        try (var input = new BufferedReader(new InputStreamReader(System.in))) {
            boolean quitRequested = false;
            do {
                view.display("> ");
                String cmd = input.readLine();
                quitRequested = handleCommand(cmd.strip());
            } while (!quitRequested);
        } catch (IOException e) {
            view.display("erreur: commande invalide");
        }
    }

    public boolean handleCommand(String cmd) {
        return switch (cmd) {
            case "s" -> {
                toggleSondeSelection();
                yield false;
            }
            case "h" -> {
                displayHelp();
                yield false;
            }
            case "q" -> true;
            case "r" -> {
                raiseTemperature();
                yield false;
            }
            case "m" -> {
                mitigateTemperature();
                yield false;
            }
            default -> false;
        };
    }

    public void toggleSondeSelection() {
        if (selectedSondeType == SondeType.TEMPERATURE) {
            selectedSondeType = SondeType.HUMIDITY;
            view.display("Sonde d'humidite relative selectionnee");
        } else {
            selectedSondeType = SondeType.TEMPERATURE;
            view.display("Sonde de température sélectionnée");
        }
    }



    public void displayHelp() {
        view.display("l - List the commands");
        view.display("m - Mitigate the generated temperature");
        view.display("r - Raise the generated temperature");
        view.display("q - quit the application");
    }

    private void raiseTemperature() {
        if (selectedSondeType == SondeType.TEMPERATURE) {
            sonde.adjustValue(0.5);
        } else if(selectedSondeType == SondeType.HUMIDITY) {
           sondeHumidity.adjustValue(4);
        }
    }

    private void mitigateTemperature() {

        if (selectedSondeType == SondeType.TEMPERATURE) {
            sonde.adjustValue(-0.5);
        } else if(selectedSondeType == SondeType.HUMIDITY){
            sondeHumidity.adjustValue(-4);
        }
    }


    @Override
    public void displayTemperature(Mesure mesure) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy a HH:mm:ss");
            String formattedDate = mesure.moment().format(formatter);
            view.display("Temperature moyenne a " + formattedDate + " est: " + mesure.temperature() + " C");
    }

    @Override
    public void displayHumidity(Mesure mesure) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            String formattedDate = mesure.moment().format(formatter);
            view.display("L'humidite moyenne a " + formattedDate + " est de : " + mesure.temperature() + " %");
    }



    @Override
    public void displayAlert(String alertType, double expectedTemperature, double difference){
        //if(selectedSondeType == SondeType.TEMPERATURE) {
            view.displayAlert(alertType, expectedTemperature, difference);
        //}

    }

    @Override
    public void displayAlertHumidity(String alertType, double expectedTemperature, double difference) {
        //if(selectedSondeType == SondeType.HUMIDITY) {
            view.displayAlertHumidity(alertType,expectedTemperature,difference);
        //}

    }

    @Override
    public void displayAlertReturn(String condition) {
        if(selectedSondeType == SondeType.TEMPERATURE) {
            view.display(condition);
        }

    }

    @Override
    public void displayAlertReturnHumidity() {
        if(selectedSondeType == SondeType.HUMIDITY) {
            view.display("Condition d'alerte d'humidité résolue pour l'humidité");
        }
    }

    @Override
    public void displayMessageDB(String message) {
        view.display(message);
    }


    public void setSonde(Sonde sonde) {
        this.sonde = sonde;
    }

    public void setSondeHumidity(SondeHumidity sonde) {
        this.sondeHumidity = sonde;
    }





}
