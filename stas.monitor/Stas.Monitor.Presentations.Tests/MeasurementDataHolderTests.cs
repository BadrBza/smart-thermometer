using NUnit.Framework;
using Stas.Monitor.Domains;
using Stas.Monitor.Presentations;
using System;

namespace Stas.Monitor.Presentations.Tests
{
    [TestFixture]
    public class MeasurementDataHolderTests
    {
        [Test]
        public void Should_AddTemperatureEvent_Correctly()
        {
            // Arrange
            var dataHolder = new MeasurementDataHolder();
            var tempEvent = new CombinedTemperatureDto("Thermo1", 25.0, DateTime.Now, 0.5);

            // Act
            dataHolder.AddTemperatureEvent(tempEvent);

            // Assert
            Assert.AreEqual(1, dataHolder.SelectedTemperatureEvents.Count);
            Assert.IsTrue(dataHolder.SelectedTemperatureEvents.Contains(tempEvent));
        }

        [Test]
        public void Should_ClearTemperatureEvents_Correctly()
        {
            // Arrange
            var dataHolder = new MeasurementDataHolder();
            dataHolder.AddTemperatureEvent(new CombinedTemperatureDto("Thermo1", 25.0, DateTime.Now, 0.5));

            // Act
            dataHolder.ClearTemperatureEvents();

            // Assert
            Assert.IsEmpty(dataHolder.SelectedTemperatureEvents);
        }

        [Test]
        public void Should_AddHumidityEvent_Correctly()
        {
            // Arrange
            var dataHolder = new MeasurementDataHolder();
            var humidityEvent = new CombinedHumidityDto("Thermo1", 50.0, DateTime.Now, 0.5);

            // Act
            dataHolder.AddHumidityEvent(humidityEvent);

            // Assert
            Assert.AreEqual(1, dataHolder.SelectedHumidityEvents.Count);
            Assert.IsTrue(dataHolder.SelectedHumidityEvents.Contains(humidityEvent));
        }

        [Test]
        public void Should_ClearHumidityEvents_Correctly()
        {
            // Arrange
            var dataHolder = new MeasurementDataHolder();
            dataHolder.AddHumidityEvent(new CombinedHumidityDto("Thermo1", 50.0, DateTime.Now, 0.5));

            // Act
            dataHolder.ClearHumidityEvents();

            // Assert
            Assert.IsEmpty(dataHolder.SelectedHumidityEvents);
        }
    }
}
