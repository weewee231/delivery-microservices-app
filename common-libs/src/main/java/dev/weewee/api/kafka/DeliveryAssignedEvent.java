package dev.weewee.api.kafka;

import lombok.Builder;

@Builder
public record DeliveryAssignedEvent(
        Long orderId,
        String courierName,
        Integer etaMinutes
) {
}
