package dev.weewee.orderservice.api;

import dev.weewee.api.http.payment.PaymentMethod;

public record OrderPaymentRequest(
        PaymentMethod paymentMethod
) {
}
