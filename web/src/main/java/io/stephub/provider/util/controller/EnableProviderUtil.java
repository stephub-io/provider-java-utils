package io.stephub.provider.util.controller;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({ProviderUtilConfiguration.class})
public @interface EnableProviderUtil {
}
