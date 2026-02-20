package stas.thermometer.domains;

import java.time.LocalTime;


public record Jalon(LocalTime time, double temperature) {

}