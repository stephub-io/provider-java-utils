package io.stephub.provider.api.model;

import io.stephub.provider.api.jackson.ClassToSchemaSerializer;
import io.stephub.provider.api.model.spec.StepSpec;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class ProviderInfo<SCHEMA> {
    private String name;
    private String version;
    @JsonSerialize(using = ClassToSchemaSerializer.class)
    private SCHEMA optionsSchema;
    private List<StepSpec<SCHEMA>> steps;
}
