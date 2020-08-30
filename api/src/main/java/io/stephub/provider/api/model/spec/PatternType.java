package io.stephub.provider.api.model.spec;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PatternType {
    SIMPLE;

    @Override
    @JsonValue
    public String toString() {
        return this.name().toLowerCase();
    }
}
