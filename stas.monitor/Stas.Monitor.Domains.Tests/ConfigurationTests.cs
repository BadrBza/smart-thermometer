using NUnit.Framework;
using Stas.Monitor.Domains;
using System;
using System.Collections.Generic;

namespace Stas.Monitor.Domains.Tests
{
    [TestFixture]
    public class ConfigurationTests
    {
        [Test]
        public void Should_CreateConfiguration_WithCorrectValues()
        {
            // Arrange
            var expectedConfigThermometer = new List<Thermometer>
            {
                new Thermometer("Thermo1", "MM/dd/yyyy", "degres", new Dictionary<string, int>{{"Profile1", 1}}),
                new Thermometer("Thermo2", "dd/MM/yyyy", "degres", new Dictionary<string, int>{{"Profile2", 2}})
            };

            // Act
            var configuration = new Configuration(
                expectedConfigThermometer);

            // Assert
            CollectionAssert.AreEqual(expectedConfigThermometer, configuration.ConfigThermometer);
        }
    }
}
