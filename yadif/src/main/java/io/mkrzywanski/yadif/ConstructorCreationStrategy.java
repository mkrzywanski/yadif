package io.mkrzywanski.yadif;

import io.mkrzywanski.yadif.annotation.Qualifier;
import io.mkrzywanski.yadif.api.YadifBeanInsantiationException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
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
        final Annotation[][] parameterAnnotations = constructor.getParameterAnnotations();
        final Class<?>[] parameterTypes = constructor.getParameterTypes();

        final List<Bean> result = new ArrayList<>(parameterTypes.length);
        for (int i = 0; i < parameterTypes.length; i++) {
            final Class<?> parameterType = parameterTypes[i];
            final Annotation[] annotations = parameterAnnotations[i];
            final Optional<Qualifier> extract = AnnotationUtils.extractQualifierAnnotation(annotations);

            final BeanId beanId = extract.map(Qualifier::value)
                    .map(BeanId::new)
                    .orElseGet(BeanId::empty);
            result.add(new Bean(parameterType, beanId));

        }
        return result;
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
