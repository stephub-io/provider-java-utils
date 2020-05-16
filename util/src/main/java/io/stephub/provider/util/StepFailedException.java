package io.stephub.provider.util;

/**
 * Convenience exception to indicate the given step has failed conforming to expectations
 * - not crashed unexpectedly.
 */
public class StepFailedException extends RuntimeException {
    public StepFailedException(final String message) {
        super(message);
    }

    public StepFailedException(final Throwable cause) {
        super(cause);
    }
}
