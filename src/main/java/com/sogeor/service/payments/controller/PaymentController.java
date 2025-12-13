package com.sogeor.service.payments.controller;

import com.sogeor.service.payments.dto.PaymentDTOs.PaymentConfirmationRequest;
import com.sogeor.service.payments.dto.PaymentDTOs.PaymentRefundRequest;
import com.sogeor.service.payments.dto.PaymentDTOs.PaymentRequest;
import com.sogeor.service.payments.dto.PaymentDTOs.PaymentResponse;
import com.sogeor.service.payments.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<@NotNull PaymentResponse> createPayment(@RequestBody PaymentRequest request) {
        return paymentService.createPayment(request);
    }

    @GetMapping("/{id}")
    public Mono<@NotNull PaymentResponse> getPayment(@PathVariable Integer id) {
        return paymentService.getPayment(id);
    }

    @PostMapping("/confirm")
    public Mono<@NotNull PaymentResponse> confirmPayment(@RequestBody PaymentConfirmationRequest request) {
        return paymentService.confirmPayment(request);
    }

    @PostMapping("/refund")
    public Mono<@NotNull PaymentResponse> refundPayment(@RequestBody PaymentRefundRequest request) {
        return paymentService.refundPayment(request);
    }

}
