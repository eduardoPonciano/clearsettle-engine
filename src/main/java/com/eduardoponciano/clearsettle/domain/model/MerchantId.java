package com.eduardoponciano.clearsettle.domain.model;

import java.util.Objects;
import java.util.UUID;

public record MerchantId(UUID value) {
    public MerchantId {
        Objects.requireNonNull(value, "MerchantId value cannot be null");
    }

    public static MerchantId fromString(String value) {
        return new MerchantId(UUID.fromString(value));
    }
}
