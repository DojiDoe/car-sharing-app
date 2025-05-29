package doji.doe.carsharing.util;

import doji.doe.carsharing.dto.payment.CreatePaymentRequestDto;
import doji.doe.carsharing.dto.payment.PaymentDetailedResponseDto;
import doji.doe.carsharing.dto.payment.PaymentResponseDto;
import doji.doe.carsharing.model.Payment;
import java.math.BigDecimal;
import java.util.List;

public class PaymentTestUtil {

    public static final String FIRST_PAYMENT_SESSION_URL = "https://checkout.stripe.com/c/pay/cs_test_a1ATNr0UKM0uFxvpf1A3hgZMHSKxvLNCCdmSszbYE88z7Dff8vctHLMuqT#fidkdWxOYHwnPyd1blpxYHZxWjA0Vzc8UHBDTzNNTFF%2FaTVOaXA8aWdmYWpKbW1pSkp1QGA3ZmB8dENidG1wUDJ2QjcydTZrMlFGS0BGcD11ajRAX1BdU0lTTm1EM39SZ3xzSW9zMWNESGhkNTVGbWt0bGhEaScpJ2N3amhWYHdzYHcnP3F3cGApJ2lkfGpwcVF8dWAnPyd2bGtiaWBabHFgaCcpJ2BrZGdpYFVpZGZgbWppYWB3dic%2FcXdwYHgl";
    public static final String FIRST_PAYMENT_SESSION_ID = "cs_test_a1ATNr0UKM0uFxvpf1A3hgZMHSKxvLNC"
            + "CdmSszbYE88z7Dff8vctHLMuqT";
    public static final String SECOND_PAYMENT_SESSION_URL = "https://checkout.stripe.com/c/pay/cs_test_a1fySJU9VFrOxGsVirWo2v4fe9cQBqFd41VSpHydUF3FEdwsP3juDwlQBV#fidkdWxOYHwnPyd1blpxYHZxWjA0Vzc8UHBDTzNNTFF%2FaTVOaXA8aWdmYWpKbW1pSkp1QGA3ZmB8dENidG1wUDJ2QjcydTZrMlFGS0BGcD11ajRAX1BdU0lTTm1EM39SZ3xzSW9zMWNESGhkNTVGbWt0bGhEaScpJ2N3amhWYHdzYHcnP3F3cGApJ2lkfGpwcVF8dWAnPyd2bGtiaWBabHFgaCcpJ2BrZGdpYFVpZGZgbWppYWB3dic%2FcXdwYHgl";
    public static final String SECOND_PAYMENT_SESSION_ID = "cs_test_a1fySJU9VFrOxGsVirWo2v4fe9cQBqF"
            + "d41VSpHydUF3FEdwsP3juDwlQBV";

    public static CreatePaymentRequestDto getCreatePaymentRequestDto() {
        return new CreatePaymentRequestDto()
                .setRentalId(1L)
                .setType(Payment.Type.PAYMENT);
    }

    public static Payment getPayment() {
        return new Payment()
                .setId(1L)
                .setStatus(Payment.Status.PENDING)
                .setType(Payment.Type.PAYMENT)
                .setRental(RentalTestUtil.getRental())
                .setSessionUrl(FIRST_PAYMENT_SESSION_URL)
                .setSessionId(FIRST_PAYMENT_SESSION_ID)
                .setAmountToPay(BigDecimal.valueOf(52));
    }

    public static PaymentResponseDto getPaymentResponseDto(Payment payment) {
        return new PaymentResponseDto()
                .setSessionId(payment.getSessionId())
                .setSessionUrl(payment.getSessionUrl());
    }

    public static PaymentDetailedResponseDto getPaymentDetailedResponseDto(Payment payment) {
        return new PaymentDetailedResponseDto()
                .setId(payment.getId())
                .setStatus(payment.getStatus())
                .setType(payment.getType())
                .setRentalId(payment.getRental().getId())
                .setSessionUrl(payment.getSessionUrl())
                .setSessionId(payment.getSessionId())
                .setAmountToPay(payment.getAmountToPay());
    }

    public static List<Payment> getListOfPayments() {
        return List.of(getPayment(),
                new Payment()
                        .setId(2L)
                        .setStatus(Payment.Status.PENDING)
                        .setType(Payment.Type.PAYMENT)
                        .setRental(RentalTestUtil.getRental().setId(2L))
                        .setSessionUrl(SECOND_PAYMENT_SESSION_URL)
                        .setSessionId(SECOND_PAYMENT_SESSION_ID)
                        .setAmountToPay(BigDecimal.valueOf(67)));
    }

    public static List<PaymentDetailedResponseDto> getListOfPaymentDetailedResponseDto(
            List<Payment> listOfPayments) {
        return listOfPayments.stream()
                .map(payment -> new PaymentDetailedResponseDto()
                        .setId(payment.getId())
                        .setStatus(payment.getStatus())
                        .setType(payment.getType())
                        .setRentalId(payment.getRental().getId())
                        .setSessionUrl(payment.getSessionUrl())
                        .setSessionId(payment.getSessionId())
                        .setAmountToPay(payment.getAmountToPay()))
                .toList();
    }
}
