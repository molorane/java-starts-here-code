package za.co.molorane.javastartshere.ch08.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import za.co.molorane.javastartshere.ch08.api.OrdersApiDelegate;
import za.co.molorane.javastartshere.ch08.api.model.OrderRequest;
import za.co.molorane.javastartshere.ch08.api.model.OrderResponse;
import za.co.molorane.javastartshere.ch08.service.OrderService;

import java.util.UUID;

/**
 * Implementation of the OpenAPI-generated OrdersApiDelegate interface.
 * Demonstrates the API-First (contract-driven) pattern:
 * - OpenAPI specification generates the HTTP interface and DTOs.
 * - Business logic is decoupled from transport metadata.
 */
@Component
public class OrderApiDelegateImpl implements OrdersApiDelegate {

    private final OrderService orderService;

    public OrderApiDelegateImpl(OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    public ResponseEntity<OrderResponse> createOrder(OrderRequest orderRequest) {
        OrderResponse response = orderService.placeOrder(orderRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<OrderResponse> getOrderById(UUID orderId) {
        OrderResponse order = orderService.getOrder(orderId);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(order);
    }
}
