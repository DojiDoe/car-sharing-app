package doji.doe.carsharing.service.payment;

import doji.doe.carsharing.dto.payment.PaymentRequestDto;
import doji.doe.carsharing.dto.payment.PaymentResponseDto;

public interface PaymentService {
    PaymentResponseDto checkoutRentals(PaymentRequestDto requestDto);
}
