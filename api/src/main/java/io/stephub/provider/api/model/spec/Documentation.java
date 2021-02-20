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
    private List<DocumentationExample> examples;

    @NoArgsConstructor
    @Getter
    @ToString
    @EqualsAndHashCode
    @SuperBuilder
    public static class DocumentationExample {
        /**
         * Example value in JSON representation.
         */
        private String value;

        /**
         * Description in markdown.
         */
        private String description;
    }
}
