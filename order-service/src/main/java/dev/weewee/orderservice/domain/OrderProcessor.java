package dev.weewee.orderservice.domain;

import dev.weewee.api.http.order.OrderStatus;
import dev.weewee.api.http.order.CreateOrderRequestDto;
import dev.weewee.api.http.payment.CreatePaymentRequestDto;
import dev.weewee.api.http.payment.PaymentStatus;
import dev.weewee.orderservice.api.OrderPaymentRequest;
import dev.weewee.orderservice.domain.db.*;
import dev.weewee.orderservice.external.PaymentHttpClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;

@RequiredArgsConstructor
@Service
public class OrderProcessor {

    private final OrderJpaRepository repository;
    private final OrderEntityMapper orderEntityMapper;
    private final PaymentHttpClient paymentHttpClient;

    public OrderEntity create(CreateOrderRequestDto request) {
        var entity = orderEntityMapper.toEntity(request);
        calculatePricingForOrder(entity);
        entity.setOrderStatus(OrderStatus.PENDING_PAYMENT);
        return repository.save(entity);
    }

    public OrderEntity getOrderOrThrow(Long id) {
        var orderItemEntityOptional = repository.findById(id);
        return orderItemEntityOptional
                .orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id)));
    }

    private void calculatePricingForOrder(OrderEntity entity) {
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (OrderItemEntity item : entity.getItems()) {
            var randomPrice = ThreadLocalRandom.current().nextDouble(100,5000);
            item.setPriceAtPurchase(BigDecimal.valueOf(randomPrice));

            totalPrice = item.getPriceAtPurchase()
                    .multiply(BigDecimal.valueOf(item.getQuantity()))
                    .add(totalPrice);
        }
        entity.setTotalAmount(totalPrice);

    }

    public OrderEntity processPayment(
            Long id,
            OrderPaymentRequest request
    ) {
        var entity = getOrderOrThrow(id);
        if (!entity.getOrderStatus().equals(OrderStatus.PENDING_PAYMENT)) {
            throw new RuntimeException("Order must be in status PENDING_PAYMENT");
        }
        var response = paymentHttpClient.createPayment(CreatePaymentRequestDto.builder()
                .orderId(id)
                .paymentMethod(request.paymentMethod())
                .amount(entity.getTotalAmount())
                .build());

        var status = response.paymentStatus().equals(PaymentStatus.PAYMENT_SUCCEEDED)
                ? OrderStatus.PAID
                : OrderStatus.PAYMENT_FAILED;

        entity.setOrderStatus(status);
        return repository.save(entity);
    }
}
