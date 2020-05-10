package io.stephub.provider.util.spring;

import io.stephub.provider.api.ProviderException;
import io.stephub.provider.api.model.StepRequest;
import io.stephub.provider.api.model.StepResponse;
import io.stephub.provider.api.model.spec.ArgumentSpec;
import io.stephub.provider.api.model.spec.StepSpec;
import io.stephub.provider.util.LocalProviderAdapter;
import io.stephub.provider.util.spring.annotation.StepArgument;
import io.stephub.provider.util.spring.annotation.StepMethod;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.time.Duration;

@Component
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
            final StepSpec.StepSpecBuilder<Object> specBuilder = StepSpec.builder();
            specBuilder.
                    id(method.getName()).
                    pattern(stepMethodAno.pattern()).
                    patternType(stepMethodAno.patternType());
            provider.stepInvokers.put(method.getName(), buildInvoker(bean, method, specBuilder));
            provider.stepSpecs.add(specBuilder.build());
        }, method -> method.isAnnotationPresent(StepMethod.class));
    }

    private static SpringBeanProvider.StepInvoker<Object> buildInvoker(final Object bean, final Method stepMethod, final StepSpec.StepSpecBuilder<Object> specBuilder) {
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
                                    schema(parameter.getType()).
                                    build()
                    );
                } else {
                    throw new ProviderException("Unsatisfiable step method parameter [" + i + "] with name=" + parameter.getName());
                }
            }
            parameterAccessors[i] = accessor;
        }
        return (((sessionId, state, request) -> {
            final Object[] args = new Object[parameterAccessors.length];
            for (int i = 0; i < parameterAccessors.length; i++) {
                args[i] = parameterAccessors[i].getParameter(sessionId, state, request);
            }
            try {
                final long start = System.currentTimeMillis();
                final StepResponse<Object> response = (StepResponse<Object>) stepMethod.invoke(bean, args);
                if (response != null && response.getDuration() == null) {
                    response.setDuration(Duration.ofMillis(System.currentTimeMillis() - start));
                }
                return response;
            } catch (final IllegalAccessException | InvocationTargetException e) {
                throw new ProviderException("Failed to invoke step method=" + stepMethod.getName(), e);
            }
        }));
    }

    private interface ParameterAccessor {
        Object getParameter(String sessionId, LocalProviderAdapter.SessionState<?> state, StepRequest<Object> request);
    }


}
