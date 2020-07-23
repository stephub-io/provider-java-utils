package io.stephub.provider.api.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
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

    @NotNull
    private StepStatus status;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Builder.Default
    private Duration duration = Duration.ZERO;

    private String errorMessage;

    @Valid
    private VALUE output;
}
