package com.eduardoponciano.clearsettle.domain.model;

import java.util.Objects;
import java.util.UUID;

public record TransactionId(UUID value) {
    public TransactionId {
        Objects.requireNonNull(value, "TransactionId value cannot be null");
    }

    public static TransactionId generate() {
        return new TransactionId(UUID.randomUUID());
    }
    
    public static TransactionId fromString(String value) {
        return new TransactionId(UUID.fromString(value));
    }
}
