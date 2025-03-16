package doji.doe.carsharing.service.payment.impl;

import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import doji.doe.carsharing.dto.payment.PaymentRequestDto;
import doji.doe.carsharing.dto.payment.PaymentResponseDto;
import doji.doe.carsharing.exception.EntityNotFoundException;
import doji.doe.carsharing.mapper.PaymentMapper;
import doji.doe.carsharing.model.Payment;
import doji.doe.carsharing.model.Rental;
import doji.doe.carsharing.repository.payment.PaymentRepository;
import doji.doe.carsharing.repository.rental.RentalRepository;
import doji.doe.carsharing.service.payment.PaymentService;
import doji.doe.carsharing.service.payment.StripeService;
import doji.doe.carsharing.service.payment.strategy.CalculationService;
import doji.doe.carsharing.service.payment.strategy.CalculationServiceFactory;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    @Value("${stripe.secretKey}")
    private String secretKey;
    private final RentalRepository rentalRepository;
    private final PaymentRepository paymentRepository;
    private final StripeService stripeService;
    private final CalculationServiceFactory calculationServiceFactory;
    private final PaymentMapper paymentMapper;

    @Override
    @Transactional
    public PaymentResponseDto checkoutRentals(PaymentRequestDto requestDto) {
        Stripe.apiKey = secretKey;
        Rental rental = rentalRepository.findById(requestDto.getRentalId()).orElseThrow(
                () -> new EntityNotFoundException("Can't find a rental by id: "
                        + requestDto.getRentalId())
        );
        CalculationService calculationService = calculationServiceFactory
                .getCalculationService(requestDto.getType());
        BigDecimal amount = calculationService.calculateAmount(rental);
        Session session = stripeService.createSession(amount);
        return paymentMapper.toDto(paymentRepository
                .save(preparePayment(session, requestDto, rental)));
    }

    private static Payment preparePayment(Session session,
                                          PaymentRequestDto requestDto,
                                          Rental rental) {
        return new Payment()
                .setStatus(Payment.Status.PENDING)
                .setType(requestDto.getType())
                .setRental(rental)
                .setSessionUrl(session.getUrl())
                .setSessionId(session.getId())
                .setAmountToPay(BigDecimal.valueOf(session.getAmountTotal()));
    }
}
