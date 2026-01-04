package dev.weewee.orderservice.api;

import dev.weewee.orderservice.domain.OrderEntity;
import dev.weewee.orderservice.domain.OrderStatus;

import java.math.BigDecimal;
import java.util.Set;

/**
 * DTO for {@link OrderEntity}
 */
public record OrderDto(
        Long id,
        Long customerId,
        String address,
        BigDecimal totalAmount,
        String courierName,
        Integer etaMinutes,
        OrderStatus orderStatus,
        Set<OrderItemDto> items) {
}