package io.stephub.provider.util;

import io.stephub.provider.util.model.ProviderOptions;
import io.stephub.provider.util.model.StepResponse;
import io.stephub.provider.util.spring.SpringBeanProvider;
import io.stephub.provider.util.spring.annotation.StepArgument;
import io.stephub.provider.util.spring.annotation.StepMethod;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.springframework.stereotype.Service;

@Service
public class DummyProvider extends SpringBeanProvider<DummyState, DummyOptions> {
    @Override
    protected String getName() {
        return "dummy";
    }

    @Override
    protected Class<? extends DummyOptions> getOptionsSchema() {
        return DummyOptions.class;
    }

    @Override
    protected DummyState startState(final String sessionId, final ProviderOptions<DummyOptions> options) {
        return null;
    }

    @Override
    protected void stopState(final DummyState state) {

    }

    @StepMethod(pattern = ".*")
    public StepResponse step1(@StepArgument(name = "arg1") boolean arg1,
                              @StepArgument(name = "arg2") String arg2) {
        return StepResponse.builder().status(StepResponse.StepStatus.PASSED).
                build();
    }
}

@Data
class DummyOptions {
    private String baseUrl;
}

@SuperBuilder
class DummyState extends LocalProviderAdapter.SessionState<DummyOptions> {

}