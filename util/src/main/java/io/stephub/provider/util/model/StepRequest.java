package io.stephub.provider.util.model;

import lombok.*;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Data
@EqualsAndHashCode
@Getter
@Builder
public class StepRequest {
    private String id;
    @Singular
    private Map<String, Object> arguments;
    private Object docString;
    private List<Map<String, Object>> dataTable;
}
