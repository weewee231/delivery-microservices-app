package dev.weewee.adminservice.external;

import dev.weewee.api.http.order.OrderDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderServiceClient {

    @Qualifier("orderWebClient")
    private final WebClient orderWebClient;

    public List<OrderDto> getAllOrders() {
        try {
            return orderWebClient.get()
                    .uri("/api/orders")
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<OrderDto>>() {})
                    .block();
        } catch (Exception e) {
            log.error("Failed to get orders from order-service: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    public OrderDto getOrderById(Long id) {
        try {
            return orderWebClient.get()
                    .uri("/api/orders/{id}", id)
                    .retrieve()
                    .bodyToMono(OrderDto.class)
                    .block();
        } catch (Exception e) {
            log.error("Failed to get order {} from order-service: {}", id, e.getMessage());
            return null;
        }
    }

    public OrderDto cancelOrder(Long id) {
        try {
            return orderWebClient.put()
                    .uri("/api/orders/{id}/cancel", id)
                    .retrieve()
                    .bodyToMono(OrderDto.class)
                    .block();
        } catch (Exception e) {
            log.error("Failed to cancel order {}: {}", id, e.getMessage());
            return null;
        }
    }
}

