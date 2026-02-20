using System.Reflection;
using NetArchTest.Rules;
using Stas.Monitor.Domains;
using Stas.Monitor.Infrastructures;
using Stas.Monitor.Presentations;
using Stas.Monitor.Views;

namespace Stas.Monitor.App.Tests;

public class ArchTests
{
  private static readonly Assembly[] Assemblies = new[]
  {
    typeof(App).Assembly,
    typeof(Configuration).Assembly, 
    typeof(IniConfigurationReader).Assembly,
    typeof(MainPresenter).Assembly,
    typeof(MainWindow).Assembly
  };


  [Test]
  public void Views_Should_Depend_On_Presentation()
  {
    Assert.That(Types.InAssemblies(Assemblies)
      .That()
      .ResideInNamespace("Stas.Monitor.Views")
      .ShouldNot()
      .HaveDependencyOnAny("Stas.Monitor.Domains", "Stas.Monitor.Infrastructures",
        "Stas.Monitor.App")
      .GetResult()
      .IsSuccessful, Is.True);
  }


  [Test]
  public void Infrastructures_Should_Depends_On_Domains()
  {
    Assert.That(Types.InAssemblies(Assemblies)
      .That()
      .ResideInNamespace("Stas.Monitor.Infrastructures")
      .ShouldNot()
      .HaveDependencyOnAny("Stas.Monitor.Presenters", "Stas.Monitor.Views", "Stas.Monitor.App")
      .GetResult()
      .IsSuccessful, Is.True);
  }


  [Test]
  public void Presentations_Should_Depend_Only_On_Domains()
  {
    Assert.That(Types.InAssemblies(Assemblies)
      .That()
      .ResideInNamespace("Stas.Monitor.Presenters")
      .ShouldNot()
      .HaveDependencyOnAny("Stas.Monitor.Infrastructures", "Stas.Monitor.Views", "Stas.Monitor.App")
      .GetResult()
      .IsSuccessful, Is.True);
  }


  [Test]
  public void Domain_Should_Not_Depend_On_Other_Assemblies()
  {
    Assert.That(Types.InAssemblies(Assemblies)
      .That()
      .ResideInNamespace("Stas.Monitor.Domains")
      .ShouldNot()
      .HaveDependencyOnAny("Stas.Monitor.Presenters", "Stas.Monitor.Views", "Stas.Monitor.App",
        "Stas.Monitor.Infrastructures")
      .GetResult()
      .IsSuccessful, Is.True);
  }
}