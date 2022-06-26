package io.mkrzywanski.yadif.api;

public class YadifBeanInsantiationException extends RuntimeException {
    public YadifBeanInsantiationException(final Throwable cause) {
        super(cause);
    }

    public YadifBeanInsantiationException(final String message) {
        super(message);
    }
}
