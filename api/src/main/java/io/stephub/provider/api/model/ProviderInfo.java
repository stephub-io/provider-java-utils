package io.stephub.provider.api.model;

import io.stephub.provider.api.model.spec.StepSpec;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class ProviderInfo<SCHEMA> {
    private String name;
    private String version;
    private SCHEMA optionsSchema;
    private List<StepSpec<SCHEMA>> steps;
}
