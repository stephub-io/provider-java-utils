package io.stephub.provider.util.controller;

import io.stephub.provider.util.LocalProviderAdapter;
import io.stephub.provider.util.spring.SpringBeanProvider;

import java.lang.reflect.ParameterizedType;

public abstract class LocalSpringBeanProviderAdapter<S extends LocalProviderAdapter.SessionState<PO>, PO> extends SpringBeanProvider<S, PO, Class<?>, Object> {

    protected abstract Class<?> getOptionsSchemaClass();

    @Override
    protected final Class<?> getOptionsSchema() {
        return LocalSpringBeanProviderAdapter.this.getOptionsSchemaClass();
    }
}
