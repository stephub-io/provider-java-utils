package io.stephub.provider.api.model.spec;

import io.stephub.provider.api.jackson.ClassToSchemaSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@EqualsAndHashCode
@ToString
public class ArgumentSpec<SCHEMA> {
    private String name;
    @JsonSerialize(using = ClassToSchemaSerializer.class)
    private SCHEMA schema;
}
