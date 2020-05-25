package io.stephub.provider.api.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Duration;

@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder
@EqualsAndHashCode
public class StepResponse<VALUE> {
    public enum StepStatus {
        PASSED, FAILED, ERRONEOUS;

        @Override
        @JsonValue
        public String toString() {
            return this.name().toLowerCase();
        }
    }

    private StepStatus status;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Duration duration;
    private String errorMessage;
    private VALUE output;
}
