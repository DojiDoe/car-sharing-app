package doji.doe.carsharing.service.payment.impl;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import doji.doe.carsharing.dto.notification.NotificationType;
import doji.doe.carsharing.dto.payment.CreatePaymentRequestDto;
import doji.doe.carsharing.dto.payment.PaymentDetailedResponseDto;
import doji.doe.carsharing.dto.payment.PaymentResponseDto;
import doji.doe.carsharing.dto.payment.PaymentStatusResponseDto;
import doji.doe.carsharing.exception.EntityNotFoundException;
import doji.doe.carsharing.exception.PaymentException;
import doji.doe.carsharing.mapper.PaymentMapper;
import doji.doe.carsharing.model.Payment;
import doji.doe.carsharing.model.Rental;
import doji.doe.carsharing.model.User;
import doji.doe.carsharing.repository.payment.PaymentRepository;
import doji.doe.carsharing.repository.rental.RentalRepository;
import doji.doe.carsharing.service.notification.NotificationTemplates;
import doji.doe.carsharing.service.notification.TelegramNotificationService;
import doji.doe.carsharing.service.payment.PaymentService;
import doji.doe.carsharing.service.payment.StripeService;
import doji.doe.carsharing.service.payment.strategy.CalculationService;
import doji.doe.carsharing.service.payment.strategy.CalculationServiceFactory;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private static final String COMPLETED_STATUS = "complete";
    @Value("${stripe.secretKey}")
    private String secretKey;
    private final RentalRepository rentalRepository;
    private final PaymentRepository paymentRepository;
    private final StripeService stripeService;
    private final CalculationServiceFactory calculationServiceFactory;
    private final PaymentMapper paymentMapper;
    private final TelegramNotificationService notificationService;

    @Override
    public List<PaymentDetailedResponseDto> getAll(User user, Long id) {
        if (user.getRole().equals(User.Role.ROLE_CUSTOMER)) {
            if (!Objects.equals(user.getId(), id)) {
                throw new AccessDeniedException("This account with id: " + user.getId()
                        + " don't have permission to see payments of other customers");
            }
            return paymentRepository.findAllByRental_User_Id(id).stream()
                    .map(paymentMapper::toDetailedDto)
                    .toList();
        }
        return paymentRepository.findAll().stream()
                .map(paymentMapper::toDetailedDto)
                .toList();
    }

    @Override
    @Transactional
    public PaymentResponseDto createPaymentSession(CreatePaymentRequestDto requestDto) {
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

    @Override
    @Transactional
    public PaymentStatusResponseDto handleSuccess(String sessionId) {
        Payment payment = paymentRepository.findPaymentBySessionId(sessionId).orElseThrow(
                () -> new EntityNotFoundException("Can't find a payment by sessionId: " + sessionId)
        );
        try {
            Session session = Session.retrieve(sessionId);
            if (session.getStatus().equals(COMPLETED_STATUS)) {
                payment.setStatus(Payment.Status.PAID);
                Payment savedPayment = paymentRepository.save(payment);
                String message = NotificationTemplates.getTemplate(NotificationType.PAYMENT_SUCCESS,
                        payment.getId(),
                        payment.getAmountToPay(),
                        payment.getRental().getId());
                notificationService.notifyAdmin(message);
                return paymentMapper.toStatusDto(savedPayment);
            } else {
                throw new PaymentException("Payment checkout session with id: "
                        + sessionId + " is not completed");
            }
        } catch (StripeException e) {
            throw new EntityNotFoundException("Can't find a payment by sessionId: " + sessionId);
        }
    }

    @Override
    @Transactional
    public PaymentStatusResponseDto handleCancel(String sessionId) {
        Payment payment = paymentRepository.findPaymentBySessionId(sessionId).orElseThrow(
                () -> new EntityNotFoundException("Can't find a payment by sessionId: " + sessionId)
        );
        payment.setStatus(Payment.Status.CANCEL);
        Payment savedPayment = paymentRepository.save(payment);
        String message = NotificationTemplates.getTemplate(NotificationType.PAYMENT_FAILED,
                payment.getId(),
                payment.getRental().getId());
        notificationService.notifyAdmin(message);
        return paymentMapper.toStatusDto(savedPayment);
    }

    private static Payment preparePayment(Session session,
                                          CreatePaymentRequestDto requestDto,
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
