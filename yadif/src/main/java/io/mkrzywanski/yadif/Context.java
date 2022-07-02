package io.mkrzywanski.yadif;

import io.mkrzywanski.yadif.api.BeanWithId;
import io.mkrzywanski.yadif.api.NoUniqueBeanDefinitionException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Context {

    private final Map<String, List<BeanWithId>> beans;

    public Context() {
        beans = new HashMap<>();
    }

    void add(final String name, final BeanWithId object) {
        beans.computeIfAbsent(name, s -> new ArrayList<>()).add(object);
    }

    public <T> Optional<T> getInstance(final String fullClassName, final Class<T> clazz) {
        final List<BeanWithId> beansWithIds = this.beans.get(fullClassName);
        if (beansWithIds == null || beansWithIds.isEmpty()) {
            return Optional.empty();
        }
        if (beansWithIds.size() > 1) {
            throw new NoUniqueBeanDefinitionException("");
        }
        final BeanWithId beanWithId = beansWithIds.get(0);
        final Object bean = beanWithId.bean();
        final T beanInstance = clazz.isInstance( bean) ? clazz.cast( bean) : null;
        return Optional.ofNullable(beanInstance);
    }

    public <T> List<T> getByType(final Class<T> clazz) {
        final String canonicalName = clazz.getCanonicalName();
        return Optional.ofNullable(beans.get(canonicalName))
                .map(objects -> objects.stream().map(BeanWithId::bean).map(clazz::cast).toList())
                .orElseGet(ArrayList::new);
    }

}
