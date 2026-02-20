using NUnit.Framework;
using Stas.Monitor.Presentations;

namespace Stas.Monitor.Presentations.Tests
{
    [TestFixture]
    public class UiStateTests
    {
        [Test]
        public void SelectedThermometerName_ShouldUpdateCorrectly()
        {
            // Arrange
            var uiState = new UiState();
            var expectedThermometerName = "Thermometer1";

            // Act
            uiState.SelectedThermometerName = expectedThermometerName;

            // Assert
            Assert.AreEqual(expectedThermometerName, uiState.SelectedThermometerName);
        }

        [Test]
        public void ShowTemperatureMeasurements_ShouldUpdateCorrectly()
        {
            // Arrange
            var uiState = new UiState();
            var expectedValue = false;

            // Act
            uiState.ShowTemperatureMeasurements = expectedValue;

            // Assert
            Assert.AreEqual(expectedValue, uiState.ShowTemperatureMeasurements);
        }

        [Test]
        public void ShowHumidityMeasurements_ShouldUpdateCorrectly()
        {
            // Arrange
            var uiState = new UiState();
            var expectedValue = false;

            // Act
            uiState.ShowHumidityMeasurements = expectedValue;

            // Assert
            Assert.AreEqual(expectedValue, uiState.ShowHumidityMeasurements);
        }
    }
}
