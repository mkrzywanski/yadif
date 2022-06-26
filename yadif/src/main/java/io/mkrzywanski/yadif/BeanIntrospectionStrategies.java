package io.mkrzywanski.yadif;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class BeanIntrospectionStrategies {

    private final Map<Class<?>, BeanCreationStrategy> strategyMap;

    BeanIntrospectionStrategies(final Map<Class<?>, BeanCreationStrategy> initial) {
        this.strategyMap = initial;
    }

    static BeanIntrospectionStrategies empty() {
        return new BeanIntrospectionStrategies(new HashMap<>());
    }

    BeanCreationStrategy get(final Class<?> clazz) {
        return strategyMap.get(clazz);
    }

    static BeanIntrospectionStrategies withInitial(final Map<Class<?>, BeanCreationStrategy> initial) {
        return new BeanIntrospectionStrategies(initial);
    }

    Map<Class<?>, List<Class<?>>> adjacency() {
        return strategyMap.entrySet()
                .stream().collect(Collectors.toMap(Map.Entry::getKey, e -> Arrays.asList(e.getValue().getParameterTypes())));
    }


    BeanIntrospectionStrategies merge(final BeanIntrospectionStrategies second) {
        final Map<Class<?>, BeanCreationStrategy> copy = new HashMap<>();
        copy.putAll(second.strategyMap);
        copy.putAll(this.strategyMap);
        return new BeanIntrospectionStrategies(copy);

    }
}
