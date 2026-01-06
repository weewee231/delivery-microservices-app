package dev.weewee.orderservice.domain;

public enum OrderStatus {
    CREATED,
    CONFIRMED,
    PREPARING,
    READY_FOR_PICKUP,
    IN_DELIVERY,
    DELIVERED,
    CANCELLED,
    PENDING_STATUS

}
