package com.sogeor.service.payments.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sogeor.service.payments.dto.event.EventDTOs.PaymentConfirmedEvent;
import com.sogeor.service.payments.dto.event.EventDTOs.PaymentFailedEvent;
import com.sogeor.service.payments.dto.event.EventDTOs.PaymentRefundedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentKafkaProducer {

    private static final String TOPIC = "payment-events";

    private final KafkaTemplate<@NotNull String, @NotNull Object> kafkaTemplate;

    private final ObjectMapper objectMapper;

    private void sendEvent(Object event, String key, String eventType) {
        try {
            log.info("Sending {} event for orderId: {}", eventType, key);
            kafkaTemplate.send(TOPIC, key, event);
        } catch (Exception e) {
            log.error("Error sending {} event", eventType, e);
        }
    }

    public void sendPaymentConfirmed(PaymentConfirmedEvent event) {
        sendEvent(event, event.getOrderId(), "PAYMENT_CONFIRMED");
    }

    public void sendPaymentFailed(PaymentFailedEvent event) {
        sendEvent(event, event.getOrderId(), "PAYMENT_FAILED");
    }

    public void sendPaymentRefunded(PaymentRefundedEvent event) {
        sendEvent(event, event.getOrderId(), "PAYMENT_REFUNDED");
    }

}
