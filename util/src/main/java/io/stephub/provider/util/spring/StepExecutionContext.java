package io.stephub.provider.util.spring;

import io.stephub.provider.api.model.LogEntry;

public interface StepExecutionContext {
    void addLog(LogEntry logEntry);
}
