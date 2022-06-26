package io.mkrzywanski.yadif;

import io.mkrzywanski.yadif.annotation.Qualifier;
import io.mkrzywanski.yadif.api.YadifBeanInsantiationException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class BeanMethodCreationStrategy implements BeanCreationStrategy {

    private final Method method;
    private final Object target;

    BeanMethodCreationStrategy(final Method method, final Object target) {
        this.method = method;
        this.target = target;
    }

    @Override
    public List<Bean> dependencies() {
        final var parameterAnnotations = method.getParameterAnnotations();
        final var parameterTypes = method.getParameterTypes();

        final List<Bean> result = new ArrayList<>(parameterTypes.length);
        for (int i = 0; i < parameterTypes.length; i++) {
            final var parameterType = parameterTypes[i];
            final var annotations = parameterAnnotations[i];
            final Optional<Qualifier> extract = AnnotationUtils.extractQualifierAnnotation(annotations);
            final BeanId s = extract.map(Qualifier::value).map(BeanId::new).orElseGet(BeanId::empty);
            result.add(new Bean(parameterType, s));

        }
        return result;
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
