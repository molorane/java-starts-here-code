package za.co.molorane.javastartshere.ch12.adapter;

import za.co.molorane.javastartshere.ch12.application.InvoiceService;
import za.co.molorane.javastartshere.ch12.domain.DomainInvoice;

import java.util.Objects;

/**
 * Driving (inbound) adapter calling the application layer.
 * Adapters depend on Application, never the other way around.
 */
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = Objects.requireNonNull(invoiceService, "invoiceService cannot be null");
    }

    public DomainInvoice handleInvoiceCreation(String id, double amount) {
        return invoiceService.createInvoice(id, amount);
    }
}
