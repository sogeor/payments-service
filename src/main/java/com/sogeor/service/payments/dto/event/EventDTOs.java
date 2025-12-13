package com.sogeor.service.payments.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class EventDTOs {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PaymentConfirmedEvent {

        private String eventType;

        private String orderId;

        private BigDecimal amount;

        private String transactionId;

        private Date timestamp;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PaymentFailedEvent {

        private String eventType;

        private String orderId;

        private String reason;

        private Date timestamp;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PaymentRefundedEvent {

        private String eventType;

        private String orderId;

        private BigDecimal amount;

        private String transactionId;

        private Date timestamp;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderCreatedEvent {

        private String eventType;

        private String orderId;

        private String userId;

        private BigDecimal totalAmount;

        private List<OrderItem> items;

        private String shippingAddress;

        private Date timestamp;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderItem {

        private String productId;

        private Integer quantity;

        private BigDecimal price;

    }

}
