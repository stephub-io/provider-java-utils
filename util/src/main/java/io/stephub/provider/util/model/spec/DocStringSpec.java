package io.stephub.provider.util.model.spec;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.stephub.provider.util.jackson.ClassToSchemaSerializer;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@ToString
@EqualsAndHashCode
public class DocStringSpec {
    @JsonSerialize(using = ClassToSchemaSerializer.class)
    private Class<?> schema;
}
