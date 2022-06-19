package io.mkrzywanski.yadif;

import java.util.List;

interface TopologicalSort<T> {
    List<T> sort(Graph graph);
}
