package io.stephub.provider.util;

import io.stephub.provider.api.model.StepRequest;
import io.stephub.provider.api.model.StepResponse;
import io.stephub.provider.util.spring.StepExecutionContext;

public interface StepRequestInterceptor<S extends LocalProviderAdapter.SessionState<?>, VALUE> {
    StepResponse<VALUE> intercept(Chain<S, VALUE> chain);

    interface Chain<S extends LocalProviderAdapter.SessionState<?>, VALUE> {
        S state();

        StepExecutionContext executionContext();

        StepRequest<VALUE> request();

        StepResponse<VALUE> proceed(StepRequest<VALUE> request);
    }
}
