package za.co.molorane.javastartshere.ch12;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import za.co.molorane.javastartshere.ch12.adapter.InvoiceController;
import za.co.molorane.javastartshere.ch12.domain.DomainInvoice;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@DisplayName("Chapter 12: Architectural Invariant Defense Tests")
class ArchitectureRulesTest {

    @Test
    @DisplayName("Domain models must NEVER depend on outer web or persistence adapters")
    void domainMustNotDependOnAdapters() {
        JavaClasses classes = new ClassFileImporter().importPackages("za.co.molorane.javastartshere.ch12");

        noClasses().that().resideInAPackage("..domain..")
                .should().dependOnClassesThat().resideInAPackage("..adapter..")
                .check(classes);
    }
}
