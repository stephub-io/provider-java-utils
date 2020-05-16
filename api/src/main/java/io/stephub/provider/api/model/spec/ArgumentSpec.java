package io.stephub.provider.api.model.spec;

import lombok.*;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString
public class ArgumentSpec<SCHEMA> extends ValueSpec<SCHEMA> {
    private String name;
}
