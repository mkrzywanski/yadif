package io.mkrzywanski.yadif;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

class ConstructorCreationStrategy implements BeanCreationStrategy {

    private final Constructor<?> constructor;

    ConstructorCreationStrategy(final Constructor<?> constructor) {
        this.constructor = constructor;
    }

    @Override
    public int getParameterCount() {
        return constructor.getParameterCount();
    }

    @Override
    public Class<?>[] getParameterTypes() {
        return constructor.getParameterTypes();
    }

    @Override
    public Object invoke(final Object[] args)  {
        try {
            constructor.setAccessible(true);
            return constructor.newInstance(args);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new YadifBeanInsantiationException(e);
        }
    }
}
