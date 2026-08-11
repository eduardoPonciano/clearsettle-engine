package com.eduardoponciano.clearsettle.infrastructure.adapter.inbound.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/v1/transactions/settlement")
public class SettlementController {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC = "settlement-requested";

    public SettlementController(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> requestSettlement(@Valid @RequestBody SettlementRequest request) {
        
        Map<String, Object> event = Map.of(
                "eventId", UUID.randomUUID().toString(),
                "transactionId", request.transactionId().toString(),
                "amount", request.amount(),
                "currency", request.currency(),
                "merchantId", request.merchantId().toString(),
                "customerId", request.customerId().toString(),
                "occurredAt", request.timestamp().toString()
        );

        kafkaTemplate.send(TOPIC, request.transactionId().toString(), event);

        return ResponseEntity.accepted().body(Map.of(
                "status", "ACCEPTED",
                "message", "Transaction settlement is being processed.",
                "correlationId", request.transactionId()
        ));
    }

    public record SettlementRequest(
            @NotNull UUID transactionId,
            @NotNull @DecimalMin("0.01") BigDecimal amount,
            @NotBlank @Pattern(regexp = "^[A-Z]{3}$") String currency,
            @NotNull UUID merchantId,
            @NotNull UUID customerId,
            @NotNull Instant timestamp
    ) {}
}
