package ru.itis.billing.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Slf4j
@Configuration
public class KafkaErrorConfig {

    @Bean
    public CommonErrorHandler kafkaErrorHandler() {
        DefaultErrorHandler handler = new DefaultErrorHandler(
                (record, ex) -> log.error("Failed to process record: {}", record, ex),
                new FixedBackOff(1000L, 2L)  // 2 попытки с интервалом 1 сек
        );
        // Не пытаться повторять для ошибок десериализации
        handler.addNotRetryableExceptions(
                org.apache.kafka.common.errors.SerializationException.class,
                tools.jackson.databind.exc.MismatchedInputException.class
        );
        return handler;
    }
}