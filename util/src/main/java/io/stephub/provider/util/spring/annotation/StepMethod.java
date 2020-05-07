package io.stephub.provider.util.spring.annotation;

import io.stephub.provider.api.model.spec.PatternType;
import io.stephub.provider.util.spring.SpringBeanProvider;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface StepMethod {
    String pattern();

    Class<? extends SpringBeanProvider> provider() default SpringBeanProvider.class;

    PatternType patternType() default PatternType.REGEX;
}
