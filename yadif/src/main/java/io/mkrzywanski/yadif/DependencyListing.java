package io.mkrzywanski.yadif;

import io.mkrzywanski.yadif.annotation.Qualifier;

import java.lang.reflect.Executable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

final class DependencyListing {

    private DependencyListing() {
    }

    static List<Bean> getDependenciesOf(final Executable executable) {
        final var parameterAnnotations = executable.getParameterAnnotations();
        final var parameterTypes = executable.getParameterTypes();

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
}
