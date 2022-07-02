package io.mkrzywanski.yadif;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

class DfsGraphCycleDetecting implements GraphCycleDetecting {

    @Override
    public Set<Path> detectCycles(final Graph graph) {
        final var rootCandidates = graph.getRootCandidates();
        final Set<Path> allCycles = new HashSet<>();
        for (Bean rootCandidate : rootCandidates) {
            final Set<Bean> alreadyVisitedNodes = new HashSet<>();
            final Path currentPath = Path.withInitial(rootCandidate);
            final Set<Path> cycles = detectCycleInternal(graph, rootCandidate, alreadyVisitedNodes, currentPath);
            allCycles.addAll(cycles);
        }
        return allCycles;
    }

    private Set<Path> detectCycleInternal(final Graph graph, final Bean currentNode, final Set<Bean> alreadyVisited, final Path currentPath) {
        final var adjacentNodes = graph.getAdjacentNodes(currentNode);
        alreadyVisited.add(currentNode);

        final Set<Path> result = new HashSet<>();
        for (final Bean adjacentNode : adjacentNodes) {
            final Path copiedPath = currentPath.copy();
            copiedPath.add(adjacentNode);
            if (!isVisited(alreadyVisited, adjacentNode)) {
                final var otherPaths = detectCycleInternal(graph, adjacentNode, alreadyVisited, copiedPath);
                otherPaths.stream().filter(Path::isCycle).forEach(result::add);
            } else {
                result.add(copiedPath);
            }
        }
        return result;
    }

    private boolean isVisited(final Set<Bean> alreadyVisited, final Bean adjacentNode) {
        final boolean contains = alreadyVisited.contains(adjacentNode);
        if (contains) {
            return true;
        } else {
            return alreadyVisited.stream().map(Bean::type).collect(Collectors.toSet()).contains(adjacentNode.type());
        }
    }

}
