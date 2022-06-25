package io.mkrzywanski.yadif;

import io.mkrzywanski.yadif.annotation.Instance;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class IntrospectConfigClass implements BeanIntrospectionStrategy {

    @Override
    public BeanIntrospectionStrategies introspect(final Object object) {
        final var factoryMethods = getFactoryMethods(object);

        final Map<Class<?>, List<Class<?>>> adjacencyMatrix = factoryMethods.stream()
                .collect(Collectors.toMap(Method::getReturnType, method -> Arrays.asList(method.getParameterTypes())));

        final var strategyMap = factoryMethods
                .stream()
                .collect(Collectors.<Method, Class<?>, BeanCreationStrategy>toMap(Method::getReturnType, method -> new BeanMethodCreationStrategy(method, object)));

        return new BeanIntrospectionStrategies(strategyMap);
    }

    private List<Method> getFactoryMethods(final Object object) {
        final var methods = object.getClass()
                .getMethods();
        return Arrays.stream(methods)
                .filter(method -> method.isAnnotationPresent(Instance.class))
                .toList();
    }
}
