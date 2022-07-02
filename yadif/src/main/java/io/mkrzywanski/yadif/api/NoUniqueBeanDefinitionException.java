package io.mkrzywanski.yadif.api;

public class NoUniqueBeanDefinitionException extends YadifException {
    public NoUniqueBeanDefinitionException(final Throwable cause) {
        super(cause);
    }

    public NoUniqueBeanDefinitionException(final String message) {
        super(message);
    }
}
