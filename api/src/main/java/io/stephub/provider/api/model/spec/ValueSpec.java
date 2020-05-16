package io.stephub.provider.api.model.spec;

import lombok.*;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
@SuperBuilder
public abstract class ValueSpec<SCHEMA> {
    private SCHEMA schema;
    @Builder.Default
    private boolean strict = false;
}
