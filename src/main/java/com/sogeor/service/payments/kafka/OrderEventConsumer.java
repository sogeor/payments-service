package com.sogeor.service.payments.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sogeor.service.payments.domain.Order;
import com.sogeor.service.payments.dto.event.EventDTOs.OrderCreatedEvent;
import com.sogeor.service.payments.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventConsumer {

    private final OrderRepository orderRepository;

    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "order-events", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeOrderEvent(String message) {
        log.info("Received order event: {}", message);
        try {
            if (message.contains("ORDER_CREATED")) {
                OrderCreatedEvent event = objectMapper.readValue(message, OrderCreatedEvent.class);
                saveOrder(event).subscribe();
            }
        } catch (Exception e) {
            log.error("Error processing order event", e);
        }
    }

    private Mono<@NotNull Order> saveOrder(OrderCreatedEvent event) {
        try {
            Integer id = Integer.parseInt(event.getOrderId());
            return orderRepository.save(Order.builder().id(id).createdAt(Instant.now()).build())
                                  .doOnSuccess(o -> log.info("Saved order locally: {}", o.getId()))
                                  .doOnError(e -> log.error("Failed to save order locally", e));
        } catch (NumberFormatException e) {
            log.error("Invalid orderId format in event: {}", event.getOrderId());
            return Mono.empty();
        }
    }

}
