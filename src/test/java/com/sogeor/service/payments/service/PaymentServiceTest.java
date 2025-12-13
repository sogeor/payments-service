package com.sogeor.service.payments.service;

import com.sogeor.service.payments.domain.Payment;
import com.sogeor.service.payments.dto.PaymentDTOs.PaymentConfirmationRequest;
import com.sogeor.service.payments.dto.PaymentDTOs.PaymentRefundRequest;
import com.sogeor.service.payments.dto.PaymentDTOs.PaymentRequest;
import com.sogeor.service.payments.dto.event.EventDTOs.PaymentConfirmedEvent;
import com.sogeor.service.payments.dto.event.EventDTOs.PaymentRefundedEvent;
import com.sogeor.service.payments.kafka.PaymentKafkaProducer;
import com.sogeor.service.payments.repository.OrderRepository;
import com.sogeor.service.payments.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PaymentKafkaProducer paymentKafkaProducer;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void createPayment_Success() {
        PaymentRequest request = PaymentRequest.builder()
                                               .orderId(1)
                                               .amount(BigDecimal.valueOf(100.00))
                                               .paymentMethod("CREDIT_CARD")
                                               .build();

        when(orderRepository.existsById(1)).thenReturn(Mono.just(true));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment p = invocation.getArgument(0);
            p.setId(10);
            return Mono.just(p);
        });

        StepVerifier.create(paymentService.createPayment(request))
                    .expectNextMatches(
                            response -> response.getId().equals(10) && response.getStatus().equals("PENDING") &&
                                        response.getAmount().equals(BigDecimal.valueOf(100.00)))
                    .verifyComplete();

        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    void createPayment_OrderNotFound() {
        PaymentRequest request = PaymentRequest.builder().orderId(1).build();

        when(orderRepository.existsById(1)).thenReturn(Mono.just(false));

        StepVerifier.create(paymentService.createPayment(request))
                    .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                                                     throwable.getMessage().equals("Order not found"))
                    .verify();

        verify(paymentRepository, never()).save(any());
    }

    @Test
    void confirmPayment_Success() {
        PaymentConfirmationRequest request = new PaymentConfirmationRequest(10, "txn_123");
        Payment payment = Payment.builder().id(10).orderId(1).amount(BigDecimal.TEN).status("PENDING").build();

        when(paymentRepository.findById(10)).thenReturn(Mono.just(payment));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(paymentService.confirmPayment(request))
                    .expectNextMatches(response -> response.getStatus().equals("CONFIRMED") &&
                                                   response.getTransactionId().equals("txn_123"))
                    .verifyComplete();

        verify(paymentKafkaProducer).sendPaymentConfirmed(any(PaymentConfirmedEvent.class));
    }

    @Test
    void refundPayment_Success() {
        PaymentRefundRequest request = new PaymentRefundRequest(10);
        Payment payment = Payment.builder()
                                 .id(10)
                                 .orderId(1)
                                 .amount(BigDecimal.TEN)
                                 .status("CONFIRMED")
                                 .transactionId("txn_123")
                                 .build();

        when(paymentRepository.findById(10)).thenReturn(Mono.just(payment));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(paymentService.refundPayment(request))
                    .expectNextMatches(response -> response.getStatus().equals("REFUNDED"))
                    .verifyComplete();

        verify(paymentKafkaProducer).sendPaymentRefunded(any(PaymentRefundedEvent.class));
    }

}
