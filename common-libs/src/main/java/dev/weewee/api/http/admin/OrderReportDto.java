package dev.weewee.api.http.admin;

import dev.weewee.api.http.order.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderReportDto {
    private LocalDateTime periodStart;
    private LocalDateTime periodEnd;
    private long totalOrders;
    private BigDecimal totalRevenue;
    private List<OrdersByStatusDto> ordersByStatus;
    private List<DailyOrdersDto> dailyOrders;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrdersByStatusDto {
        private OrderStatus status;
        private long count;
        private BigDecimal revenue;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyOrdersDto {
        private String date;
        private long count;
        private BigDecimal revenue;
    }
}

