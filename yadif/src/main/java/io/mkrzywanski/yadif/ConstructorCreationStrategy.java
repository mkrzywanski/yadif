package io.mkrzywanski.yadif;

import io.mkrzywanski.yadif.annotation.Qualifier;
import io.mkrzywanski.yadif.api.YadifBeanInsantiationException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

class ConstructorCreationStrategy implements BeanCreationStrategy {

    private final Constructor<?> constructor;

    ConstructorCreationStrategy(final Constructor<?> constructor) {
        this.constructor = constructor;
        this.constructor.setAccessible(true);
    }

    @Override
    public List<Bean> dependencies() {
        return Arrays.stream(constructor.getAnnotatedParameterTypes()).map(annotatedType -> {
            final Qualifier annotation = annotatedType.getAnnotation(Qualifier.class);
            final BeanId beanId = Optional.ofNullable(annotation).map(Qualifier::value)
                    .map(BeanId::new)
                    .orElseGet(BeanId::empty);
            return new Bean((Class<?>) annotatedType.getType(), beanId);
        }).toList();
    }

    @Override
    public Object invoke(final Object[] args)  {
        try {
            return constructor.newInstance(args);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new YadifBeanInsantiationException(e);
        }
    }
}
