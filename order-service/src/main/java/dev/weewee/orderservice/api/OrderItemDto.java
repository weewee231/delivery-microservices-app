package dev.weewee.orderservice.api;

import dev.weewee.orderservice.domain.OrderItemEntity;

import java.math.BigDecimal;

/**
 * DTO for {@link OrderItemEntity}
 */
public record OrderItemDto(
        Long id,
        Long itemId,
        Integer quantity,
        String name,
        BigDecimal priceAtPurchase) {
}


