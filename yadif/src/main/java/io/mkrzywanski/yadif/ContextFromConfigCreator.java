package io.mkrzywanski.yadif;

import io.mkrzywanski.yadif.annotation.Instance;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;

class ContextFromConfigCreator {

    Context fromConfig(final Class<?> clazz) {
        try {
            final Constructor<?> constructor = clazz.getConstructor();
            final Object o = constructor.newInstance();
            return fromConfig(o);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    Context fromConfig(final Object object) {
        Objects.requireNonNull(object);
        final var methods = object.getClass()
                .getMethods();
        final var context = new Context();

        Arrays.stream(methods)
                .filter(method -> method.isAnnotationPresent(Instance.class))
                .map(getBean(object))
                .forEach(instance -> context.add(instance.getClass().getTypeName(), instance));

        return context;
    }

    private Function<Method, Object> getBean(final Object object) {
        return method -> {
            try {
                return method.invoke(object);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new IllegalStateException(e);
            }
        };
    }
}
