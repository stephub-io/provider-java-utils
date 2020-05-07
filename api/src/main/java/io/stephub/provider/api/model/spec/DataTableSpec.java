package io.stephub.provider.api.model.spec;

import io.stephub.provider.api.jackson.ClassToSchemaSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

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
    @Builder
    @Getter
    @ToString
    @EqualsAndHashCode
    public static class ColumnSpec<SCHEMA> {
        private String name;
        @JsonSerialize(using = ClassToSchemaSerializer.class)
        private SCHEMA schema;
    }

    private boolean header;
    @Singular
    private List<ColumnSpec<SCHEMA>> columns;
}
