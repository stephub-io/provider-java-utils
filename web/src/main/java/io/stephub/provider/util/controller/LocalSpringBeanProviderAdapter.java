package io.stephub.provider.util.controller;

import io.stephub.provider.util.LocalProviderAdapter;
import io.stephub.provider.util.spring.SpringBeanProvider;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Type;

public abstract class LocalSpringBeanProviderAdapter<S extends LocalProviderAdapter.SessionState<PO>, PO> extends SpringBeanProvider<S, PO, AnnotatedType, Object> {

    protected abstract Class<?> getOptionsSchemaClass();

    @Override
    protected final AnnotatedType getOptionsSchema() {
        return new AnnotatedType() {
            @Override
            public Type getType() {
                return LocalSpringBeanProviderAdapter.this.getOptionsSchemaClass();
            }

            @Override
            public <T extends Annotation> T getAnnotation(final Class<T> aClass) {
                return LocalSpringBeanProviderAdapter.this.getOptionsSchemaClass().getAnnotation(aClass);
            }

            @Override
            public Annotation[] getAnnotations() {
                return LocalSpringBeanProviderAdapter.this.getOptionsSchemaClass().getAnnotations();
            }

            @Override
            public Annotation[] getDeclaredAnnotations() {
                return LocalSpringBeanProviderAdapter.this.getOptionsSchemaClass().getDeclaredAnnotations();
            }
        };
    }
}
