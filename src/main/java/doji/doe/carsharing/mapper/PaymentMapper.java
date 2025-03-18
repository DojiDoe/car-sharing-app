package doji.doe.carsharing.mapper;

import doji.doe.carsharing.config.MapperConfig;
import doji.doe.carsharing.dto.payment.PaymentDetailedResponseDto;
import doji.doe.carsharing.dto.payment.PaymentResponseDto;
import doji.doe.carsharing.dto.payment.PaymentStatusResponseDto;
import doji.doe.carsharing.model.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface PaymentMapper {

    PaymentResponseDto toDto(Payment payment);

    @Mapping(target = "rentalId", source = "rental.id")
    PaymentDetailedResponseDto toDetailedDto(Payment payment);

    PaymentStatusResponseDto toStatusDto(Payment payment);
}
