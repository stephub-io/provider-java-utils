package io.stephub.provider.util.model.spec;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PatternType {
    REGEX;

    @Override
    @JsonValue
    public String toString() {
        return this.name().toLowerCase();
    }
}
