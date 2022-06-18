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

    List<? extends Class<?>> getNodeDependents(final Class<?> node) {
        return graph.entrySet()
                .stream()
                .filter(classListEntry -> classListEntry.getValue().contains(node))
                .map(classListEntry -> (Class<?>) classListEntry.getKey())
                .toList();
    }

    void removeEdge(final Class<?> from, final Class<?> toRemove) {
        graph.computeIfPresent(from, (clazz, classes) -> {
            final var classes1 = new ArrayList<>(classes);
            classes1.remove(toRemove);
            return classes1;
        });
    }

    boolean hasDependencies(final Class<?> node) {
        return !graph.get(node).isEmpty();
    }
}
