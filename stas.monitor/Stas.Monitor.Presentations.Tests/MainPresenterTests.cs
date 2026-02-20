using Moq;
using Stas.Monitor.Domains;
using Stas.Monitor.Infrastructures;
using System.Data;

namespace Stas.Monitor.Presentations.Tests
{
    [TestFixture]
    public class MainPresenterTests
    {
        private Mock<IDatabaseConnectionFactory> _mockConnectionFactory;
        private Mock<IUiState> _mockUiState;
        private Mock<IMeasurementDataHolder> _mockDataHolder;
        private MainPresenter _presenter;
        private Configuration _configuration;

        [SetUp]
        public void Setup()
        {
            _mockConnectionFactory = new Mock<IDatabaseConnectionFactory>();
            _mockUiState = new Mock<IUiState>();
            _mockDataHolder = new Mock<IMeasurementDataHolder>();

            var thermometers = new List<Thermometer> { new Thermometer("Thermo1", "MM/dd/yyyy", "Celsius", new Dictionary<string, int> { { "Profile1", 1 } }) };
            _configuration = new Configuration(thermometers);

            _presenter = new MainPresenter(_configuration, _mockConnectionFactory.Object, _mockUiState.Object, _mockDataHolder.Object);
        }

        [Test]
        public void Constructor_ShouldInitializeWithFirstThermometer()
        {
            _mockUiState.VerifySet(ui => ui.SelectedThermometerName = "Thermo1", Times.Once);
        }

        [Test]
        public void Setting_SelectedThermometerName_ShouldTriggerUpdateMeasurements()
        {
            string newThermometerName = "Thermo2";
            _presenter.SelectedThermometerName = newThermometerName;

            _mockUiState.VerifySet(ui => ui.SelectedThermometerName = newThermometerName, Times.Once);
        }

        [Test]
        public void Toggling_ShowTemperatureMeasurements_ShouldTriggerVisibilityToggle()
        {
            _presenter.ShowTemperatureMeasurements = true;
            _mockDataHolder.Verify(m => m.ClearTemperatureEvents(), Times.Once);
        }

        [Test]
        public void Toggling_ShowHumidityMeasurements_ShouldTriggerVisibilityToggle()
        {
            _presenter.ShowHumidityMeasurements = true;
            _mockDataHolder.Verify(m => m.ClearHumidityEvents(), Times.Once);
        }

        [Test]
        public void UpdateMeasurements_ShouldFetchTemperatureData()
        {
            Mock<IDbConnection> mockConnection = new Mock<IDbConnection>();
            _mockConnectionFactory.Setup(f => f.CreateConnection()).Returns(mockConnection.Object);

            _presenter.UpdateMeasurements();

            mockConnection.Verify(conn => conn.Open(), Times.Once);
        }

        [Test]
        public void UpdateMeasurementsHumidity_ShouldFetchHumidityData()
        {
            Mock<IDbConnection> mockConnection = new Mock<IDbConnection>();
            _mockConnectionFactory.Setup(f => f.CreateConnection()).Returns(mockConnection.Object);

            _presenter.UpdateMeasurementsHumidity();

            mockConnection.Verify(conn => conn.Open(), Times.Once);
        }

        [Test]
        public void Changing_SelectedDurationTime_ShouldUpdateMeasurements()
        {
            TimeSpan newDuration = TimeSpan.FromMinutes(5);
            _presenter.SelectedDurationTime = newDuration;

            Assert.AreEqual(newDuration, _presenter.SelectedDuration);

        }

        public void Changing_SelectedDurationTime_ShouldNotUpdateMeasurements()
        {
            TimeSpan newDuration = TimeSpan.FromMinutes(-1);
            _presenter.SelectedDurationTime = newDuration;

            Assert.AreNotEqual(newDuration, _presenter.SelectedDuration);
        }

    }
}
