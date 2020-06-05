package io.stephub.provider.api.model.spec;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import static io.stephub.provider.api.util.Patterns.ID_PATTERN_STR;

@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString
public class ArgumentSpec<SCHEMA> extends ValueSpec<SCHEMA> {
    @NotNull
    @Pattern(regexp = ID_PATTERN_STR)
    private String name;
}
