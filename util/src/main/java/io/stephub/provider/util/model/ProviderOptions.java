package io.stephub.provider.util.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.SuperBuilder;

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
    private O options;
}
