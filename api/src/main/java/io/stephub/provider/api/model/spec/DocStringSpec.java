package io.stephub.provider.api.model.spec;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@SuperBuilder
@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class DocStringSpec<SCHEMA> extends ValueSpec<SCHEMA> {
}
