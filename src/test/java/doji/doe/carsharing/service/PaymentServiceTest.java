package doji.doe.carsharing.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.stripe.model.checkout.Session;
import doji.doe.carsharing.dto.payment.CreatePaymentRequestDto;
import doji.doe.carsharing.dto.payment.PaymentDetailedResponseDto;
import doji.doe.carsharing.dto.payment.PaymentResponseDto;
import doji.doe.carsharing.dto.payment.PaymentStatusResponseDto;
import doji.doe.carsharing.exception.EntityNotFoundException;
import doji.doe.carsharing.mapper.PaymentMapper;
import doji.doe.carsharing.model.Payment;
import doji.doe.carsharing.model.Rental;
import doji.doe.carsharing.model.User;
import doji.doe.carsharing.repository.payment.PaymentRepository;
import doji.doe.carsharing.repository.rental.RentalRepository;
import doji.doe.carsharing.service.notification.TelegramNotificationService;
import doji.doe.carsharing.service.payment.StripeService;
import doji.doe.carsharing.service.payment.impl.PaymentServiceImpl;
import doji.doe.carsharing.service.payment.strategy.CalculationServiceFactory;
import doji.doe.carsharing.service.payment.strategy.DefaultPaymentCalculationService;
import doji.doe.carsharing.util.PaymentTestUtil;
import doji.doe.carsharing.util.SessionTestUtil;
import doji.doe.carsharing.util.UserTestUtil;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {
    public static final String RENTAL_ENTITY_NOT_FOUND_EXCEPTION_MESSAGE
            = "Can't find a rental by id: ";
    public static final String PAYMENT_ENTITY_NOT_FOUND_EXCEPTION_MESSAGE
            = "Can't find a payment by sessionId: ";
    @Mock
    private RentalRepository rentalRepository;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private StripeService stripeService;
    @Mock
    private CalculationServiceFactory calculationServiceFactory;
    @Mock
    private PaymentMapper paymentMapper;
    @Mock
    private TelegramNotificationService notificationService;
    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Test
    @DisplayName("Verify whether EntityNotFoundException is thrown when calling"
            + " createPaymentSession() method with invalid rental id")
    public void createPaymentSession_InvalidRentalId_ShouldThrowEntityNotFoundException() {
        // Given
        Long invalidId = 100L;
        CreatePaymentRequestDto requestDto = PaymentTestUtil.getCreatePaymentRequestDto();
        requestDto.setRentalId(invalidId);
        when(rentalRepository.findById(requestDto.getRentalId())).thenReturn(Optional.empty());
        // When
        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> paymentService.createPaymentSession(requestDto));
        // Then
        String expected = RENTAL_ENTITY_NOT_FOUND_EXCEPTION_MESSAGE + requestDto.getRentalId();
        String actual = exception.getMessage();
        assertThat(actual).isEqualTo(expected);
        verify(rentalRepository, times(1)).findById(requestDto.getRentalId());
        verifyNoMoreInteractions(rentalRepository);
    }

    @Test
    @DisplayName("Verify whether PaymentResponseDto is valid when calling"
            + " createPaymentSession() method with valid CreatePaymentRequestDto")
    public void createPaymentSession_ValidRequestDto_ReturnsPaymentResponseDto() {
        // Given
        CreatePaymentRequestDto requestDto = PaymentTestUtil.getCreatePaymentRequestDto();
        Payment payment = PaymentTestUtil.getPayment();
        Rental rental = payment.getRental();
        Session session = SessionTestUtil.createSession(payment);
        PaymentResponseDto responseDto = PaymentTestUtil.getPaymentResponseDto(payment);
        when(rentalRepository.findById(rental.getId())).thenReturn(Optional.of(rental));
        when(calculationServiceFactory.getCalculationService(any(Payment.Type.class)))
                .thenReturn(new DefaultPaymentCalculationService());
        when(stripeService.createSession(any(BigDecimal.class))).thenReturn(session);
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        when(paymentMapper.toDto(any(Payment.class))).thenReturn(responseDto);
        // When
        PaymentResponseDto result = paymentService.createPaymentSession(requestDto);
        // Then
        assertThat(result).isEqualTo(responseDto);
        verify(rentalRepository, times(1)).findById(requestDto.getRentalId());
        verify(paymentRepository, times(1)).save(any());
        verify(paymentMapper, times(1)).toDto(payment);
        verifyNoMoreInteractions(rentalRepository, paymentRepository, paymentMapper);
    }

    @Test
    @DisplayName("Verify whether AccessDeniedException is thrown when calling"
            + "getAll() method with invalid user id")
    public void getAll_InvalidId_ShouldThrowAccessDeniedException() {
        // Given
        Long invalidUserId = 100L;
        User user = UserTestUtil.getUser();
        // When
        Exception exception = assertThrows(
                AccessDeniedException.class,
                () -> paymentService.getAll(user, invalidUserId)
        );
        // Then
        String expected = "This account with id: " + user.getId()
                + " don't have permission to see payments of other customers";
        String actual = exception.getMessage();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Verify whether list Of PaymentResponseDto is valid when calling"
            + " getAll() method with valid user id")
    public void getAll_ValidId_ShouldReturnListOfPaymentResponseDto() {
        // Given
        Long id = 2L;
        User user = UserTestUtil.getUser();
        List<Payment> paymentList = PaymentTestUtil.getListOfPayments();
        when(paymentRepository.findAllByRental_User_Id(user.getId())).thenReturn(paymentList);
        when(paymentMapper.toDetailedDto(any(Payment.class))).thenAnswer(invocation -> {
                    Payment payment = invocation.getArgument(0);
                    return PaymentTestUtil.getPaymentDetailedResponseDto(payment);
                }
        );
        // When
        List<PaymentDetailedResponseDto> resultsDto = paymentService.getAll(user, id);
        // Then
        assertThat(resultsDto).hasSize(2);
        assertThat(resultsDto.getFirst().getSessionId())
                .isEqualTo(paymentList.getFirst().getSessionId());
        assertThat(resultsDto.getLast().getSessionUrl())
                .isEqualTo(paymentList.getLast().getSessionUrl());
        verify(paymentRepository, times(1)).findAllByRental_User_Id(user.getId());
        verify(paymentMapper, times(2)).toDetailedDto(any(Payment.class));
        verifyNoMoreInteractions(paymentRepository, paymentMapper);
    }

    @Test
    @DisplayName("Verify whether EntityNotFoundException is thrown when calling"
            + " handleSuccess() method with invalid session id")
    public void handleSuccess_InvalidSessionId_ShouldThrowEntityNotFoundException() {
        // Given
        String invalidSessionId = "InvalidSessionId";
        when(paymentRepository.findPaymentBySessionId(invalidSessionId))
                .thenReturn(Optional.empty());
        // When
        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> paymentService.handleSuccess(invalidSessionId)
        );
        // Then
        String expected = PAYMENT_ENTITY_NOT_FOUND_EXCEPTION_MESSAGE + invalidSessionId;
        String actual = exception.getMessage();
        assertThat(actual).isEqualTo(expected);
        verify(paymentRepository, times(1)).findPaymentBySessionId(invalidSessionId);
        verifyNoMoreInteractions(paymentRepository);
    }

    @Test
    @DisplayName("Verify whether EntityNotFoundException is thrown when calling"
            + " handleCancel() method with invalid session id")
    public void handleCancel_InvalidSessionId_ShouldThrowEntityNotFoundException() {
        // Given
        String invalidSessionId = "InvalidSessionId";
        when(paymentRepository.findPaymentBySessionId(invalidSessionId))
                .thenReturn(Optional.empty());
        // When
        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> paymentService.handleCancel(invalidSessionId)
        );
        // Then
        String expected = PAYMENT_ENTITY_NOT_FOUND_EXCEPTION_MESSAGE + invalidSessionId;
        String actual = exception.getMessage();
        assertThat(actual).isEqualTo(expected);
        verify(paymentRepository, times(1)).findPaymentBySessionId(invalidSessionId);
        verifyNoMoreInteractions(paymentRepository);
    }

    @Test
    @DisplayName("Verify whether PaymentStatusResponseDto is valid when calling"
            + " handleCancel() method with valid session id")
    public void handleCancel_ValidSessionId_ReturnsPaymentStatusResponseDto() {
        // Given
        Payment payment = PaymentTestUtil.getPayment();
        payment.setStatus(Payment.Status.CANCEL);
        PaymentStatusResponseDto responseDto = new PaymentStatusResponseDto(payment.getSessionId(),
                payment.getStatus());
        when(paymentRepository.findPaymentBySessionId(payment.getSessionId()))
                .thenReturn(Optional.of(payment));
        when(paymentRepository.save(payment)).thenReturn(payment);
        when(paymentMapper.toStatusDto(any(Payment.class))).thenReturn(responseDto);
        // When
        PaymentStatusResponseDto result = paymentService.handleCancel(payment.getSessionId());
        // Then
        assertNotNull(result);
        assertThat(result).isEqualTo(responseDto);
        verify(paymentRepository, times(1)).findPaymentBySessionId(payment.getSessionId());
        verify(paymentMapper, times(1)).toStatusDto(any(Payment.class));
        verify(notificationService, times(1)).notifyAdmin(anyString());
        verifyNoMoreInteractions(paymentRepository, paymentMapper);
    }
}
