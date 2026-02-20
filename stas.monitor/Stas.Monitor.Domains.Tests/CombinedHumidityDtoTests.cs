namespace Stas.Monitor.Domains.Tests;

using NUnit.Framework;
using Stas.Monitor.Domains;
using System;



    [TestFixture]
    public class CombinedHumidityDtoTests
    {
        [Test]
        public void Should_CreateCombinedHumidityDto_WithCorrectValues()
        {
            // Arrange
            string expectedThermometerName = "Thermo1";
            double expectedHumidity = 50.0;
            DateTime expectedTimestamp = DateTime.Now;
            double? expectedAlertDifference = 5.0;

            // Act
            var combinedHumidityDto = new CombinedHumidityDto(
                expectedThermometerName,
                expectedHumidity,
                expectedTimestamp,
                expectedAlertDifference);

            // Assert
            Assert.AreEqual(expectedThermometerName, combinedHumidityDto.ThermometerName);
            Assert.AreEqual(expectedHumidity, combinedHumidityDto.Humidity);
            Assert.AreEqual(expectedTimestamp, combinedHumidityDto.Timestamp);
            Assert.AreEqual(expectedAlertDifference, combinedHumidityDto.AlertDifference);
        }
    }

