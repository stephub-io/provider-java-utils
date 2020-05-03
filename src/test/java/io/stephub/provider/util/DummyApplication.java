package io.stephub.provider.util;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
public class DummyApplication {
    public static void main(final String[] args) {
        SpringApplication.run(DummyApplication.class, args);
    }

}