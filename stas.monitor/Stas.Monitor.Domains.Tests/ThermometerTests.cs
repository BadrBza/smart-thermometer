using NUnit.Framework;
using Stas.Monitor.Domains;


namespace Stas.Monitor.Tests;

[TestFixture]
public class ThermometerTests
{
    [Test]
    public void DefaultProperties_AreInitializedCorrectly()
    {
        // Arrange
        var name = "Test Thermometer";
        var dateTimeFormat = "yyyy-MM-dd";
        var temperatureFormat = "degres";
        var profile = new Dictionary<string, int>();

        // Act
        var thermometer = new Thermometer(name, dateTimeFormat, temperatureFormat, profile);

        // Assert
        Assert.AreEqual(name, thermometer.Name);
        Assert.AreEqual(dateTimeFormat, thermometer.DateTimeFormat);
        Assert.AreEqual(temperatureFormat, thermometer.TemperatureFormat);
        Assert.IsNotNull(thermometer.Profile);
        Assert.IsEmpty(thermometer.Profile);
    }
}
