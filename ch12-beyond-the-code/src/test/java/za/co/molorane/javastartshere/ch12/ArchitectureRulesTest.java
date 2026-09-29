package za.co.molorane.javastartshere.ch12;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

@DisplayName("Chapter 12: Architectural Invariant Defense Tests")
class ArchitectureRulesTest {

    private static JavaClasses importedClasses;

    @BeforeAll
    static void init() {
        importedClasses = new ClassFileImporter().importPackages("za.co.molorane.javastartshere.ch12");
    }

    @Test
    @DisplayName("Domain models must NEVER depend on outer adapters or application services")
    void domainMustNotDependOnOuterLayers() {
        noClasses().that().resideInAPackage("..domain..")
                .should().dependOnClassesThat().resideInAnyPackage("..adapter..", "..application..")
                .check(importedClasses);
    }

    @Test
    @DisplayName("All packages must be completely free of cyclic dependencies")
    void packagesMustBeFreeOfCycles() {
        slices().matching("za.co.molorane.javastartshere.ch12.(*)..")
                .should().beFreeOfCycles()
                .check(importedClasses);
    }

    @Test
    @DisplayName("Controllers must reside in adapter packages and never in domain or application")
    void controllersMustResideInAdapter() {
        classes().that().haveSimpleNameEndingWith("Controller")
                .should().resideInAPackage("..adapter..")
                .check(importedClasses);
    }

    @Test
    @DisplayName("Services must reside in application packages")
    void servicesMustResideInApplication() {
        classes().that().haveSimpleNameEndingWith("Service")
                .should().resideInAPackage("..application..")
                .check(importedClasses);
    }

    @Test
    @DisplayName("Layered Architecture: Adapter -> Application -> Domain")
    void enforceLayeredArchitecture() {
        layeredArchitecture()
                .consideringOnlyDependenciesInLayers()
                .layer("Adapter").definedBy("..adapter..")
                .layer("Application").definedBy("..application..")
                .layer("Domain").definedBy("..domain..")
                .whereLayer("Adapter").mayNotBeAccessedByAnyLayer()
                .whereLayer("Application").mayOnlyBeAccessedByLayers("Adapter")
                .whereLayer("Domain").mayOnlyBeAccessedByLayers("Application", "Adapter")
                .check(importedClasses);
    }
}
