package io.mkrzywanski.yadif;

import io.mkrzywanski.yadif.api.YadifBeanInsantiationException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

class BeanMethodCreationStrategy implements BeanCreationStrategy {

    private final Method method;
    private final Object target;

    BeanMethodCreationStrategy(final Method method, final Object target) {
        this.method = method;
        this.target = target;
    }

    @Override
    public List<Bean> dependencies() {
        return DependencyListing.getDependenciesOf(method);
    }

    @Override
    public Object invoke(final Object[] args) {
        try {
            return method.invoke(target, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new YadifBeanInsantiationException(e);
        }
    }
}
