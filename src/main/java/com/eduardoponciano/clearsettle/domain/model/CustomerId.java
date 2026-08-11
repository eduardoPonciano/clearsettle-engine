package com.eduardoponciano.clearsettle.domain.model;

import java.util.Objects;
import java.util.UUID;

public record CustomerId(UUID value) {
    public CustomerId {
        Objects.requireNonNull(value, "CustomerId value cannot be null");
    }

    public static CustomerId fromString(String value) {
        return new CustomerId(UUID.fromString(value));
    }
}
