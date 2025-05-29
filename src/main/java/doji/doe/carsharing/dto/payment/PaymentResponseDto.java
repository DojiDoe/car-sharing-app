package doji.doe.carsharing.dto.payment;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PaymentResponseDto {
    private String sessionId;
    private String sessionUrl;
}
