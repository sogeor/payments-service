package com.sogeor.service.payments.service;

import com.sogeor.service.payments.domain.Payment;
import com.sogeor.service.payments.dto.PaymentDTOs.PaymentConfirmationRequest;
import com.sogeor.service.payments.dto.PaymentDTOs.PaymentRefundRequest;
import com.sogeor.service.payments.dto.PaymentDTOs.PaymentRequest;
import com.sogeor.service.payments.dto.PaymentDTOs.PaymentResponse;
import com.sogeor.service.payments.dto.event.EventDTOs.PaymentConfirmedEvent;
import com.sogeor.service.payments.dto.event.EventDTOs.PaymentRefundedEvent;
import com.sogeor.service.payments.kafka.PaymentKafkaProducer;
import com.sogeor.service.payments.repository.OrderRepository;
import com.sogeor.service.payments.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;

    private final OrderRepository orderRepository;

    private final PaymentKafkaProducer paymentKafkaProducer;

    @Transactional
    public Mono<@NotNull PaymentResponse> createPayment(PaymentRequest request) {
        return orderRepository.existsById(request.getOrderId()).flatMap(exists -> {
            if (!exists) {
                return Mono.error(new RuntimeException("Order not found"));
            }
            return paymentRepository.save(Payment.builder()
                                                 .orderId(request.getOrderId())
                                                 .amount(request.getAmount())
                                                 .status("PENDING")
                                                 .paymentMethod(request.getPaymentMethod())
                                                 .createdAt(Instant.now())
                                                 .updatedAt(Instant.now())
                                                 .build());
        }).map(this::mapToResponse);
    }

    public Mono<@NotNull PaymentResponse> getPayment(Integer id) {
        return paymentRepository.findById(id)
                                .switchIfEmpty(Mono.error(new RuntimeException("Payment not found")))
                                .map(this::mapToResponse);
    }

    @Transactional
    public Mono<@NotNull PaymentResponse> confirmPayment(PaymentConfirmationRequest request) {
        return paymentRepository.findById(request.getId())
                                .switchIfEmpty(Mono.error(new RuntimeException("Payment not found")))
                                .flatMap(payment -> {
                                    if (!"PENDING".equals(payment.getStatus())) {
                                        return Mono.error(new RuntimeException("Payment is not pending"));
                                    }
                                    payment.setStatus("CONFIRMED");
                                    payment.setTransactionId(request.getTransactionId());
                                    payment.setUpdatedAt(Instant.now());
                                    return paymentRepository.save(payment).doOnSuccess(p -> {
                                        PaymentConfirmedEvent event = PaymentConfirmedEvent.builder()
                                                                                           .eventType(
                                                                                                   "PAYMENT_CONFIRMED")
                                                                                           .orderId(String.valueOf(
                                                                                                   p.getOrderId()))
                                                                                           .amount(p.getAmount())
                                                                                           .transactionId(
                                                                                                   p.getTransactionId())
                                                                                           .timestamp(new Date())
                                                                                           .build();
                                        paymentKafkaProducer.sendPaymentConfirmed(event);
                                    });
                                })
                                .map(this::mapToResponse);
    }

    @Transactional
    public Mono<@NotNull PaymentResponse> refundPayment(PaymentRefundRequest request) {
        return paymentRepository.findById(request.getId())
                                .switchIfEmpty(Mono.error(new RuntimeException("Payment not found")))
                                .flatMap(payment -> {
                                    if (!"CONFIRMED".equals(payment.getStatus())) {
                                        return Mono.error(new RuntimeException("Payment is not confirmed"));
                                    }
                                    payment.setStatus("REFUNDED");
                                    payment.setUpdatedAt(Instant.now());
                                    return paymentRepository.save(payment).doOnSuccess(p -> {
                                        PaymentRefundedEvent event = PaymentRefundedEvent.builder()
                                                                                         .eventType("PAYMENT_REFUNDED")
                                                                                         .orderId(String.valueOf(
                                                                                                 p.getOrderId()))
                                                                                         .amount(p.getAmount())
                                                                                         .transactionId(
                                                                                                 p.getTransactionId())
                                                                                         .timestamp(new Date())
                                                                                         .build();
                                        paymentKafkaProducer.sendPaymentRefunded(event);
                                    });
                                })
                                .map(this::mapToResponse);
    }

    private PaymentResponse mapToResponse(Payment payment) {
        return PaymentResponse.builder()
                              .id(payment.getId())
                              .orderId(payment.getOrderId())
                              .amount(payment.getAmount())
                              .status(payment.getStatus())
                              .paymentMethod(payment.getPaymentMethod())
                              .transactionId(payment.getTransactionId())
                              .createdAt(payment.getCreatedAt())
                              .updatedAt(payment.getUpdatedAt())
                              .build();
    }

}
