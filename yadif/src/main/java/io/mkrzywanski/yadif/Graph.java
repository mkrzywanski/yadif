package io.mkrzywanski.yadif;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

class Graph {

    private final Map<Bean, List<Bean>> adjacencyMap;

    Graph(final Map<Bean, List<Bean>> adjacencyMap) {
        this.adjacencyMap = adjacencyMap;
    }

    Graph copy() {
        final Map<Bean, List<Bean>> adjacencyMatrixCopy = adjacencyMap.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, classListEntry -> new ArrayList<>(classListEntry.getValue())));
        return new Graph(adjacencyMatrixCopy);
    }

    Set<Bean> getLeafs() {
        return adjacencyMap
                .entrySet()
                .stream()
                .filter(listEntry -> listEntry.getValue().isEmpty())
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    Set<Bean> getRootCandidates() {
        return adjacencyMap.entrySet().stream()
                .filter(classListEntry -> !classListEntry.getValue().isEmpty())
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    Set<Bean> getNodeDependents(final Bean node) {


        return adjacencyMap.entrySet()
                .stream()
                .filter(classListEntry -> {
                    final boolean exactMatch = classListEntry.getValue().contains(node);
                    if (exactMatch) {
                        return true;
                    } else {
                        return classListEntry.getValue().stream().map(Bean::type).collect(Collectors.toSet()).contains(node.type());
                    }
                })
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    Set<Bean> nodes() {
        return new HashSet<>(adjacencyMap.keySet());
    }

    void removeEdge(final Bean from, final Bean toRemove) {
        adjacencyMap.computeIfPresent(from, (clazz, beans) -> {
            final var copy = new ArrayList<>(beans);
            if (copy.contains(toRemove)) {
                copy.remove(toRemove);
            } else {
                copy.removeIf(bean -> bean.type() == toRemove.type());
            }

            return copy;
        });
    }

    boolean hasDependencies(final Bean node) {
        return !adjacencyMap.get(node).isEmpty();
    }

    List<Bean> getAdjacentNodes(final Bean vertex) {
        final List<Bean> beans = adjacencyMap.get(vertex);
        if (beans != null) {
            return beans;
        }
        return adjacencyMap.entrySet().stream()
                .filter(e -> e.getKey().type() == vertex.type())
                .map(Map.Entry::getValue)
                .findAny()
                .orElseThrow(RuntimeException::new);
    }
}
