package io.mkrzywanski.yadif;

public record BeanId(String id) {

    static BeanId of(final String id) {
        return new BeanId(id);
    }

    static BeanId empty() {
        return new BeanId("");
    }

    boolean isEmpty() {
        return id.isEmpty();
    }
}
