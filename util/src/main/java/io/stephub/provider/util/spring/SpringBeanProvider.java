package io.stephub.provider.util.spring;

import io.stephub.provider.api.ProviderException;
import io.stephub.provider.api.model.LogEntry;
import io.stephub.provider.api.model.ProviderInfo;
import io.stephub.provider.api.model.StepRequest;
import io.stephub.provider.api.model.StepResponse;
import io.stephub.provider.api.model.spec.StepSpec;
import io.stephub.provider.util.LocalProviderAdapter;
import io.stephub.provider.util.StepRequestInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class SpringBeanProvider<S extends LocalProviderAdapter.SessionState<PO>, PO, SCHEMA, VALUE> extends LocalProviderAdapter<S, PO, SCHEMA, VALUE> {
    Map<String, StepInvoker<VALUE>> stepInvokers = new HashMap<>();
    List<StepSpec<SCHEMA>> stepSpecs = new ArrayList<>();

    @Autowired(required = false)
    private BuildProperties buildProperties;

    private final List<StepRequestInterceptor<S, VALUE>> interceptors = new ArrayList<>();

    public interface StepInvoker<VALUE> {
        StepResponse<VALUE> invoke(String sessionId, SessionState<?> state, StepExecutionContext executionContext, StepRequest<VALUE> request);
    }

    private class InterceptorChain implements StepRequestInterceptor.Chain<S, VALUE> {
        private String sessionId;
        private final S state;
        private final StepExecutionContext executionContext;
        private final StepRequest<VALUE> request;
        private final List<StepRequestInterceptor<S, VALUE>> remainingInterceptors;

        public InterceptorChain(final String sessionId, final S state, final StepExecutionContext executionContext, final StepRequest<VALUE> request, final List<StepRequestInterceptor<S, VALUE>> remainingInterceptors) {
            this.state = state;
            this.executionContext = executionContext;
            this.request = request;
            this.remainingInterceptors = remainingInterceptors;
        }

        @Override
        public S state() {
            return this.state;
        }

        @Override
        public StepExecutionContext executionContext() {
            return this.executionContext;
        }

        @Override
        public StepRequest<VALUE> request() {
            return this.request;
        }

        @Override
        public StepResponse<VALUE> proceed(final StepRequest<VALUE> request) {
            if (!this.remainingInterceptors.isEmpty()) {
                final InterceptorChain subChain = new InterceptorChain(this.sessionId, this.state, this.executionContext, request, this.remainingInterceptors.subList(1, this.remainingInterceptors.size()));
                return this.remainingInterceptors.get(0).intercept(subChain);
            } else {
                final StepInvoker<VALUE> stepMethod = SpringBeanProvider.this.stepInvokers.get(request.getId());
                if (stepMethod != null) {
                    return stepMethod.invoke(this.sessionId, this.state, this.executionContext, request);
                } else {
                    throw new ProviderException("No implementation in " + this.getClass().getName() + " found for step with id=" + request.getId());
                }
            }
        }
    }

    @Override
    protected final StepResponse<VALUE> executeWithinState(final String sessionId, final S state, final StepRequest<VALUE> request) {
        final List<LogEntry> logEntries = new ArrayList<>();
        final StepExecutionContext sec = logEntry -> logEntries.add(logEntry);
        final StepResponse<VALUE> stepResponse = new InterceptorChain(sessionId, state, sec, request, this.interceptors).proceed(request);
        final List<LogEntry> composed = new ArrayList<>();
        if (stepResponse.getLogs() != null) {
            composed.addAll(stepResponse.getLogs());
        }
        composed.addAll(logEntries);
        stepResponse.setLogs(composed);
        return stepResponse;
    }

    protected abstract String getName();

    protected String getVersion() throws ProviderException {
        return this.buildProperties != null ? this.buildProperties.getVersion() : null;
    }

    protected abstract SCHEMA getOptionsSchema();

    @Override
    public final ProviderInfo<SCHEMA> getInfo() {
        return ProviderInfo.<SCHEMA>builder().name(this.getName()).
                version(this.getVersion()).steps(this.stepSpecs).
                optionsSchema(this.getOptionsSchema()).build();
    }

    public final void addInterceptor(final StepRequestInterceptor<S, VALUE> interceptor) {
        this.interceptors.add(interceptor);
    }

    public final void removeInterceptor(final StepRequestInterceptor<S, VALUE> interceptor) {
        this.interceptors.remove(interceptor);
    }
}
