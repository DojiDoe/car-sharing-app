package doji.doe.carsharing.repository.payment;

import doji.doe.carsharing.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
