package io.stephub.provider.util.spring.annotation;

public @interface StepDoc {
    String description() default "";

    String[] examples() default {};
}
