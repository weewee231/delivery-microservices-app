package dev.weewee.orderservice.api;

public record OrderItemRequestDto(
        Long itemId,
        Integer quantity,
        String name

) {}
