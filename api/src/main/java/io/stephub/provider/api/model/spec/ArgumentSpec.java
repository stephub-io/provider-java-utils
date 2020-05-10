package io.stephub.provider.api.model.spec;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@EqualsAndHashCode
@ToString
public class ArgumentSpec<SCHEMA> {
    private String name;
    private SCHEMA schema;
}
