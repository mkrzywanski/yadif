package io.mkrzywanski.yadif;

import io.mkrzywanski.yadif.api.YadifBeanInsantiationException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

class ConstructorCreationStrategy implements BeanCreationStrategy {

    private final Constructor<?> constructor;

    ConstructorCreationStrategy(final Constructor<?> constructor) {
        this.constructor = constructor;
        this.constructor.setAccessible(true);
    }

    @Override
    public List<Bean> dependencies() {
        return DependencyListing.getDependenciesOf(constructor);
    }

    @Override
    public Object invoke(final Object[] args) {
        try {
            return constructor.newInstance(args);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new YadifBeanInsantiationException(e);
        }
    }
}
