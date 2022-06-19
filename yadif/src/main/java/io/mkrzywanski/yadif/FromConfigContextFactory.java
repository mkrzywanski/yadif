package io.mkrzywanski.yadif;

import io.mkrzywanski.yadif.annotation.Instance;
import io.mkrzywanski.yadif.api.CyclePath;
import io.mkrzywanski.yadif.api.YadifException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

class FromConfigContextFactory {

    private final TopologicalSort<Class<?>> topologicalSort = new KahnTopologicalSort();
    private final DfsGraphCycleDetecting cycleDetecting = new DfsGraphCycleDetecting();

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
        final var factoryMethods = getFactoryMethods(object);

        final Map<Class<?>, List<Class<?>>> adjacencyMatrix = factoryMethods.stream()
                .collect(Collectors.toMap(Method::getReturnType, method -> Arrays.asList(method.getParameterTypes())));
        final Graph graph = new Graph(adjacencyMatrix);

        detectCycles(graph);

        final var orderedDependencies = topologicalSort.sort(graph);

        final var beanToFactoryMethod = factoryMethods.stream().collect(Collectors.toMap(Method::getReturnType, Function.identity()));
        final Map<Class<?>, Object> initializedBeans = new HashMap<>();

        for (Class<?> beanType : orderedDependencies) {
            final Method initializingMethod = beanToFactoryMethod.get(beanType);
            final int parameterCount = initializingMethod.getParameterCount();
            final Object[] args = new Object[parameterCount];
            for (int i = 0; i < parameterCount; i++) {
                args[i] = initializedBeans.get(initializingMethod.getParameterTypes()[i]);
            }
            try {
                final Object constructedBean = initializingMethod.invoke(object, args);
                initializedBeans.put(beanType, constructedBean);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new YadifException(e);
            }
        }

        return createContext(initializedBeans);
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

    private List<Method> getFactoryMethods(final Object object) {
        final var methods = object.getClass()
                .getMethods();
        return Arrays.stream(methods)
                .filter(method -> method.isAnnotationPresent(Instance.class))
                .toList();
    }

    private Context createContext(final Map<Class<?>, Object> initializedBeans) {
        final var context = new Context();
        initializedBeans.forEach((key, value) -> context.add(key.getTypeName(), value));
        return context;
    }
}
