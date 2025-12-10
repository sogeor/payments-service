package com.sogeor.service.payments;

import org.jetbrains.annotations.NotNull;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @since 1.0.0-RC1
 */
@SpringBootApplication
public class PaymentsServiceApplication {

    /**
     * @since 1.0.0-RC1
     */
    static void main(final @NotNull String... arguments) {
        SpringApplication.run(PaymentsServiceApplication.class, arguments);
    }

}
