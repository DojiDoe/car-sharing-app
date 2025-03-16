package doji.doe.carsharing.mapper;

import doji.doe.carsharing.config.MapperConfig;
import doji.doe.carsharing.dto.payment.PaymentResponseDto;
import doji.doe.carsharing.model.Payment;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface PaymentMapper {

    PaymentResponseDto toDto(Payment payment);

}
