package io.mkrzywanski.yadif;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Context {

    private final Map<String, Object> beans;

    public Context() {
        beans = new HashMap<>();
    }

    void add(final String name, final Object object) {
        beans.put(name, object);
    }

    public <T> Optional<T> getInstance(final String fullClassName, final Class<T> clazz) {
        final Object bean = beans.get(fullClassName);
        return Optional.ofNullable(bean).map(instance -> clazz.isInstance(instance) ? clazz.cast(instance) : null);
    }
}
