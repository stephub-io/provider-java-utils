package io.stephub.provider.api.model.spec;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@ToString
@EqualsAndHashCode
public class OutputSpec<SCHEMA> {
    private SCHEMA schema;
}
