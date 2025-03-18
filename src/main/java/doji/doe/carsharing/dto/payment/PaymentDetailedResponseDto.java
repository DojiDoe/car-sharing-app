package doji.doe.carsharing.dto.payment;

import doji.doe.carsharing.model.Payment;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class PaymentDetailedResponseDto {
    private Long id;
    private Payment.Status status;
    private Payment.Type type;
    private Long rentalId;
    private String sessionUrl;
    private String sessionId;
    private BigDecimal amountToPay;
}
