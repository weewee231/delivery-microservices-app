package dev.weewee.api.http.order;

import java.math.BigDecimal;

public record OrderItemDto(
        Long id,
        Long itemId,
        Integer quantity,
        String name,
        BigDecimal priceAtPurchase) {
}


