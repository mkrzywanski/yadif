package io.mkrzywanski.yadif;

import java.util.List;

interface BeanCreationStrategy {

    List<Bean> dependencies();

    Object invoke(Object[] args);
}
