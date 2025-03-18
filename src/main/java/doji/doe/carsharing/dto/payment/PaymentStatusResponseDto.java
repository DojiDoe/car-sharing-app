package doji.doe.carsharing.dto.payment;

import doji.doe.carsharing.model.Payment;

public record PaymentStatusResponseDto(
        String sessionId,
        Payment.Status status
) {
}
