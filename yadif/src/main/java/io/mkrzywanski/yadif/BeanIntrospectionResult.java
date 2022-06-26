package io.mkrzywanski.yadif;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class BeanIntrospectionResult {

    private final Map<Bean, BeanCreationStrategy> strategyMap;

    BeanIntrospectionResult(final Map<Bean, BeanCreationStrategy> initial) {
        this.strategyMap = initial;
    }

    static BeanIntrospectionResult empty() {
        return new BeanIntrospectionResult(new HashMap<>());
    }

    BeanCreationStrategy get(final Bean bean) {
        return strategyMap.get(bean);
    }

    Map<Bean, List<Bean>> adjacency() {
        return strategyMap.entrySet()
                .stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().dependencies()));
    }

    BeanIntrospectionResult merge(final BeanIntrospectionResult second) {
        final Map<Bean, BeanCreationStrategy> copy = new HashMap<>();
        copy.putAll(second.strategyMap);
        copy.putAll(this.strategyMap);
        return new BeanIntrospectionResult(copy);

    }
}
