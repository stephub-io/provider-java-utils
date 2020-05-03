package io.stephub.provider.util.controller;

import io.stephub.provider.util.LocalProviderAdapter;
import io.stephub.provider.util.model.ProviderOptions;
import io.stephub.provider.util.model.StepResponse;
import io.stephub.provider.util.spring.SpringBeanProvider;
import io.stephub.provider.util.spring.StepMethodAnnotationProcessor;
import io.stephub.provider.util.spring.annotation.StepArgument;
import io.stephub.provider.util.spring.annotation.StepMethod;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = GenericProviderController.class)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {GenericProviderController.class, StepMethodAnnotationProcessor.class})
public class GenericProviderControllerTest {

    @SpyBean
    private DummyProvider provider;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testProviderInfo() throws Exception {
        this.mockMvc.perform(get("/")).
                andDo(print()).andExpect(status().isOk()).
                andExpect(jsonPath("$.name", is(this.provider.getName()))).
                andExpect(jsonPath("$.version", notNullValue())).
                andExpect(jsonPath("$.optionsSchema").isMap()).
                andExpect(jsonPath("$.optionsSchema.type", is("object"))).
                andExpect(jsonPath("$.optionsSchema.properties", aMapWithSize(1))).
                andExpect(jsonPath("$.optionsSchema.properties.baseUrl.type", is("string"))).
                andExpect(jsonPath("$.steps").isArray()).
                andExpect(jsonPath("$.steps", hasSize(1)));
    }

    public static class DummyProvider extends SpringBeanProvider<DummyState, DummyOptions> {
        private final DummyProvider mock = mock(DummyProvider.class);

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
            return this.mock.startState(sessionId, options);
        }

        @Override
        protected void stopState(final DummyState state) {
            this.mock.stopState(state);
        }

        @StepMethod(pattern = ".*")
        public StepResponse step1(@StepArgument(name = "arg1") final boolean arg1,
                                  @StepArgument(name = "arg2") final String arg2) {
            return this.mock.step1(arg1, arg2);
        }
    }

    @Data
    public static class DummyOptions {
        private String baseUrl;
    }

    @SuperBuilder
    public static class DummyState extends LocalProviderAdapter.SessionState<DummyOptions> {

    }
}