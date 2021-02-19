package io.stephub.provider.api.model.spec;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.List;

@NoArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
@SuperBuilder
public class Documentation {
    /**
     * Description in markdown.
     */
    private String description;
    /**
     * Examples in markdown.
     */
    private List<String> examples;
}
