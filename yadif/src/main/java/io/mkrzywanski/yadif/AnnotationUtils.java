package io.mkrzywanski.yadif;

import io.mkrzywanski.yadif.annotation.Qualifier;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Optional;

final class AnnotationUtils {

    private AnnotationUtils() {
    }

    static Optional<Qualifier> extractQualifierAnnotation(final Annotation[] annotations) {
        return Arrays.stream(annotations)
                .filter(annotation -> annotation.annotationType().equals(Qualifier.class))
                .map(Qualifier.class::cast)
                .findAny();
    }
}
