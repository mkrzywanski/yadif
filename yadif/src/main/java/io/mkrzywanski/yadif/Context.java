package io.mkrzywanski.yadif;

import io.mkrzywanski.yadif.api.NoUniqueBeanDefinitionException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Context {

    private final Map<String, List<Object>> beans;

    public Context() {
        beans = new HashMap<>();
    }

    void add(final String name, final Object object) {
        beans.computeIfAbsent(name, s -> new ArrayList<>()).add(object);
    }

    public <T> Optional<T> getInstance(final String fullClassName, final Class<T> clazz) {
        final List<Object> bean = beans.get(fullClassName);
        if (bean == null || bean.isEmpty()) {
            return Optional.empty();
        }
        if (bean.size() > 1) {
            throw new NoUniqueBeanDefinitionException();
        }
        final Object o = bean.get(0);
        final T t = clazz.isInstance(o) ? clazz.cast(o) : null;
        return Optional.ofNullable(t);
    }

    public <T> List<T> getByType(final Class<T> clazz) {
        final String canonicalName = clazz.getCanonicalName();
        return Optional.ofNullable(beans.get(canonicalName))
                .map(objects -> objects.stream().map(clazz::cast).toList())
                .orElseGet(ArrayList::new);
    }
}
