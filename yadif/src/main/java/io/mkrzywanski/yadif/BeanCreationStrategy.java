package io.mkrzywanski.yadif;

interface BeanCreationStrategy {

    int getParameterCount();

    Class<?>[] getParameterTypes();

    Object invoke(Object[] args);
}
