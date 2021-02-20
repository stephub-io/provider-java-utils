package io.stephub.provider.util.spring.annotation;

public @interface StepDoc {
    String description() default "";

    StepDocExample[] examples() default {};

    public @interface StepDocExample {
        String value() default "";

        String description() default "";
    }
}
