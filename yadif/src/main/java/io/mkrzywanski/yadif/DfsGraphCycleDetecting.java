package io.mkrzywanski.yadif;

import java.util.HashSet;
import java.util.Set;

class DfsGraphCycleDetecting implements GraphCycleDetecting {

    @Override
    public Set<Path> detectCycles(final Graph graph) {
        final var rootCandidates = graph.getRootCandidates();
        final Set<Path> allCycles = new HashSet<>();
        for (Class<?> rootCandidate : rootCandidates) {
            final Set<Class<?>> alreadyVisitedNodes = new HashSet<>();
            final Path currentPath = Path.withInitial(rootCandidate);
            final Set<Path> cycles = detectCycleInternal(graph, rootCandidate, alreadyVisitedNodes, currentPath);
            allCycles.addAll(cycles);
        }
        return allCycles;
    }

    private Set<Path> detectCycleInternal(final Graph graph, final Class<?> currentNode, final Set<Class<?>> alreadyVisited, final Path currentPath) {
        final var adjacentNodes = graph.getAdjacentNodes(currentNode);
        alreadyVisited.add(currentNode);

        final Set<Path> result = new HashSet<>();
        for (final Class<?> adjacentNode : adjacentNodes) {
            final Path copiedPath = currentPath.copy();
            copiedPath.add(adjacentNode);
            if (!alreadyVisited.contains(adjacentNode)) {
                final var otherPaths = detectCycleInternal(graph, adjacentNode, alreadyVisited, copiedPath);
                otherPaths.stream().filter(Path::isCycle).forEach(result::add);
            } else {
                result.add(copiedPath);
            }
        }
        return result;
    }

}
