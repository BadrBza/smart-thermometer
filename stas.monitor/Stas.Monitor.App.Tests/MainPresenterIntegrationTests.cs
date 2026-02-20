using NUnit.Framework;
using Stas.Monitor.Domains;
using Stas.Monitor.Infrastructures;
using Stas.Monitor.Presentations;
using System.Collections.Generic;
using System.IO;
using System.Linq;
/*
[TestFixture]
public class MainPresenterIntegrationTests
{
    private Configuration _configuration;
    private CsvConfigurationReader _csvReader;
    private MainPresenter _presenter;
    private string _happyPathCsv = "happyPath.csv";
    private string _happyPathAlert = "happyPathAlert.csv";
    string _emptyCsv = "empty.csv";
    private List<Thermometer> _thermometers;

    [SetUp]
    public void SetUp()
    {
        _thermometers = new List<Thermometer>
        {
            new Thermometer { Name = "Chambre" },
            new Thermometer { Name = "Cuisine" }
        };

        if (!File.Exists(_happyPathCsv))
        {
            File.WriteAllLines(_happyPathCsv, new[]
            {
                "Name,DateTime,Type,Value",
                "Chambre,2023-10-25 14:00:00,Température,22.5",
                "Cuisine,2023-10-25 14:05:00,Température,24.0"
            });
        }

        if (!File.Exists(_happyPathAlert))
        {
            File.WriteAllLines(_happyPathAlert, new[]
            {
                "Name,DateTime,Expected,Actual",
                "Chambre,2023-10-25 14:00:00,22.0,22.5"
            });
        }


        _csvReader = new CsvConfigurationReader();
    }

    [Test]
    public void HappyPath_UpdateMeasurements_ShouldLoadCorrectEvents()
    {
        _configuration = new Configuration(_happyPathCsv, _thermometers, _happyPathAlert);
        _presenter = new MainPresenter(_configuration, _csvReader);

        _presenter.UpdateMeasurements();

        Assert.AreEqual(2, _presenter.SelectedThermometerEvents.Count);
        Assert.IsTrue(_presenter.SelectedThermometerEvents.Any(e => e is ThermometerMeasure && ((ThermometerMeasure)e).Value == 22.5));
        Assert.IsTrue(_presenter.SelectedThermometerEvents.Any(e => e is ThermometerAlert && ((ThermometerAlert)e).ActualTemperature == 22.5));
    }

    [Test]
    public void DegradedPath_MissingThermometer_ShouldNotLoadDataForMissingThermometer()
    {
        _configuration = new Configuration(_happyPathCsv, _thermometers, _happyPathAlert);
        _presenter = new MainPresenter(_configuration, _csvReader);
        _presenter.UpdateMeasurements();
        var initialCount = _presenter.SelectedThermometerEvents.Count;

        _thermometers.RemoveAll(t => t.Name == "Chambre");
        _presenter = new MainPresenter(new Configuration(_happyPathCsv, _thermometers, _happyPathAlert), _csvReader);
        _presenter.UpdateMeasurements();

        Assert.IsTrue(_presenter.SelectedThermometerEvents.Count < initialCount);
    }




    [TearDown]
    public void CleanUp()
    {
        if (File.Exists(_happyPathCsv))
        {
            File.Delete(_happyPathCsv);
        }

        if (File.Exists(_happyPathAlert))
        {
            File.Delete(_happyPathAlert);
        }
    }

}*/
