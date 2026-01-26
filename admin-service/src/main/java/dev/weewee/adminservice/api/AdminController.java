package dev.weewee.adminservice.api;

import dev.weewee.adminservice.domain.AdminService;
import dev.weewee.api.http.admin.*;
import dev.weewee.api.http.order.OrderStatus;
import dev.weewee.api.http.user.UserRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
@Tag(name = "Admin", description = "Admin API for managing the platform")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/dashboard")
    @Operation(summary = "Get dashboard statistics")
    public DashboardStatsDto getDashboardStats() {
        log.info("Getting dashboard stats");
        return adminService.getDashboardStats();
    }

    @GetMapping("/orders")
    @Operation(summary = "Get orders with pagination and filters")
    public PageResponseDto<AdminOrderDto> getOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) String sortBy) {
        return adminService.getOrders(page, size, status, sortBy);
    }

    @PutMapping("/orders/{id}/cancel")
    @Operation(summary = "Cancel an order")
    public AdminOrderDto cancelOrder(@PathVariable Long id) {
        log.info("Cancelling order: id={}", id);
        return adminService.cancelOrder(id);
    }

    @GetMapping("/users")
    @Operation(summary = "Get users with pagination and filters")
    public PageResponseDto<AdminUserDto> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) UserRole role) {
        return adminService.getUsers(page, size, role);
    }

    @DeleteMapping("/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a user")
    public void deleteUser(@PathVariable Long id) {
        log.info("Deleting user: id={}", id);
        adminService.deleteUser(id);
    }

    @GetMapping("/reports/orders")
    @Operation(summary = "Get order report for a period")
    public OrderReportDto getOrderReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        log.info("Getting order report: from={}, to={}", from, to);
        return adminService.getOrderReport(from, to);
    }
}

