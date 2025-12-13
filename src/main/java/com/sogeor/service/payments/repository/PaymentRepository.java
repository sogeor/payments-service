package com.sogeor.service.payments.repository;

import com.sogeor.service.payments.domain.Payment;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends ReactiveCrudRepository<@NotNull Payment, @NotNull Integer> {}
