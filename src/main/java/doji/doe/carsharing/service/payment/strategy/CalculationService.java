package doji.doe.carsharing.service.payment.strategy;

import doji.doe.carsharing.model.Payment;
import doji.doe.carsharing.model.Rental;
import java.math.BigDecimal;

public interface CalculationService {
    BigDecimal calculateAmount(Rental rental);

    Payment.Type getServiceType();
}
