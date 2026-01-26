package dev.weewee.api.http.admin;

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
public class DashboardStatsDto {
    private OrderStats orderStats;
    private PaymentStats paymentStats;
    private DeliveryStats deliveryStats;
    private UserStats userStats;
    private LocalDateTime generatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderStats {
        private long totalOrders;
        private long pendingPaymentOrders;
        private long paidOrders;
        private long deliveredOrders;
        private long cancelledOrders;
        private BigDecimal totalRevenue;
        private BigDecimal averageOrderValue;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentStats {
        private long totalPayments;
        private long successfulPayments;
        private long failedPayments;
        private BigDecimal totalProcessed;
        private double successRate;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeliveryStats {
        private long totalDeliveries;
        private long activeDeliveries;
        private long completedDeliveries;
        private double averageEtaMinutes;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserStats {
        private long totalUsers;
        private long customers;
        private long couriers;
        private long admins;
        private long newUsersToday;
    }
}

