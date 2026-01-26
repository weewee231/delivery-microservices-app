package dev.weewee.adminservice.domain;

import dev.weewee.adminservice.external.OrderServiceClient;
import dev.weewee.adminservice.external.UserServiceClient;
import dev.weewee.api.http.admin.*;
import dev.weewee.api.http.order.OrderDto;
import dev.weewee.api.http.order.OrderStatus;
import dev.weewee.api.http.user.UserDto;
import dev.weewee.api.http.user.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

    private final OrderServiceClient orderServiceClient;
    private final UserServiceClient userServiceClient;

    public DashboardStatsDto getDashboardStats() {
        log.info("Generating dashboard stats");

        var orders = orderServiceClient.getAllOrders();
        var users = userServiceClient.getAllUsers();

        return DashboardStatsDto.builder()
                .orderStats(calculateOrderStats(orders))
                .paymentStats(calculatePaymentStats(orders))
                .deliveryStats(calculateDeliveryStats(orders))
                .userStats(calculateUserStats(users))
                .generatedAt(LocalDateTime.now())
                .build();
    }

    public PageResponseDto<AdminOrderDto> getOrders(int page, int size, OrderStatus status, String sortBy) {
        log.info("Getting orders: page={}, size={}, status={}, sortBy={}", page, size, status, sortBy);

        var allOrders = orderServiceClient.getAllOrders();
        var users = userServiceClient.getAllUsers();
        var userMap = users.stream().collect(Collectors.toMap(UserDto::getId, u -> u));

        var filteredOrders = status != null
                ? allOrders.stream().filter(o -> o.orderStatus() == status).toList()
                : allOrders;

        var sortedOrders = sortOrders(filteredOrders, sortBy);

        int totalElements = sortedOrders.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        int start = Math.min(page * size, totalElements);
        int end = Math.min(start + size, totalElements);

        var pageContent = sortedOrders.subList(start, end).stream()
                .map(order -> mapToAdminOrder(order, userMap.get(order.customerId())))
                .toList();

        return PageResponseDto.<AdminOrderDto>builder()
                .content(pageContent)
                .page(page)
                .size(size)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .first(page == 0)
                .last(page >= totalPages - 1)
                .build();
    }

    public PageResponseDto<AdminUserDto> getUsers(int page, int size, UserRole role) {
        log.info("Getting users: page={}, size={}, role={}", page, size, role);

        var allUsers = userServiceClient.getAllUsers();
        var orders = orderServiceClient.getAllOrders();
        var ordersByCustomer = orders.stream()
                .collect(Collectors.groupingBy(OrderDto::customerId));

        var filteredUsers = role != null
                ? allUsers.stream().filter(u -> u.getRole() == role).toList()
                : allUsers;

        int totalElements = filteredUsers.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        int start = Math.min(page * size, totalElements);
        int end = Math.min(start + size, totalElements);

        var pageContent = filteredUsers.subList(start, end).stream()
                .map(user -> mapToAdminUser(user, ordersByCustomer.getOrDefault(user.getId(), List.of())))
                .toList();

        return PageResponseDto.<AdminUserDto>builder()
                .content(pageContent)
                .page(page)
                .size(size)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .first(page == 0)
                .last(page >= totalPages - 1)
                .build();
    }

    public OrderReportDto getOrderReport(LocalDateTime from, LocalDateTime to) {
        log.info("Generating order report: from={}, to={}", from, to);

        var orders = orderServiceClient.getAllOrders();

        var ordersByStatus = orders.stream()
                .collect(Collectors.groupingBy(OrderDto::orderStatus));

        var statusStats = ordersByStatus.entrySet().stream()
                .map(entry -> OrderReportDto.OrdersByStatusDto.builder()
                        .status(entry.getKey())
                        .count(entry.getValue().size())
                        .revenue(entry.getValue().stream()
                                .map(OrderDto::totalAmount)
                                .reduce(BigDecimal.ZERO, BigDecimal::add))
                        .build())
                .toList();

        var totalRevenue = orders.stream()
                .map(OrderDto::totalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return OrderReportDto.builder()
                .periodStart(from)
                .periodEnd(to)
                .totalOrders(orders.size())
                .totalRevenue(totalRevenue)
                .ordersByStatus(statusStats)
                .dailyOrders(List.of())
                .build();
    }

    public AdminOrderDto cancelOrder(Long orderId) {
        log.info("Cancelling order: id={}", orderId);
        var order = orderServiceClient.cancelOrder(orderId);
        if (order != null) {
            var user = userServiceClient.getUserById(order.customerId());
            return mapToAdminOrder(order, user);
        }
        return null;
    }

    public void deleteUser(Long userId) {
        log.info("Deleting user: id={}", userId);
        userServiceClient.deleteUser(userId);
    }

    private DashboardStatsDto.OrderStats calculateOrderStats(List<OrderDto> orders) {
        var totalRevenue = orders.stream()
                .filter(o -> o.orderStatus() == OrderStatus.PAID || o.orderStatus() == OrderStatus.DELIVERED)
                .map(OrderDto::totalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        var avgOrderValue = orders.isEmpty() ? BigDecimal.ZERO
                : totalRevenue.divide(BigDecimal.valueOf(Math.max(orders.size(), 1)), 2, RoundingMode.HALF_UP);

        return DashboardStatsDto.OrderStats.builder()
                .totalOrders(orders.size())
                .pendingPaymentOrders(countByStatus(orders, OrderStatus.PENDING_PAYMENT))
                .paidOrders(countByStatus(orders, OrderStatus.PAID))
                .deliveredOrders(countByStatus(orders, OrderStatus.DELIVERED))
                .cancelledOrders(countByStatus(orders, OrderStatus.CANCELLED))
                .totalRevenue(totalRevenue)
                .averageOrderValue(avgOrderValue)
                .build();
    }

    private DashboardStatsDto.PaymentStats calculatePaymentStats(List<OrderDto> orders) {
        long successful = countByStatus(orders, OrderStatus.PAID) + countByStatus(orders, OrderStatus.DELIVERED);
        long failed = countByStatus(orders, OrderStatus.PAYMENT_FAILED);
        long total = successful + failed;

        var totalProcessed = orders.stream()
                .filter(o -> o.orderStatus() == OrderStatus.PAID || o.orderStatus() == OrderStatus.DELIVERED)
                .map(OrderDto::totalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return DashboardStatsDto.PaymentStats.builder()
                .totalPayments(total)
                .successfulPayments(successful)
                .failedPayments(failed)
                .totalProcessed(totalProcessed)
                .successRate(total > 0 ? (double) successful / total * 100 : 0)
                .build();
    }

    private DashboardStatsDto.DeliveryStats calculateDeliveryStats(List<OrderDto> orders) {
        var deliveryOrders = orders.stream()
                .filter(o -> o.orderStatus() == OrderStatus.DELIVERY_ASSIGNED || o.orderStatus() == OrderStatus.DELIVERED)
                .toList();

        var avgEta = deliveryOrders.stream()
                .filter(o -> o.etaMinutes() != null)
                .mapToInt(OrderDto::etaMinutes)
                .average()
                .orElse(0);

        return DashboardStatsDto.DeliveryStats.builder()
                .totalDeliveries(deliveryOrders.size())
                .activeDeliveries(countByStatus(orders, OrderStatus.DELIVERY_ASSIGNED))
                .completedDeliveries(countByStatus(orders, OrderStatus.DELIVERED))
                .averageEtaMinutes(avgEta)
                .build();
    }

    private DashboardStatsDto.UserStats calculateUserStats(List<UserDto> users) {
        return DashboardStatsDto.UserStats.builder()
                .totalUsers(users.size())
                .customers(users.stream().filter(u -> u.getRole() == UserRole.CUSTOMER).count())
                .couriers(users.stream().filter(u -> u.getRole() == UserRole.COURIER).count())
                .admins(users.stream().filter(u -> u.getRole() == UserRole.ADMIN).count())
                .newUsersToday(0)
                .build();
    }

    private long countByStatus(List<OrderDto> orders, OrderStatus status) {
        return orders.stream().filter(o -> o.orderStatus() == status).count();
    }

    private List<OrderDto> sortOrders(List<OrderDto> orders, String sortBy) {
        if (sortBy == null) return orders;

        return switch (sortBy) {
            case "amount_desc" -> orders.stream()
                    .sorted(Comparator.comparing(OrderDto::totalAmount).reversed())
                    .toList();
            case "amount_asc" -> orders.stream()
                    .sorted(Comparator.comparing(OrderDto::totalAmount))
                    .toList();
            case "id_desc" -> orders.stream()
                    .sorted(Comparator.comparing(OrderDto::id).reversed())
                    .toList();
            default -> orders;
        };
    }

    private AdminOrderDto mapToAdminOrder(OrderDto order, UserDto user) {
        return AdminOrderDto.builder()
                .id(order.id())
                .customerId(order.customerId())
                .customerEmail(user != null ? user.getEmail() : null)
                .customerName(user != null ? user.getFirstName() + " " + user.getLastName() : null)
                .address(order.address())
                .totalAmount(order.totalAmount())
                .status(order.orderStatus())
                .courierName(order.courierName())
                .etaMinutes(order.etaMinutes())
                .build();
    }

    private AdminUserDto mapToAdminUser(UserDto user, List<OrderDto> userOrders) {
        return AdminUserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .role(user.getRole())
                .blocked(false)
                .ordersCount(userOrders.size())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
