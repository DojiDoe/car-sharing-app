package doji.doe.carsharing.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;

import doji.doe.carsharing.model.Payment;
import doji.doe.carsharing.repository.payment.PaymentRepository;
import doji.doe.carsharing.util.PaymentTestUtil;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = {"classpath:database/clean-up-data.sql",
        "classpath:database/users/insert-users.sql",
        "classpath:database/cars/insert-cars.sql",
        "classpath:database/rentals/insert-rentals.sql",
        "classpath:database/payments/insert-payments.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class PaymentRepositoryTest {
    @Autowired
    private PaymentRepository paymentRepository;

    @Test
    @DisplayName("Find all payments of user by user id")
    public void findAllByRental_User_Id_ValidUserId_ListOfPayment() {
        Long userId = 2L;
        List<Payment> expected = PaymentTestUtil.getListOfPayments();
        List<Payment> actual = paymentRepository.findAllByRental_User_Id(userId);
        assertEquals(2, actual.size());
        for (int i = 0; i < expected.size(); i++) {
            assertTrue(reflectionEquals(actual.get(i), expected.get(i), "rental"));
        }
    }

    @Test
    @DisplayName("Find payment by session id")
    public void findPaymentBySessionId_ValidSessionId_ReturnsPayment() {
        String sessionId = "cs_test_a1ATNr0UKM0uFxvpf1A3hgZMHSKxvLNCCdmSszbYE88z7Dff8vctHLMuqT";
        Optional<Payment> result = paymentRepository.findPaymentBySessionId(sessionId);
        assertTrue(result.isPresent());
        assertEquals(sessionId, result.get().getSessionId());
    }
}
