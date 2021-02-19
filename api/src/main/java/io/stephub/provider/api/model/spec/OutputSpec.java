package io.stephub.provider.api.model.spec;

import lombok.*;

import javax.validation.Valid;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@ToString
@EqualsAndHashCode
public class OutputSpec<SCHEMA> {
    @Valid
    private SCHEMA schema;

    private Documentation doc;
}
