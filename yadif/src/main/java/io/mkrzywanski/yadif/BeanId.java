package io.mkrzywanski.yadif;

record BeanId(String id) {
    static BeanId empty() {
        return new BeanId("");
    }

    boolean isEmpty() {
        return id.isEmpty();
    }
}
