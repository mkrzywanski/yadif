package io.mkrzywanski.yadif;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class BeanIntrospectionResult {

    private final Map<Class<?>, BeanCreationStrategy> strategyMap;

    BeanIntrospectionResult(final Map<Class<?>, BeanCreationStrategy> initial) {
        this.strategyMap = initial;
    }

    static BeanIntrospectionResult empty() {
        return new BeanIntrospectionResult(new HashMap<>());
    }

    BeanCreationStrategy get(final Class<?> clazz) {
        return strategyMap.get(clazz);
    }

    Map<Class<?>, List<Class<?>>> adjacency() {
        return strategyMap.entrySet()
                .stream().collect(Collectors.toMap(Map.Entry::getKey, e -> Arrays.asList(e.getValue().getParameterTypes())));
    }

    BeanIntrospectionResult merge(final BeanIntrospectionResult second) {
        final Map<Class<?>, BeanCreationStrategy> copy = new HashMap<>();
        copy.putAll(second.strategyMap);
        copy.putAll(this.strategyMap);
        return new BeanIntrospectionResult(copy);

    }
}
