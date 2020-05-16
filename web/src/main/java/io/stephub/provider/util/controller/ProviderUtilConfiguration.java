package io.stephub.provider.util.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.stephub.provider.util.controller.jackson.AnnotatedTypeToSchemaSerializer;
import io.stephub.provider.util.spring.StepMethodAnnotationProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.lang.reflect.AnnotatedType;

@Configuration
@ComponentScan(basePackageClasses = {ProviderUtilConfiguration.class})
public class ProviderUtilConfiguration {
    @Autowired
    private ObjectMapper objectMapper;

    @PostConstruct
    public void setUpObjectMapper() {
        final SimpleModule module = new SimpleModule();
        module.addSerializer(AnnotatedType.class, new AnnotatedTypeToSchemaSerializer());
        this.objectMapper.registerModule(module);
    }

    @Bean
    @Autowired
    public StepMethodAnnotationProcessor stepMethodAnnotationProcessor(final ConfigurableListableBeanFactory beanFactory) {
        return new StepMethodAnnotationProcessor(beanFactory);
    }
}
