package io.stephub.provider.api.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.validation.Valid;
import java.time.Duration;

@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder
@EqualsAndHashCode
public
class ProviderOptions<O> {
    @Builder.Default
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Duration sessionTimeout = Duration.ofMinutes(5);
    @Valid
    private O options;
}
