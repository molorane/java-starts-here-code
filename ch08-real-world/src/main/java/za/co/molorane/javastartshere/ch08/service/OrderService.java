package za.co.molorane.javastartshere.ch08.service;

import org.springframework.stereotype.Service;
import za.co.molorane.javastartshere.ch08.api.model.OrderItem;
import za.co.molorane.javastartshere.ch08.api.model.OrderRequest;
import za.co.molorane.javastartshere.ch08.api.model.OrderResponse;
import za.co.molorane.javastartshere.ch08.api.model.OrderStatus;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OrderService {

    private final Map<UUID, OrderResponse> store = new ConcurrentHashMap<>();

    public OrderResponse placeOrder(OrderRequest request) {
        UUID orderId = UUID.randomUUID();
        double total = request.getItems().stream()
                .mapToDouble(i -> i.getUnitPrice() * i.getQuantity())
                .sum();

        OrderResponse response = new OrderResponse();
        response.setOrderId(orderId);
        response.setCustomerId(request.getCustomerId());
        response.setStatus(OrderStatus.CONFIRMED);
        response.setTotalAmount(total);
        response.setCreatedAt(OffsetDateTime.now());
        response.setItems(request.getItems());

        store.put(orderId, response);
        return response;
    }

    public OrderResponse getOrder(UUID orderId) {
        return store.get(orderId);
    }
}
