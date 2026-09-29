package za.co.molorane.javastartshere.ch12.application;

import za.co.molorane.javastartshere.ch12.domain.DomainInvoice;
import za.co.molorane.javastartshere.ch12.domain.InvoiceId;

import java.util.Optional;

/**
 * Driven (outbound) persistence port.
 * Allows application services to interact with persistence abstractions without coupling to databases.
 */
public interface InvoiceRepositoryPort {
    void save(DomainInvoice invoice);
    Optional<DomainInvoice> findById(InvoiceId id);
}
