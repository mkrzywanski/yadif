package io.mkrzywanski.yadif;

import io.mkrzywanski.yadif.annotation.Qualifier;
import io.mkrzywanski.yadif.api.YadifBeanInsantiationException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

class BeanMethodCreationStrategy implements BeanCreationStrategy {

    private final Method method;
    private final Object target;

    BeanMethodCreationStrategy(final Method method, final Object target) {
        this.method = method;
        this.target = target;
    }

//    @Override
//    public int getParameterCount() {
//        return method.getParameterCount();
//    }

    @Override
    public List<Class<?>> getParameterTypes() {
        return Arrays.stream(method.getParameterTypes()).toList();
    }

    @Override
    public List<Bean> dependencies() {
        return Arrays.stream(method.getAnnotatedParameterTypes()).map(annotatedType -> {
            final Qualifier annotation = annotatedType.getAnnotation(Qualifier.class);
            final BeanId beanId = Optional.ofNullable(annotation)
                    .map(Qualifier::value).map(BeanId::new)
                    .orElseGet(BeanId::empty);
            //
            return new Bean((Class<?>) annotatedType.getType(), beanId);
        }).toList();
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
