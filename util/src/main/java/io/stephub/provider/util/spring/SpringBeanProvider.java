package io.stephub.provider.util.spring;

import io.stephub.provider.api.ProviderException;
import io.stephub.provider.api.model.ProviderInfo;
import io.stephub.provider.api.model.StepRequest;
import io.stephub.provider.api.model.StepResponse;
import io.stephub.provider.api.model.spec.StepSpec;
import io.stephub.provider.util.LocalProviderAdapter;
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

    public interface StepInvoker<VALUE> {
        StepResponse<VALUE> invoke(String sessionId, SessionState<?> state, StepRequest<VALUE> request);
    }

    @Override
    protected final StepResponse<VALUE> executeWithinState(final String sessionId, final S state, final StepRequest<VALUE> request) {
        final StepInvoker<VALUE> stepMethod = this.stepInvokers.get(request.getId());
        if (stepMethod != null) {
            return stepMethod.invoke(sessionId, state, request);
        } else {
            throw new ProviderException("No implementation in " + this.getClass().getName() + " found for step with id=" + request.getId());
        }
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

}
