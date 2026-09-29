package za.co.molorane.javastartshere.ch12.application;

import za.co.molorane.javastartshere.ch12.domain.DomainInvoice;
import za.co.molorane.javastartshere.ch12.domain.InvoiceId;

import java.util.Objects;

/**
 * Application use case coordinator.
 * Operates purely on domain models and ports.
 */
public class InvoiceService {

    private final InvoiceRepositoryPort repository;

    public InvoiceService(InvoiceRepositoryPort repository) {
        this.repository = Objects.requireNonNull(repository, "repository cannot be null");
    }

    public DomainInvoice createInvoice(String id, double amount) {
        DomainInvoice invoice = new DomainInvoice(new InvoiceId(id), amount);
        repository.save(invoice);
        return invoice;
    }

    public DomainInvoice issueInvoice(String id) {
        InvoiceId invoiceId = new InvoiceId(id);
        DomainInvoice invoice = repository.findById(invoiceId)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found: " + id));
        invoice.issue();
        repository.save(invoice);
        return invoice;
    }
}
