package doji.doe.carsharing.service.payment;

import doji.doe.carsharing.dto.payment.CreatePaymentRequestDto;
import doji.doe.carsharing.dto.payment.PaymentDetailedResponseDto;
import doji.doe.carsharing.dto.payment.PaymentResponseDto;
import doji.doe.carsharing.dto.payment.PaymentStatusResponseDto;
import doji.doe.carsharing.model.User;
import java.util.List;

public interface PaymentService {
    List<PaymentDetailedResponseDto> getAll(User user, Long id);

    PaymentResponseDto createPaymentSession(CreatePaymentRequestDto requestDto);

    PaymentStatusResponseDto handleSuccess(String sessionId);

    PaymentStatusResponseDto handleCancel(String sessionId);
}
