package za.co.molorane.javastartshere.ch12.adapter;

import za.co.molorane.javastartshere.ch12.application.InvoiceRepositoryPort;
import za.co.molorane.javastartshere.ch12.domain.DomainInvoice;
import za.co.molorane.javastartshere.ch12.domain.InvoiceId;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Driven (outbound) infrastructure adapter implementing the domain port.
 */
public class InMemoryInvoiceRepository implements InvoiceRepositoryPort {

    private final Map<InvoiceId, DomainInvoice> store = new ConcurrentHashMap<>();

    @Override
    public void save(DomainInvoice invoice) {
        store.put(invoice.getId(), invoice);
    }

    @Override
    public Optional<DomainInvoice> findById(InvoiceId id) {
        return Optional.ofNullable(store.get(id));
    }
}
