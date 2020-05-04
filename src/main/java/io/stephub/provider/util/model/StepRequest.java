package io.stephub.provider.util.model;

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
public class StepRequest {
    @NotNull
    private String id;
    @Singular
    private Map<String, Object> arguments;
    private Object docString;
    private List<Map<String, Object>> dataTable;

}
