package io.stephub.provider.api.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode
@Getter
@SuperBuilder
public class StepRequest<VALUE> {
    @NotNull
    private String id;
    @Singular
    private Map<String, VALUE> arguments;
    private VALUE docString;
    private List<Map<String, VALUE>> dataTable;

}
