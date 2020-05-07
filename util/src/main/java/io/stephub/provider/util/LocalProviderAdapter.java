package io.stephub.provider.util;

import io.stephub.provider.api.Provider;
import io.stephub.provider.api.ProviderException;
import io.stephub.provider.api.model.ProviderInfo;
import io.stephub.provider.api.model.ProviderOptions;
import io.stephub.provider.api.model.StepRequest;
import io.stephub.provider.api.model.StepResponse;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
public abstract class LocalProviderAdapter<S extends LocalProviderAdapter.SessionState<PO>, PO, SCHEMA, VALUE>
        implements Provider<PO, SCHEMA, VALUE> {

    private final ExpiringMap<String, S> sessionStore = ExpiringMap.builder()
            .expirationListener((sessionId, state) -> {
                log.info("Session expired: {}", sessionId);
                this.stopState((S) state);
            })
            .variableExpiration()
            .build();

    @Data
    @SuperBuilder
    public static class SessionState<PO> {
        private String sessionId;
        private ProviderOptions<PO> providerOptions;
    }

    @Override
    public final String createSession(final ProviderOptions<PO> options) {
        final String sessionId = UUID.randomUUID().toString();
        final S state = this.startState(sessionId, options);
        state.setSessionId(sessionId);
        state.setProviderOptions(options);
        this.sessionStore.put(sessionId, state, ExpirationPolicy.ACCESSED, options.getSessionTimeout().getSeconds(), TimeUnit.SECONDS);
        log.debug("Created session {} with options: {}", sessionId, options);
        return sessionId;
    }

    protected abstract S startState(String sessionId, ProviderOptions<PO> options);

    protected abstract void stopState(S state);

    protected abstract StepResponse<VALUE> executeWithinState(String sessionId, S state, StepRequest<VALUE> request);

    @Override
    public abstract ProviderInfo<SCHEMA> getInfo();

    @Override
    public final StepResponse<VALUE> execute(final String sessionId, final StepRequest<VALUE> request) {
        return this.executeWithinState(sessionId, this.getStateSafe(sessionId), request);
    }

    @Override
    public final void destroySession(final String sessionId) {
        this.stopState(this.getStateSafe(sessionId));
        this.sessionStore.remove(sessionId);
        log.debug("Destroyed session: {}", sessionId);
    }

    private S getStateSafe(final String sessionId) {
        final S state = this.sessionStore.get(sessionId);
        if (state == null) {
            throw new ProviderException("Session not found or expired: " + sessionId);
        }
        return state;
    }
}
