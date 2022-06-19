package io.mkrzywanski.yadif;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

class Graph {

    private final Map<Class<?>, List<Class<?>>> adjacencyMap;

    Graph(final Map<Class<?>, List<Class<?>>> adjacencyMap) {
        this.adjacencyMap = adjacencyMap;
    }

    Graph copy() {
        final Map<Class<?>, List<Class<?>>> adjacencyMatrixCopy = adjacencyMap.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, classListEntry -> new ArrayList<>(classListEntry.getValue())));
        return new Graph(adjacencyMatrixCopy);
    }

    Set<Class<?>> getLeafs() {
        return adjacencyMap
                .entrySet()
                .stream()
                .filter(listEntry -> listEntry.getValue().isEmpty()).map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    Set<Class<?>> getRootCandidates() {
        return adjacencyMap.entrySet().stream()
                .filter(classListEntry -> !classListEntry.getValue().isEmpty())
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    Set<Class<?>> getNodeDependents(final Class<?> node) {
        return adjacencyMap.entrySet()
                .stream()
                .filter(classListEntry -> classListEntry.getValue().contains(node))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    void removeEdge(final Class<?> from, final Class<?> toRemove) {
        adjacencyMap.computeIfPresent(from, (clazz, classes) -> {
            final var copy = new ArrayList<>(classes);
            copy.remove(toRemove);
            return copy;
        });
    }

    boolean hasDependencies(final Class<?> node) {
        return !adjacencyMap.get(node).isEmpty();
    }

    List<Class<?>> getAdjacentNodes(final Class<?> vertex) {
        return adjacencyMap.getOrDefault(vertex, List.of());
    }
}
