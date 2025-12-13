package com.sogeor.service.payments.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("payments")
public class Payment {

    @Id
    private Integer id;

    private Integer orderId;

    private BigDecimal amount;

    private String status;

    private String paymentMethod;

    private String transactionId;

    private Instant createdAt;

    private Instant updatedAt;

}
