package io.mkrzywanski.yadif;

import java.util.List;

interface BeanCreationStrategy {

    List<Class<?>> getParameterTypes();

    List<Bean> dependencies();

    Object invoke(Object[] args);
}
