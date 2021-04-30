package io.stephub.provider.util.spring;

import io.stephub.provider.api.model.LogEntry;
import io.stephub.provider.api.model.ProviderOptions;
import io.stephub.provider.api.model.StepRequest;
import io.stephub.provider.api.model.StepResponse;
import io.stephub.provider.api.model.spec.StepSpec;
import io.stephub.provider.util.LocalProviderAdapter;
import io.stephub.provider.util.spring.annotation.*;
import lombok.experimental.SuperBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.stereotype.Component;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static io.stephub.provider.api.model.StepResponse.StepStatus.ERRONEOUS;
import static java.time.Duration.ofMinutes;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {StepMethodAnnotationProcessor.class, StepMethodAnnotationProcessorTest.SomeBean.class})
class StepMethodAnnotationProcessorTest {

    public static class TestProvider extends SpringBeanProvider<TestState, Object, Class<?>, Object> {
        private final TestProvider mock = mock(TestProvider.class);

        {
            try {
                when(this.mock.testStepNoArgs()).thenReturn(new StepResponse());
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }

        private TestState state;

        @StepMethod(pattern = "Bla bla")
        public StepResponse<Object> testStepNoArgs() throws InterruptedException {
            Thread.sleep(1000);
            return this.mock.testStepNoArgs();
        }

        @StepMethod(pattern = "Bla bla multiple")
        public StepResponse<Object> testStepMultipleArgs(final TestState someState,
                                                         @StepArgument(name = "enabled") final boolean arg1,
                                                         @StepArgument(name = "data") final String arg2) {
            return this.mock.testStepMultipleArgs(someState, arg1, arg2);
        }

        @StepMethod(pattern = "Doc with arg")
        public StepResponse<Object> testPayloadDocString(final TestState someState,
                                                         @StepDocString(doc = @StepDoc(
                                                                 examples = {@StepDoc.StepDocExample(
                                                                         value = "abc",
                                                                         description = "def"
                                                                 )}
                                                         )) final String doc,
                                                         @StepArgument(name = "arg") final String arg) {
            return this.mock.testPayloadDocString(someState, doc, arg);
        }

        @StepMethod(pattern = "Data table with arg")
        public StepResponse<Object> testPayloadDataTable(final TestState someState,
                                                         @StepDataTable(
                                                                 header = true,
                                                                 columns = {@StepColumn(name = "col1", type = boolean.class)}
                                                         ) final List<Map<String, ?>> dataTable,
                                                         @StepArgument(name = "arg") final String arg) {
            return this.mock.testPayloadDataTable(someState, dataTable, arg);
        }

        @StepMethod(pattern = "Dup1")
        public StepResponse<Object> dup() {
            return this.mock.dup();
        }

        @StepMethod(pattern = "Dup2")
        public StepResponse<Object> dup(@StepArgument(name = "data") final String arg) {
            return this.mock.dup(arg);
        }

        @StepMethod(pattern = "testLogs")
        public void testLogs(final StepExecutionContext sec) {
            sec.addLog(LogEntry.builder().message("Hello").build());
        }

        @Override
        protected TestState startState(final String sessionId, final ProviderOptions<Object> options) {
            this.state = mock(TestState.class);
            return this.state;
        }

        @Override
        protected void stopState(final TestState state) {

        }

        @Override
        public String getName() {
            return "test";
        }

        @Override
        public Class<Object> getOptionsSchema() {
            return Object.class;
        }
    }

    @SuperBuilder
    public static class TestState extends LocalProviderAdapter.SessionState<Object> {

    }

    @Component
    public static class SomeBean {
        private final SomeBean mock = mock(SomeBean.class);

        @StepMethod(pattern = "Bla bla blub", provider = TestProvider.class)
        public StepResponse<Object> testStepExternalNoArgs() {
            return this.mock.testStepExternalNoArgs();
        }
    }

    @SpyBean
    private TestProvider testProvider;

    @Autowired
    private SomeBean externalBean;

    @Test
    public void testStepNoArgs() throws InterruptedException {
        final String sid = this.testProvider.createSession(ProviderOptions.builder().sessionTimeout(ofMinutes(1)).build());
        final StepResponse response = this.testProvider.execute(sid, StepRequest.builder().id("testStepNoArgs").build());
        verify(this.testProvider.mock).testStepNoArgs();
        assertThat(response.getDuration().getSeconds(), greaterThanOrEqualTo(1l));
    }

    @Test
    @DirtiesContext
    public void testInterceptorsRewritingRequestAndResponse() throws InterruptedException {
        final String sid = this.testProvider.createSession(ProviderOptions.builder().sessionTimeout(ofMinutes(1)).build());
        final AtomicBoolean interceptor1Called = new AtomicBoolean(false);
        this.testProvider.addInterceptor(chain -> {
            interceptor1Called.set(true);
            final StepResponse<Object> response = chain.proceed(chain.request());
            response.setDuration(Duration.ofHours(1));
            return response;
        });
        this.testProvider.addInterceptor(chain -> chain.proceed(StepRequest.builder().id("testStepNoArgs").build()));
        final StepResponse response = this.testProvider.execute(sid, StepRequest.builder().id("unknown").build());
        verify(this.testProvider.mock).testStepNoArgs();
        assertThat(interceptor1Called.get(), equalTo(true));
        assertThat(response.getDuration(), equalTo(Duration.ofHours(1)));
    }

