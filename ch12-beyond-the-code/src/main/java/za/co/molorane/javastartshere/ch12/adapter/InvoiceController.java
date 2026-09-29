package za.co.molorane.javastartshere.ch12.adapter;

import za.co.molorane.javastartshere.ch12.domain.DomainInvoice;

/**
 * Adapter calling the domain.
 */
public class InvoiceController {
    public DomainInvoice handleInvoiceRequest(String id, double amount) {
        return new DomainInvoice(id, amount);
    }
}
