package dev.weewee.paymentservice.domain.db;

import dev.weewee.api.http.payment.CreatePaymentRequestDto;
import dev.weewee.api.http.payment.CreatePaymentResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface PaymentEntityMapper {
    PaymentEntity toEntity(CreatePaymentRequestDto request);

    @Mapping(source = "id", target = "paymentId")
    CreatePaymentResponseDto toResponseDto(PaymentEntity savedEntity);

}
