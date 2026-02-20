namespace Stas.Monitor.Domains;

public interface IConfigurationReader
{
    void LoadFromConfig(TextReader reader);

    //void DisplayAllSections();

    //void DisplayThermometerConfigs();
}
