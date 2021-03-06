package io.mkrzywanski.yadif;

import io.mkrzywanski.yadif.annotation.Instance;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

class IntrospectConfigClass implements BeanIntrospectionStrategy {

    @Override
    public BeanIntrospectionResult introspect(final Object object) {
        final var factoryMethods = getFactoryMethods(object);

        final var strategyMap = factoryMethods
                .stream()
                .collect(Collectors.<Method, Bean, BeanCreationStrategy>toMap(method -> new Bean(method.getReturnType(), new BeanId(method.getName())), method -> new BeanMethodCreationStrategy(method, object)));

        return new BeanIntrospectionResult(strategyMap);
    }

    private List<Method> getFactoryMethods(final Object object) {
        final var methods = object.getClass()
                .getMethods();
        return Arrays.stream(methods)
                .filter(method -> method.isAnnotationPresent(Instance.class))
                .toList();
    }
}
