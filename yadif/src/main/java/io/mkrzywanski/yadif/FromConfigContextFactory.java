package io.mkrzywanski.yadif;

import io.mkrzywanski.yadif.annotation.Instance;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

class FromConfigContextFactory {

    private final TopologicalSort<Class<?>> topologicalSort = new KhanTopologicalSort();

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

        final Map<Class<?>, List<Class<?>>> classToDependencies = factoryMethods.stream().collect(Collectors.toMap(Method::getReturnType, method -> Arrays.asList(method.getParameterTypes())));
        final var orderedDependencies = topologicalSort.sort(classToDependencies);

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
