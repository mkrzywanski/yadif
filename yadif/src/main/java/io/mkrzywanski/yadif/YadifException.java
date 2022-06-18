package io.mkrzywanski.yadif;

public class YadifException extends RuntimeException {
    YadifException(final String message) {
        super(message);
    }

    YadifException(final Throwable cause) {
        super(cause);
    }
}
