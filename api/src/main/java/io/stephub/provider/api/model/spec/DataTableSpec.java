package io.stephub.provider.api.model.spec;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

import static io.stephub.provider.api.util.Patterns.ID_PATTERN_STR;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@ToString
@EqualsAndHashCode
public class DataTableSpec<SCHEMA> {
    @NoArgsConstructor
    @AllArgsConstructor
    @SuperBuilder
    @Getter
    @ToString
    @EqualsAndHashCode(callSuper = true)
    public static class ColumnSpec<SCHEMA> extends ValueSpec<SCHEMA> {
        @Pattern(regexp = ID_PATTERN_STR)
        @NotNull
        private String name;
    }

    private boolean header;

    @Singular
    @Valid
    @NotNull
    @Size(min = 1)
    private List<ColumnSpec<SCHEMA>> columns;
}
