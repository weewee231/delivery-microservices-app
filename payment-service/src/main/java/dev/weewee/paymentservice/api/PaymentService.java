package dev.weewee.paymentservice.api;

import dev.weewee.api.http.payment.CreatePaymentRequestDto;
import dev.weewee.api.http.payment.CreatePaymentResponseDto;
import dev.weewee.paymentservice.domain.db.PaymentEntityMapper;
import dev.weewee.paymentservice.domain.db.PaymentEntityRepository;
import dev.weewee.api.http.payment.PaymentMethod;
import dev.weewee.api.http.payment.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class PaymentService {

    private final PaymentEntityMapper mapper;
    private final PaymentEntityRepository repository;


    public CreatePaymentResponseDto makePayment(CreatePaymentRequestDto request) {

        var found = repository.findByOrderId(request.orderId());
        if (found.isPresent()) {
            log.info("Payment already exists for orderId={}", request.orderId());
            return mapper.toResponseDto(found.get());
        }

        var entity = mapper.toEntity(request);

        var status = request.paymentMethod().equals(PaymentMethod.QR)
                ? PaymentStatus.PAYMENT_FAILED
                : PaymentStatus.PAYMENT_SUCCEEDED;

        entity.setPaymentStatus(status);

        var savedEntity = repository.save(entity);
        return mapper.toResponseDto(savedEntity);
    }
}
