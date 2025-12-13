package com.sogeor.service.payments.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

public class PaymentDTOs {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PaymentRequest {

        private Integer orderId;

        private BigDecimal amount;

        private String paymentMethod;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PaymentResponse {

        private Integer id;

        private Integer orderId;

        private BigDecimal amount;

        private String status;

        private String paymentMethod;

        private String transactionId;

        private Instant createdAt;

        private Instant updatedAt;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentConfirmationRequest {

        private Integer id;

        private String transactionId;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentRefundRequest {

        private Integer id;

    }

}
