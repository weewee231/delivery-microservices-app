package dev.weewee.api.http.admin;

import dev.weewee.api.http.order.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminOrderDto {
    private Long id;
    private Long customerId;
    private String customerEmail;
    private String customerName;
    private String address;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private String courierName;
    private Integer etaMinutes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

