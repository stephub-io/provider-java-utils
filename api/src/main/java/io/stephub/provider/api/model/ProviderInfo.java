package io.stephub.provider.api.model;

import io.stephub.provider.api.model.spec.StepSpec;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@SuperBuilder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class ProviderInfo<SCHEMA> {
    @NotNull
    private String name;
    @NotNull
    private String version;
    @Valid
    private SCHEMA optionsSchema;
    @Valid
    private List<StepSpec<SCHEMA>> steps;
}
