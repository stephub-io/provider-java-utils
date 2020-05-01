package io.stephub.provider.util.controller;

import io.stephub.provider.util.model.ProviderInfo;
import io.stephub.provider.util.spring.SpringBeanProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class GenericProviderController {
    @Autowired
    private SpringBeanProvider<?, ?> provider;

    @GetMapping(value = "/", produces = APPLICATION_JSON_VALUE)
    public ProviderInfo getProviderInfo() {
        return provider.getInfo();
    }
}
