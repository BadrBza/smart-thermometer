package stas.thermometer.domains;

public interface IMesureDataMapper {
    int insertTemperature(Mesure mesure) throws RepositoryException;
    int insertHumidity(Mesure mesure) throws RepositoryException;
    int insertHumidityAlert(Alert mesureAlerte) throws RepositoryException;
    int insertTemperatureAlert(Alert mesureAlerte) throws RepositoryException;
}
