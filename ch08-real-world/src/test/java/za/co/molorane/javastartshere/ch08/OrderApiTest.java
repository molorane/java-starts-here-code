package za.co.molorane.javastartshere.ch08;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import za.co.molorane.javastartshere.ch08.api.OrdersApiDelegate;
import za.co.molorane.javastartshere.ch08.api.model.OrderItem;
import za.co.molorane.javastartshere.ch08.api.model.OrderRequest;
import za.co.molorane.javastartshere.ch08.api.model.OrderResponse;
import za.co.molorane.javastartshere.ch08.api.model.OrderStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisplayName("Chapter 8: Contract-Driven API-First Integration Test")
class OrderApiTest {

    @Autowired
    private OrdersApiDelegate orderApiDelegate;

    @Test
    @DisplayName("Should successfully place an order using OpenAPI generated models and delegate")
    void shouldCreateOrderViaGeneratedContract() {
        OrderItem item = new OrderItem();
        item.setSku("JAVA-STARTS-HERE-BOOK");
        item.setQuantity(2);
        item.setUnitPrice(49.99);

        OrderRequest request = new OrderRequest();
        request.setCustomerId("CUST-1001");
        request.setCurrency("USD");
        request.setItems(List.of(item));

        ResponseEntity<OrderResponse> response = orderApiDelegate.createOrder(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCustomerId()).isEqualTo("CUST-1001");
        assertThat(response.getBody().getStatus()).isEqualTo(OrderStatus.CONFIRMED);
        assertThat(response.getBody().getTotalAmount()).isEqualTo(99.98);
    }
}
