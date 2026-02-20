package stas.thermometer.app;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

public class ArchTest {
    @Test
    void should_respects_architecture() {
        var archRule = layeredArchitecture()
                .consideringAllDependencies()
                .layer("App").definedBy("stas.thermometer.app")
                .layer("Presentations").definedBy("stas.thermometer.presentations..")
                .layer("Views").definedBy("stas.thermometer.views..")
                .layer("Domains").definedBy("stas.thermometer.domains..")
                .layer("Infrastructures").definedBy("stas.thermometer.infrastructures..")

                .whereLayer("Views").mayOnlyBeAccessedByLayers("App")
                .whereLayer("Presentations").mayOnlyBeAccessedByLayers("App","Views")
                .whereLayer("Domains").mayOnlyBeAccessedByLayers("App","Presentations","Infrastructures")
                .whereLayer("Infrastructures").mayOnlyBeAccessedByLayers("App");

        archRule.check(new ClassFileImporter().importPackages("stas.thermometer.."));
    }

    @Test
    void domains_should_not_depend_on_third_parties() {

        var domainRule = classes().that()
                .resideInAPackage("..domains..")
                .should().onlyDependOnClassesThat().resideInAnyPackage(
                        "java..",
                        "stas.thermometer.domains..");

        domainRule.check(new ClassFileImporter().importPackages("stas.thermometer.."));
        domainRule = noClasses().that().resideInAPackage("..domains..").should().dependOnClassesThat().resideInAPackage("java.sql..");
        domainRule.check(new ClassFileImporter().importPackages("stas.thermometer.."));
    }

    @Test
    void infrastructures_should_depend_on_domains() {
        var presentationRule = classes().that()
                .resideInAPackage("..infrastructures..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("java..",
                        "org.apache.logging.log4j..",
                        "stas.thermometer.infrastructures..",
                        "stas.thermometer.domains..");

        presentationRule.check(new ClassFileImporter().importPackages("stas.thermometer.."));
    }

    @Test
    void presentations_should_on_domains() {
        var presentationRule = classes().that()
                .resideInAPackage("..presentations..")
                .should().onlyDependOnClassesThat().resideInAnyPackage(
                        "java..",
                        "stas.thermometer.presentations..",
                        "stas.thermometer.domains..");

        presentationRule.check(new ClassFileImporter().importPackages("stas.thermometer.."));
        presentationRule = noClasses().that().resideInAPackage("..domains..").should().dependOnClassesThat().resideInAPackage("java.sql..");
        presentationRule.check(new ClassFileImporter().importPackages("stas.thermometer.."));
    }

    @Test
    void views_should_depend_on_presentations() {
        var presentationRule = classes().that()
                .resideInAPackage("..views..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("java..",
                        "org.apache.logging.log4j..",
                        "joptsimple",
                        "stas.thermometer.views..",
                        "stas.thermometer.presentations..");

        presentationRule.check(new ClassFileImporter().importPackages("stas.thermometer.."));
    }

}
