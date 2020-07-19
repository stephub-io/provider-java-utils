package io.stephub.provider.util.spring;

import io.stephub.provider.api.ProviderException;
import io.stephub.provider.api.model.StepRequest;
import io.stephub.provider.api.model.StepResponse;
import io.stephub.provider.api.model.spec.ArgumentSpec;
import io.stephub.provider.api.model.spec.OutputSpec;
import io.stephub.provider.api.model.spec.StepSpec;
import io.stephub.provider.util.LocalProviderAdapter;
import io.stephub.provider.util.StepFailedException;
import io.stephub.provider.util.spring.annotation.StepArgument;
import io.stephub.provider.util.spring.annotation.StepMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.time.Duration;

import static io.stephub.provider.api.model.StepResponse.StepStatus.*;

@Slf4j
public class StepMethodAnnotationProcessor implements BeanPostProcessor {

    private final ConfigurableListableBeanFactory configurableBeanFactory;

    @Autowired
    public StepMethodAnnotationProcessor(final ConfigurableListableBeanFactory beanFactory) {
        this.configurableBeanFactory = beanFactory;
    }

    @Override
    public Object postProcessBeforeInitialization(final Object bean, final String beanName)
            throws BeansException {
        this.scanForStepMethods(bean, beanName);
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(final Object bean, final String beanName)
            throws BeansException {
        return bean;
    }


    private void scanForStepMethods(final Object bean, final String beanName) {
        final Class<?> managedBeanClass = bean.getClass();
        ReflectionUtils.doWithMethods(managedBeanClass, method -> {
            final StepMethod stepMethodAno = method.getAnnotation(StepMethod.class);
            final Class<? extends SpringBeanProvider> providerClass = stepMethodAno.provider();
            final SpringBeanProvider<LocalProviderAdapter.SessionState<?>, ?, Object, Object> provider;
            if (!providerClass.equals(SpringBeanProvider.class)) {
                provider = this.configurableBeanFactory.getBean(providerClass);
            } else if (bean instanceof SpringBeanProvider) {
                provider = (SpringBeanProvider<LocalProviderAdapter.SessionState<?>, ?, Object, Object>) bean;
            } else {
                throw new ProviderException("Invalid usage of step method annotation or target provider isn't resolvable: " + method.toString());
            }
            String invokerName = method.getName();
            int i = 1;
            while (provider.stepInvokers.containsKey(invokerName)) {
                invokerName = method.getName() + "_" + (i++);
            }
            final StepSpec.StepSpecBuilder<Object> specBuilder = StepSpec.builder();
            specBuilder.
                    id(invokerName).
                    pattern(stepMethodAno.pattern()).
                    patternType(stepMethodAno.patternType());
            provider.stepInvokers.put(invokerName, this.buildInvoker(bean, method, specBuilder));
            provider.stepSpecs.add(specBuilder.build());
        }, method -> method.isAnnotationPresent(StepMethod.class));
    }

    private SpringBeanProvider.StepInvoker<Object> buildInvoker(final Object bean, final Method stepMethod, final StepSpec.StepSpecBuilder<Object> specBuilder) {
        final Parameter[] parameters = stepMethod.getParameters();
        final ParameterAccessor[] parameterAccessors = new ParameterAccessor[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            final Parameter parameter = parameters[i];
            ParameterAccessor accessor = null;
            if (LocalProviderAdapter.SessionState.class.isAssignableFrom(parameter.getType())) {
                accessor = ((sessionId, state, request) -> state);
            } else {
                final StepArgument expectedArgument = parameter.getAnnotation(StepArgument.class);
                if (expectedArgument != null) {
                    accessor = (((sessionId, state, request) ->
                    {
                        final Object value = request.getArguments().get(expectedArgument.name());
                        if (value == null) {
                            throw new ProviderException("Missing argument with name=" + expectedArgument.name());
                        }
                        return value;
                    }));
                    specBuilder.argument(
                            ArgumentSpec.builder().name(expectedArgument.name()).
                                    schema(this.wrapSchema(parameter.getAnnotatedType())).
                                    strict(expectedArgument.strict()).
                                    build()
                    );
                } else {
                    throw new ProviderException("Unsatisfiable step method parameter [" + i + "] with name=" + parameter.getName());
                }
            }
            parameterAccessors[i] = accessor;
        }
        final AnnotatedType returnType = stepMethod.getAnnotatedReturnType();
        if (returnType != null && !stepMethod.getReturnType().equals(Void.TYPE) &&
                !StepResponse.class.isAssignableFrom(stepMethod.getReturnType())) {
            specBuilder.output(OutputSpec.builder().schema(this.wrapSchema(returnType)).build());
        }
        return (((sessionId, state, request) -> {
            final Object[] args = new Object[parameterAccessors.length];
            for (int i = 0; i < parameterAccessors.length; i++) {
                args[i] = parameterAccessors[i].getParameter(sessionId, state, request);
            }
            final long start = System.currentTimeMillis();
            try {
                StepResponse<Object> stepResponse = null;
                final Object objResponse = stepMethod.invoke(bean, args);
                final long end = System.currentTimeMillis();
                if (objResponse instanceof StepResponse) {
                    stepResponse = (StepResponse<Object>) objResponse;
                } else {
                    stepResponse = new StepResponse<>();
                    stepResponse.setStatus(PASSED);
                    stepResponse.setOutput(objResponse);
                }
                if (stepResponse.getDuration() == null) {
                    stepResponse.setDuration(Duration.ofMillis(end - start));
                }
                return stepResponse;
            } catch (Throwable e) {
                if (e instanceof InvocationTargetException) {
                    e = e.getCause();
                }
                final StepResponse.StepStatus status = e instanceof StepFailedException ? FAILED : ERRONEOUS;
                if (status == ERRONEOUS) {
                    log.info("Failed to invoke step method=" + stepMethod.getName(), e);
                }
                return StepResponse.builder().status(status).errorMessage(e.getMessage()).
                        duration(Duration.ofMillis(System.currentTimeMillis() - start)).
                        build();
            }
        }));
    }

    protected Object wrapSchema(final AnnotatedType type) {
        return type;
    }

    private interface ParameterAccessor {
        Object getParameter(String sessionId, LocalProviderAdapter.SessionState<?> state, StepRequest<Object> request);
    }


}
