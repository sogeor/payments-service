package com.sogeor.service.payments.repository;

import com.sogeor.service.payments.domain.Order;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends ReactiveCrudRepository<@NotNull Order, @NotNull Integer> {}
