package io.mkrzywanski.yadif;

import io.mkrzywanski.yadif.api.CyclePath;
import io.mkrzywanski.yadif.api.DependencyCycleDetectedException;
import io.mkrzywanski.yadif.api.YadifException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

class FromConfigContextFactory {

    private final TopologicalSort<Class<?>> topologicalSort = new KahnTopologicalSort();
    private final DfsGraphCycleDetecting cycleDetecting = new DfsGraphCycleDetecting();
    private final List<BeanIntrospectionStrategy> strategies;

    FromConfigContextFactory() {
        this.strategies = Arrays.asList(new IntrospectConfigClass(), new IntrospectPackageScan());
    }

    Context fromConfig(final Class<?> clazz) {
        try {
            final Constructor<?> constructor = clazz.getConstructor();
            final Object o = constructor.newInstance();
            return fromConfig(o);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new YadifException(e);
        }
    }

    Context fromConfig(final Object object) {
        Objects.requireNonNull(object);
        final BeanIntrospectionStrategies beanCreationStrategies = introspectConfiguration(object);

        final Graph dependencyGraph = new Graph(beanCreationStrategies.adjacency());

        detectCycles(dependencyGraph);

        final var orderedDependencies = topologicalSort.sort(dependencyGraph);

        final Map<Class<?>, Object> initializedBeans = initializeBeans(beanCreationStrategies, orderedDependencies);

        return createContext(initializedBeans);
    }

    private BeanIntrospectionStrategies introspectConfiguration(final Object object) {

        final var introspectionStrategies = strategies.stream()
                .map(beanIntrospectionStrategy -> beanIntrospectionStrategy.introspect(object))
                .toList();

        assert !introspectionStrategies.isEmpty();

        return introspectionStrategies.stream()
                .reduce(BeanIntrospectionStrategies.empty(), BeanIntrospectionStrategies::merge);
    }

    private Map<Class<?>, Object> initializeBeans(final BeanIntrospectionStrategies strategies, final List<Class<?>> orderedDependencies) {
        final Map<Class<?>, Object> initializedBeans = new HashMap<>();

        for (Class<?> beanType : orderedDependencies) {
            final BeanCreationStrategy initializingMethod = strategies.get(beanType);
            final int parameterCount = initializingMethod.getParameterCount();
            final Object[] args = new Object[parameterCount];
            for (int i = 0; i < parameterCount; i++) {
                args[i] = initializedBeans.get(initializingMethod.getParameterTypes()[i]);
            }
            final Object constructedBean = initializingMethod.invoke(args);
            initializedBeans.put(beanType, constructedBean);
        }
        return initializedBeans;
    }

    private void detectCycles(final Graph graph) {
        final Set<Path> cycles = cycleDetecting.detectCycles(graph);

        if (!cycles.isEmpty()) {
            final Set<CyclePath> cyclePaths = cycles.stream()
                    .map(path -> new CyclePath(path.getPath()))
                    .collect(Collectors.toSet());
            throw new DependencyCycleDetectedException(cyclePaths);
        }
    }

    private Context createContext(final Map<Class<?>, Object> initializedBeans) {
        final var context = new Context();
        initializedBeans.forEach((key, value) -> context.add(key.getTypeName(), value));
        return context;
    }
}
