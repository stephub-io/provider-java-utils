package io.stephub.provider.api.model;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@EqualsAndHashCode
public class LogEntry {
    @NotNull
    private String message;

    @Singular
    List<LogAttachment> attachments = new ArrayList<>();

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    @Builder
    @EqualsAndHashCode
    public static class LogAttachment {
        private String contentType;
        private String fileName;
        private byte[] content;
    }
}
