package io.mkrzywanski.yadif;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

class Graph {

    private final Map<Class<?>, List<Class<?>>> graph;

    Graph(final Map<Class<?>, List<Class<?>>> graph) {
        this.graph = graph;
    }

    Set<Class<?>> getLeafs() {
        return graph
                .entrySet()
                .stream()
                .filter(listEntry -> listEntry.getValue().isEmpty()).map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    Set<Class<?>> getNodeDependents(final Class<?> node) {
        return graph.entrySet()
                .stream()
                .filter(classListEntry -> classListEntry.getValue().contains(node))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    void removeEdge(final Class<?> from, final Class<?> toRemove) {
        graph.computeIfPresent(from, (clazz, classes) -> {
            final var copy = new ArrayList<>(classes);
            copy.remove(toRemove);
            return copy;
        });
    }

    boolean hasDependencies(final Class<?> node) {
        return !graph.get(node).isEmpty();
    }
}
