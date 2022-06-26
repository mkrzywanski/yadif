package io.mkrzywanski.yadif;

import io.mkrzywanski.yadif.annotation.Component;
import io.mkrzywanski.yadif.annotation.FactoryConstructor;
import io.mkrzywanski.yadif.annotation.PackageScan;
import io.mkrzywanski.yadif.api.YadifBeanInsantiationException;
import io.mkrzywanski.yadif.api.YadifException;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static org.reflections.scanners.Scanners.SubTypes;
import static org.reflections.scanners.Scanners.TypesAnnotated;

class IntrospectPackageScan implements BeanIntrospectionStrategy {

    @Override
    public BeanIntrospectionResult introspect(final Object object) {
        final PackageScan annotation = object.getClass().getAnnotation(PackageScan.class);

        if (annotation == null) {
            return BeanIntrospectionResult.empty();
        }

        final String packageName = annotation.value();
        if (packageName == null || packageName.isEmpty()) {
            throw new YadifException("Package scan value is null");
        }
        final Reflections reflections = new Reflections(packageName);

        final Set<Class<?>> potentialBeans =
                reflections.get(SubTypes.of(TypesAnnotated.with(Component.class)).asClass());

        return createBeanCreationStrategies(potentialBeans);
    }

    private BeanIntrospectionResult createBeanCreationStrategies(final Set<Class<?>> potentialBeans) {
        final HashMap<Class<?>, BeanCreationStrategy> result = new HashMap<>();
        for (Class<?> clazz : potentialBeans) {
            final Constructor<?>[] declaredConstructors = clazz.getDeclaredConstructors();
            final Constructor<?> constructor = isMoreThanOneConstructor(declaredConstructors) ?
                    getAnnotatedConstructor(declaredConstructors) :
                    declaredConstructors[0];

            result.put(clazz, new ConstructorCreationStrategy(constructor));
        }

        return new BeanIntrospectionResult(result);
    }

    private Constructor<?> getAnnotatedConstructor(final Constructor<?>[] declaredConstructors) {
        final List<Constructor<?>> annotatedConstructors = Arrays.stream(declaredConstructors)
                .filter(c -> c.getAnnotation(FactoryConstructor.class) != null)
                .toList();
        if (annotatedConstructors.size() > 1) {
            throw new YadifBeanInsantiationException("More than one factory constructor detected");
        }
        return annotatedConstructors.get(0);
    }

    private boolean isMoreThanOneConstructor(final Constructor<?>[] declaredConstructors) {
        return declaredConstructors.length > 1;
    }
}
