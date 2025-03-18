package doji.doe.carsharing.controller;

import doji.doe.carsharing.dto.payment.CreatePaymentRequestDto;
import doji.doe.carsharing.dto.payment.PaymentDetailedResponseDto;
import doji.doe.carsharing.dto.payment.PaymentResponseDto;
import doji.doe.carsharing.dto.payment.PaymentStatusResponseDto;
import doji.doe.carsharing.model.User;
import doji.doe.carsharing.service.payment.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @GetMapping("/{id}")
    @Operation(summary = "getPayments")
    public List<PaymentDetailedResponseDto> getAll(Authentication authentication,
                                                   @PathVariable Long id) {
        User user = (User) authentication.getPrincipal();
        return paymentService.getAll(user, id);
    }

    @PostMapping
    @Operation(summary = "create payment session")
    public PaymentResponseDto createPaymentSession(
            @RequestBody @Valid CreatePaymentRequestDto requestDto) {
        return paymentService.createPaymentSession(requestDto);
    }

    @GetMapping("/success")
    @Operation(summary = "handle successful payment")
    public PaymentStatusResponseDto handleSuccess(
            @RequestParam String sessionId) {
        return paymentService.handleSuccess(sessionId);
    }

    @GetMapping("/cancel")
    @Operation(summary = "handle cancel payment")
    public PaymentStatusResponseDto handleCancel(
            @RequestParam String sessionId) {
        return paymentService.handleCancel(sessionId);
    }
}
