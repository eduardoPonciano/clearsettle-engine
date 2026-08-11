package com.eduardoponciano.clearsettle.infrastructure.config;

import com.eduardoponciano.clearsettle.application.port.in.ProcessSettlementUseCase;
import com.eduardoponciano.clearsettle.application.port.out.IdempotencyValidator;
import com.eduardoponciano.clearsettle.application.port.out.SettlementEventPublisher;
import com.eduardoponciano.clearsettle.application.port.out.SettlementRepository;
import com.eduardoponciano.clearsettle.application.usecase.ProcessSettlementService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    public ProcessSettlementUseCase processSettlementUseCase(
            SettlementRepository repository,
            SettlementEventPublisher eventPublisher,
            IdempotencyValidator idempotencyValidator) {
        return new ProcessSettlementService(repository, eventPublisher, idempotencyValidator);
    }
}
