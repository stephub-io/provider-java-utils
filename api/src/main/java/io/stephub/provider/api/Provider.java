package io.stephub.provider.api;

import io.stephub.provider.api.model.ProviderInfo;
import io.stephub.provider.api.model.ProviderOptions;
import io.stephub.provider.api.model.StepRequest;
import io.stephub.provider.api.model.StepResponse;

public interface Provider<PO, SCHEMA, VALUE> {
    String createSession(ProviderOptions<PO> options) throws ProviderException;

    StepResponse<VALUE> execute(String sessionId, StepRequest<VALUE> request) throws ProviderException;

    void destroySession(String sessionId) throws ProviderException;

    ProviderInfo<SCHEMA> getInfo() throws ProviderException;

}
