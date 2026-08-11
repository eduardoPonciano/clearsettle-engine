package com.eduardoponciano.clearsettle.application.port.in;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record SettlementCommand(
    UUID transactionId,
    BigDecimal amount,
    String currency,
    UUID merchantId,
    UUID customerId,
    Instant occurredAt
) {}
