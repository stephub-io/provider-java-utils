package io.stephub.provider.util.model.spec;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.stephub.provider.util.jackson.ClassToSchemaSerializer;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@EqualsAndHashCode
@ToString
public class ArgumentSpec {
    private String name;
    @JsonSerialize(using = ClassToSchemaSerializer.class)
    private Class<?> schema;
}
