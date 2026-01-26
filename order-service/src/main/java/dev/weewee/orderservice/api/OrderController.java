package dev.weewee.orderservice.api;

import dev.weewee.api.http.order.CreateOrderRequestDto;
import dev.weewee.api.http.order.OrderDto;
import dev.weewee.orderservice.domain.OrderProcessor;
import dev.weewee.orderservice.domain.db.OrderEntityMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderProcessor orderProcessor;
    private final OrderEntityMapper orderEntityMapper;

    @GetMapping
    public List<OrderDto> getAll() {
        return orderProcessor.getAllOrders().stream()
                .map(orderEntityMapper::toOrderDto)
                .toList();
    }

    @PostMapping
    public OrderDto create(@RequestBody CreateOrderRequestDto request) {
        log.info("Creating order: request={}", request);
        var saved = orderProcessor.create(request);
        return orderEntityMapper.toOrderDto(saved);
    }

    @GetMapping("/{id}")
    public OrderDto getOne(@PathVariable Long id) {
        var found = orderProcessor.getOrderOrThrow(id);
        return orderEntityMapper.toOrderDto(found);
    }

    @PostMapping("/{id}/pay")
    public OrderDto payOrder(
            @PathVariable Long id,
            @RequestBody OrderPaymentRequest request) {
        log.info("Paying order with id={}, request={}", id, request);
        var entity = orderProcessor.processPayment(id, request);
        return orderEntityMapper.toOrderDto(entity);
    }

    @PutMapping("/{id}/cancel")
    public OrderDto cancelOrder(@PathVariable Long id) {
        log.info("Cancelling order with id={}", id);
        var entity = orderProcessor.cancelOrder(id);
        return orderEntityMapper.toOrderDto(entity);
    }
}