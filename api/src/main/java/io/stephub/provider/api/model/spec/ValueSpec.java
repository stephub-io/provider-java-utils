package io.stephub.provider.api.model.spec;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.validation.Valid;

@NoArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
@SuperBuilder
public abstract class ValueSpec<SCHEMA> {
    @Valid
    private SCHEMA schema;
    @Builder.Default
    private final boolean strict = false;
}
