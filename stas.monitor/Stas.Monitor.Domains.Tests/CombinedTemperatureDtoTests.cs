using NUnit.Framework;
using Stas.Monitor.Domains;
using System;

namespace Stas.Monitor.Domains.Tests
{
    [TestFixture]
    public class CombinedTemperatureDtoTests
    {
        [Test]
        public void Should_CreateCombinedTemperatureDto_WithCorrectValues()
        {
            // Arrange
            string expectedThermometerName = "Thermo1";
            double expectedTemperature = 25.0;
            DateTime expectedTimestamp = DateTime.Now;
            double? expectedDifference = 2.0;

            // Act
            var combinedTemperatureDto = new CombinedTemperatureDto(
                expectedThermometerName,
                expectedTemperature,
                expectedTimestamp,
                expectedDifference);

            // Assert
            Assert.AreEqual(expectedThermometerName, combinedTemperatureDto.ThermometerName);
            Assert.AreEqual(expectedTemperature, combinedTemperatureDto.Temperature);
            Assert.AreEqual(expectedTimestamp, combinedTemperatureDto.Timestamp);
            Assert.AreEqual(expectedDifference, combinedTemperatureDto.Difference);
        }
    }
}
