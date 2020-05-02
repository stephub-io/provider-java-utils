package io.stephub.provider.util.spring;

import io.stephub.provider.util.LocalProviderAdapter;
import io.stephub.provider.util.ProviderException;
import io.stephub.provider.util.model.ProviderInfo;
import io.stephub.provider.util.model.StepRequest;
import io.stephub.provider.util.model.StepResponse;
import io.stephub.provider.util.model.spec.StepSpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class SpringBeanProvider<S extends LocalProviderAdapter.SessionState<PO>, PO> extends LocalProviderAdapter<S, PO> {
    Map<String, StepInvoker> stepInvokers = new HashMap<>();
    List<StepSpec> stepSpecs = new ArrayList<>();

    @Autowired(required = false)
    private BuildProperties buildProperties;

    public interface StepInvoker {
        StepResponse invoke(String sessionId, SessionState<?> state, StepRequest request);
    }

    @Override
    protected final StepResponse executeWithinState(final String sessionId, final S state, final StepRequest request) {
        final StepInvoker stepMethod = this.stepInvokers.get(request.getId());
        if (stepMethod != null) {
            return stepMethod.invoke(sessionId, state, request);
        } else {
            throw new ProviderException("No implementation in " + this.getClass().getName() + " found for step with id=" + request.getId());
        }
    }

    protected abstract String getName();

    protected String getVersion() throws ProviderException {
        return this.buildProperties != null ? this.buildProperties.getVersion() : "unknown";
    }

    protected abstract Class<? extends PO> getOptionsSchema();

    @Override
    public final ProviderInfo getInfo() {
        return ProviderInfo.builder().name(this.getName()).
                version(this.getVersion()).steps(this.stepSpecs).
                optionsSchema(this.getOptionsSchema()).build();
    }

}
