package io.mkrzywanski.yadif.api;

public class YadifException extends RuntimeException {

    public YadifException(final Throwable cause) {
        super(cause);
    }

    public YadifException(final String message) {
        super(message);
    }
}
