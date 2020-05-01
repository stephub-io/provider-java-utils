package io.stephub.provider.util.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.stephub.provider.util.jackson.ClassToSchemaSerializer;
import io.stephub.provider.util.model.spec.StepSpec;
import lombok.*;

import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class ProviderInfo {
    private String name;
    private String version;
    @JsonSerialize(using = ClassToSchemaSerializer.class)
    private Class<?> optionsSchema;
    private List<StepSpec> steps;
}
