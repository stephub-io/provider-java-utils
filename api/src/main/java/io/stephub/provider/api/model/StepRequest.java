package io.stephub.provider.api.model;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode
@Getter
@Builder
public class StepRequest<VALUE> {
    @NotNull
    private String id;
    @Singular
    private Map<String, VALUE> arguments;
    private VALUE docString;
    private List<Map<String, VALUE>> dataTable;

}
