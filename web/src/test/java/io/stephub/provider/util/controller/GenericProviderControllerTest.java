package io.stephub.provider.util.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.stephub.provider.api.model.ProviderOptions;
import io.stephub.provider.api.model.StepRequest;
import io.stephub.provider.api.model.StepResponse;
import io.stephub.provider.util.LocalProviderAdapter;
import io.stephub.provider.util.spring.SpringBeanProvider;
import io.stephub.provider.util.spring.annotation.StepArgument;
import io.stephub.provider.util.spring.annotation.StepMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;

import static io.stephub.provider.api.model.StepResponse.StepStatus.PASSED;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = GenericProviderController.class)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ProviderUtilConfiguration.class})
public class GenericProviderControllerTest {

    @SpyBean
    private DummyProvider provider;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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

    @Test
    public void testCreateSession() throws Exception {
        final ProviderOptions<DummyOptions> options = new ProviderOptions<>();
        options.setSessionTimeout(Duration.ofMinutes(3));
        options.setOptions(DummyOptions.builder().baseUrl("https://stephub.io").build());
        final AtomicReference<String> sid = new AtomicReference<>();
        when(this.provider.mock.startState(argThat(s -> {
            sid.set(s);
            return true;
        }), eq(options))).thenReturn(DummyState.builder().build());

        this.mockMvc.perform(post("/sessions").
                contentType(MediaType.APPLICATION_JSON).
                content(this.objectMapper.writeValueAsBytes(options))).
                andDo(print()).andExpect(status().isCreated()).
                andExpect(jsonPath("$.id", is(sid.get())));
    }

    @Test
    public void testDestroySession() throws Exception {
        final DummyState state = DummyState.builder().build();
        when(this.provider.mock.startState(anyString(), ArgumentMatchers.any(ProviderOptions.class))).
                thenReturn(state);
        final String sid = this.provider.createSession(new ProviderOptions<>());
        this.mockMvc.perform(delete("/sessions/" + sid)).
                andDo(print()).andExpect(status().isNoContent());
        verify(this.provider.mock, times(1)).
                stopState(state);
    }

    @Test
    public void testStepExecution() throws Exception {
        // Given
        final DummyState state = DummyState.builder().build();
        when(this.provider.mock.startState(anyString(), ArgumentMatchers.any(ProviderOptions.class))).
                thenReturn(state);
        final String sid = this.provider.createSession(new ProviderOptions<>());
        when(this.provider.mock.step1(true, "Hello")).thenReturn(
                StepResponse.builder().status(PASSED).
                        output("abc", 175).
                        build()
        );

        // Call & expect
        this.mockMvc.perform(post("/sessions/" + sid + "/execute").
                contentType(MediaType.APPLICATION_JSON).
                content(this.objectMapper.writeValueAsBytes(
                        StepRequest.builder().id("step1").
                                argument("arg1", true).
                                argument("arg2", "Hello").
                                build()
                ))).
                andDo(print()).andExpect(status().isOk()).
                andExpect(jsonPath("$.status", is(PASSED.toString()))).
                andExpect(jsonPath("$.duration", Matchers.startsWith("PT"))).
                andExpect(jsonPath("$.outputs").isMap()).
                andExpect(jsonPath("$.outputs.abc", is(175)));
        verify(this.provider.mock, times(1)).
                step1(true, "Hello");
    }

    public static class DummyProvider extends SpringBeanProvider<DummyState, DummyOptions, Class<?>, Object> {
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
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DummyOptions {
        private String baseUrl;
    }

    @SuperBuilder
    public static class DummyState extends LocalProviderAdapter.SessionState<DummyOptions> {

    }
}