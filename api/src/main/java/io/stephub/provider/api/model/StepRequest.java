package io.stephub.provider.api.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;
import java.util.Map;

import static io.stephub.provider.api.util.Patterns.ID_PATTERN_STR;

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
    @Valid
    private Map<@Pattern(regexp = ID_PATTERN_STR) String, VALUE> arguments;
    @Valid
    private VALUE docString;
    @Valid
    private List<Map<@Pattern(regexp = ID_PATTERN_STR) String, VALUE>> dataTable;

}
