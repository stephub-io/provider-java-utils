package io.stephub.provider.util.model.spec;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.stephub.provider.util.jackson.ClassToSchemaSerializer;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@ToString
@EqualsAndHashCode
public class DataTableSpec {
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Getter
    @ToString
    @EqualsAndHashCode
    public static class ColumnSpec {
        private String name;
        @JsonSerialize(using = ClassToSchemaSerializer.class)
        private Class<?> schema;
    }

    private boolean header;
    @Singular
    private List<ColumnSpec> columns;
}
