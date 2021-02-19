package io.stephub.provider.util.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.stephub.provider.api.model.ProviderInfo;
import io.stephub.provider.api.model.ProviderOptions;
import io.stephub.provider.api.model.StepRequest;
import io.stephub.provider.api.model.StepResponse;
import io.stephub.provider.api.model.spec.StepSpec;
import io.stephub.provider.util.LocalProviderAdapter;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@Slf4j
public class GenericProviderController {
    @Autowired
    private LocalSpringBeanProviderAdapter<? extends LocalProviderAdapter.SessionState<? extends Object>, ? extends Object> provider;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping(value = "/", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public ProviderInfo<Class<?>> getProviderInfo() {
        return this.provider.getInfo();
    }

    @PostMapping(value = "/sessions", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public StartedSession createSession(@RequestBody final ProviderOptions<Object> options) {
        options.setOptions(this.objectMapper.convertValue(options.getOptions(), this.provider.getInfo().getOptionsSchema()));
        return StartedSession.builder().id(this.provider.createSession((ProviderOptions) options)).build();
    }

    @DeleteMapping(value = "/sessions/{sid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSession(@PathVariable("sid") final String sid) {
        this.provider.destroySession(sid);
    }

    @PostMapping(value = "/sessions/{sid}/execute")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public StepResponse<Object> executeStep(@PathVariable("sid") final String sid, @Valid @RequestBody final StepRequest<Object> request) {
        final StepSpec<Class<?>> stepSpec = this.provider.getInfo().getSteps().stream().filter(s -> s.getId().equals(request.getId())).findFirst().
                orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Step not found for id=" + request.getId()));
        final Map<String, Object> dArguments = new HashMap<>();
        stepSpec.getArguments().forEach(
                as -> {
                    final Object inputValue = request.getArguments().get(as.getName());
                    if (inputValue != null) {
                        dArguments.put(as.getName(), this.objectMapper.convertValue(inputValue, as.getSchema()));
                    } else {
                        dArguments.put(as.getName(), null);
                    }
                }
        );
        final Collection<String> missingArgs = request.getArguments().keySet();
        missingArgs.removeAll(dArguments.keySet());
        if (!missingArgs.isEmpty()) {
            log.warn("Unexpected arguments passed to step={}: {}", request.getId(), missingArgs);
        }
        request.setArguments(dArguments);
        return this.provider.execute(sid, (StepRequest) request);
    }

    @Data
    @Builder
    public static class StartedSession {
        private String id;

    }
}
