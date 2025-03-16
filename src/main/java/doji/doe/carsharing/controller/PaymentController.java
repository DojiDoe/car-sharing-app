package doji.doe.carsharing.controller;

import doji.doe.carsharing.dto.payment.PaymentRequestDto;
import doji.doe.carsharing.dto.payment.PaymentResponseDto;
import doji.doe.carsharing.service.payment.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping
    @Operation(summary = "create payment session")
    public PaymentResponseDto createPaymentSession(
            @RequestBody @Valid PaymentRequestDto requestDto) {
        return paymentService.checkoutRentals(requestDto);
    }

}
