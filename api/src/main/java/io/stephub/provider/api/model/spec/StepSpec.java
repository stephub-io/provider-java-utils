package io.stephub.provider.api.model.spec;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@EqualsAndHashCode
@ToString
public class StepSpec<SCHEMA> {
    public enum PayloadType {
        NONE,
        DOC_STRING,
        DATA_TABLE;

        @Override
        @JsonValue
        public String toString() {
            return this.name().toLowerCase();
        }
    }

    private String id;
    @NotNull
    private String pattern;
    private PatternType patternType = PatternType.SIMPLE;

    @Singular
    @Valid
    @NotNull
    private List<ArgumentSpec<SCHEMA>> arguments = new ArrayList<>();

    @Builder.Default
    @NotNull
    private PayloadType payload = PayloadType.NONE;
    @Valid
    private DataTableSpec<SCHEMA> dataTable;
    @Valid
    private DocStringSpec<SCHEMA> docString;
    @Valid
    private OutputSpec<SCHEMA> output;
}
