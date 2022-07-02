package io.mkrzywanski.yadif;

import io.mkrzywanski.yadif.api.BeanWithId;
import io.mkrzywanski.yadif.api.CyclePath;
import io.mkrzywanski.yadif.api.DependencyCycleDetectedException;
import io.mkrzywanski.yadif.api.YadifBeanInsantiationException;
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

    private final TopologicalSort<Bean> topologicalSort = new KahnTopologicalSort();
    private final DfsGraphCycleDetecting cycleDetecting = new DfsGraphCycleDetecting();
    private final List<BeanIntrospectionStrategy> strategies;

    FromConfigContextFactory() {
        this.strategies = Arrays.asList(new IntrospectConfigClass(), new IntrospectPackageScan());
    }

    Context fromConfig(final Class<?> clazz) {
        try {
            final Constructor<?> constructor = clazz.getConstructor();
            final Object configInstance = constructor.newInstance();
            return fromConfig(configInstance);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new YadifException(e);
        }
    }

    Context fromConfig(final Object object) {
        Objects.requireNonNull(object);
        final BeanIntrospectionResult beanIntrospectionResult = introspectConfiguration(object);

        final Graph dependencyGraph = new Graph(beanIntrospectionResult.adjacency());

        ensureAllNodesCanBeCreated(dependencyGraph);
        detectCycles(dependencyGraph);

        final var orderedDependencies = topologicalSort.sort(dependencyGraph);

        final Map<Bean, Object> initializedBeans = initializeBeans(beanIntrospectionResult, orderedDependencies);

        return createContext(initializedBeans);
    }

    private void ensureAllNodesCanBeCreated(final Graph dependencyGraph) {
        final Set<Bean> nodes = dependencyGraph.nodes();
        for (Bean node : nodes) {
            final var adjacentNodes = dependencyGraph.getAdjacentNodes(node);
            for (Bean b : adjacentNodes) {
                final boolean hasExactMatch = nodes.contains(b);
                if (hasExactMatch) {
                    continue;
                }
                if (nodes.stream().map(Bean::type).collect(Collectors.toSet()).contains(b.type())) {
                    continue;
                }
                throw new YadifBeanInsantiationException("Not all required dependencies found when creating context");

            }
        }
    }

    private BeanIntrospectionResult introspectConfiguration(final Object object) {

        final var introspectionStrategies = strategies.stream()
                .map(beanIntrospectionStrategy -> beanIntrospectionStrategy.introspect(object))
                .toList();

        assert !introspectionStrategies.isEmpty();

        return introspectionStrategies.stream()
                .reduce(BeanIntrospectionResult.empty(), BeanIntrospectionResult::merge);
    }

    private Map<Bean, Object> initializeBeans(final BeanIntrospectionResult strategies, final List<Bean> orderedDependencies) {
        final Map<Bean, Object> initializedBeans = new HashMap<>();

        for (Bean beanType : orderedDependencies) {
            final BeanCreationStrategy creationStrategy = strategies.get(beanType);
            final var dependencies = creationStrategy.dependencies();
            final int parameterCount = dependencies.size();
            final Object[] args = new Object[parameterCount];
            for (int i = 0; i < parameterCount; i++) {
                final Bean dependency = dependencies.get(i);
                args[i] = initializedBeans.get(new Bean(dependency.type(), dependency.id()));
            }
            final Object constructedBean = creationStrategy.invoke(args);
            initializedBeans.put(beanType, constructedBean);
        }
        return initializedBeans;
    }

    private void detectCycles(final Graph graph) {
        final Set<Path> cycles = cycleDetecting.detectCycles(graph);

        if (!cycles.isEmpty()) {
            final Set<CyclePath> cyclePaths = cycles.stream()
                    .map(path -> new CyclePath(path.getPath().stream().map(Bean::type).collect(Collectors.toList())))
                    .collect(Collectors.toSet());
            throw new DependencyCycleDetectedException(cyclePaths);
        }
    }

    private Context createContext(final Map<Bean, Object> initializedBeans) {
        final var context = new Context();
        initializedBeans.forEach((key, value) -> context.add(key.type().getTypeName(), new BeanWithId(key.id(), value)));
        return context;
    }
}
