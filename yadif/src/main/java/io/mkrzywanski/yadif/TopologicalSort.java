package io.mkrzywanski.yadif;

import java.util.List;
import java.util.Map;

interface TopologicalSort<T> {
    List<T> sort(Map<Class<?>, List<Class<?>>> a);
}