    @Test
    @DirtiesContext
    public void testStepErroneous() throws InterruptedException {
        when(this.testProvider.mock.testStepNoArgs()).thenThrow(new RuntimeException("Doesn't work"));
        final String sid = this.testProvider.createSession(ProviderOptions.builder().sessionTimeout(ofMinutes(1)).build());
        final StepResponse response = this.testProvider.execute(sid, StepRequest.builder().id("testStepNoArgs").build());
        verify(this.testProvider.mock).testStepNoArgs();
        assertThat(response.getStatus(), equalTo(ERRONEOUS));
        assertThat(response.getErrorMessage(), equalTo("Doesn't work"));
        assertThat(response.getDuration().getSeconds(), greaterThanOrEqualTo(1l));
    }

    @Test
    public void testStepMultipleArgs() {
        final String sid = this.testProvider.createSession(ProviderOptions.builder().sessionTimeout(ofMinutes(1)).build());
        this.testProvider.execute(sid, StepRequest.builder().
                id("testStepMultipleArgs").
                argument("data", "dddd").
                argument("enabled", true).
                build());
        verify(this.testProvider.mock).testStepMultipleArgs(
                this.testProvider.state,
                true,
                "dddd"
        );
    }

    @Test
    public void testExternalStepNoArgs() {
        final String sid = this.testProvider.createSession(ProviderOptions.builder().sessionTimeout(ofMinutes(1)).build());
        this.testProvider.execute(sid, StepRequest.builder().id("testStepExternalNoArgs").build());
        verify(this.externalBean.mock).testStepExternalNoArgs();
    }

    @Test
    public void testDuplicateNamedStepMethods() throws InterruptedException {
        final String sid = this.testProvider.createSession(ProviderOptions.builder().sessionTimeout(ofMinutes(1)).build());
        assertThat(this.testProvider.stepInvokers, hasKey("dup"));
        assertThat(this.testProvider.stepInvokers, hasKey("dup_1"));
    }

    @Test
    public void testStepDocWithArg() {
        final String sid = this.testProvider.createSession(ProviderOptions.builder().sessionTimeout(ofMinutes(1)).build());
        this.testProvider.execute(sid, StepRequest.builder().
                id("testPayloadDocString").
                argument("arg", "dddd").
                docString("my doc").
                build());
        verify(this.testProvider.mock).testPayloadDocString(
                this.testProvider.state,
                "my doc",
                "dddd"
        );

        // Verify spec
        final StepSpec<Class<?>> stepSpec = this.testProvider.getInfo().getSteps().stream().filter(classStepSpec -> classStepSpec.getId().equals("testPayloadDocString")).findFirst().get();
        assertThat(stepSpec.getPayload(), equalTo(StepSpec.PayloadType.DOC_STRING));
        assertThat(stepSpec.getDocString().getDoc().getExamples(), hasSize(1));
        assertThat(stepSpec.getDocString().getDoc().getExamples().get(0).getValue(), equalTo("abc"));
        assertThat(stepSpec.getDocString().getDoc().getExamples().get(0).getDescription(), equalTo("def"));
    }


    @Test
    public void testStepDataTableWithArg() {
        final String sid = this.testProvider.createSession(ProviderOptions.builder().sessionTimeout(ofMinutes(1)).build());
        this.testProvider.execute(sid, StepRequest.builder().
                id("testPayloadDataTable").
                argument("arg", "dddd").
                dataTable(Collections.singletonList(Collections.singletonMap("col1", Boolean.TRUE))).
                build());
        verify(this.testProvider.mock).testPayloadDataTable(
                this.testProvider.state,
                Collections.singletonList(Collections.singletonMap("col1", Boolean.TRUE)),
                "dddd"
        );
        final StepSpec<Class<?>> stepSpec = this.testProvider.getInfo().getSteps().stream().filter(classStepSpec -> classStepSpec.getId().equals("testPayloadDataTable")).findFirst().get();
        // Spec arg
        assertThat(stepSpec.getArguments(), hasSize(1));
        assertThat(stepSpec.getArguments().get(0).getName(), equalTo("arg"));
        assertThat(stepSpec.getArguments().get(0).getSchema(), equalTo(String.class));

        // Spec data table
        assertThat(stepSpec.getPayload(), equalTo(StepSpec.PayloadType.DATA_TABLE));
        assertThat(stepSpec.getDataTable().isHeader(), equalTo(true));
        assertThat(stepSpec.getDataTable().getColumns(), hasSize(1));
        assertThat(stepSpec.getDataTable().getColumns().get(0).getName(), equalTo("col1"));
        assertThat(stepSpec.getDataTable().getColumns().get(0).getSchema(), equalTo(boolean.class));
    }

    @Test
    public void testLogs() {
        final String sid = this.testProvider.createSession(ProviderOptions.builder().sessionTimeout(ofMinutes(1)).build());
        final StepResponse<Object> response = this.testProvider.execute(sid, StepRequest.builder().id("testLogs").build());
        assertThat(response.getLogs(), hasSize(1));
        assertThat(response.getLogs().get(0).getMessage(), equalTo("Hello"));
    }

    @Test
    public void testDistinctLogs() {
        final String sid = this.testProvider.createSession(ProviderOptions.builder().sessionTimeout(ofMinutes(1)).build());
        this.testProvider.execute(sid, StepRequest.builder().id("testLogs").build());
        final StepResponse<Object> response = this.testProvider.execute(sid, StepRequest.builder().id("testLogs").build());
        assertThat(response.getLogs(), hasSize(1));
        assertThat(response.getLogs().get(0).getMessage(), equalTo("Hello"));
    }

}