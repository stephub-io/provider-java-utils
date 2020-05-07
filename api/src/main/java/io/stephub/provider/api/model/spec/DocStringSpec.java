package io.stephub.provider.api.model.spec;

import io.stephub.provider.api.jackson.ClassToSchemaSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@ToString
@EqualsAndHashCode
public class DocStringSpec<SCHEMA> {
    @JsonSerialize(using = ClassToSchemaSerializer.class)
    private SCHEMA schema;
}
