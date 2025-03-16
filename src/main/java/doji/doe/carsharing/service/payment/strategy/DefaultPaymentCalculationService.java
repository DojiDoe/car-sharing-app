package doji.doe.carsharing.service.payment.strategy;

import doji.doe.carsharing.model.Payment;
import doji.doe.carsharing.model.Rental;
import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import org.springframework.stereotype.Service;

@Service
public class DefaultPaymentCalculationService implements CalculationService {

    @Override
    public BigDecimal calculateAmount(Rental rental) {
        long rentalDays = ChronoUnit.DAYS.between(rental.getRentalDate(), rental.getReturnDate());
        return rental.getCar().getDailyFee().multiply(BigDecimal.valueOf(rentalDays));
    }

    @Override
    public Payment.Type getServiceType() {
        return Payment.Type.PAYMENT;
    }
}
