package io.stephub.provider.api.model.spec;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

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
        private String name;
    }

    private boolean header;
    @Singular
    private List<ColumnSpec<SCHEMA>> columns;
}
