package io.stephub.provider.util;

import io.stephub.provider.util.model.ProviderOptions;
import io.stephub.provider.util.model.StepResponse;
import io.stephub.provider.util.spring.SpringBeanProvider;
import io.stephub.provider.util.spring.StepMethodAnnotationProcessor;
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

    @StepMethodAnnotationProcessor.StepMethod(pattern = ".*")
    public StepResponse step1(@StepMethodAnnotationProcessor.StepArgument(name = "arg1") boolean arg1,
                              @StepMethodAnnotationProcessor.StepArgument(name = "arg2") String arg2) {
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