package io.mkrzywanski.yadif;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

class KahnTopologicalSort implements TopologicalSort<Bean> {

    private static final Logger LOGGER = LoggerFactory.getLogger(KahnTopologicalSort.class);

    @Override
    public List<Bean> sort(final Graph graph) {
        return sortInternal(graph.copy());
    }

    public List<Bean> sortInternal(final Graph graph) {
        final List<Bean> result = new ArrayList<>();

        final var nodesWithoutDependencies = graph.getLeafs();

        final var nodeQueue = new ArrayDeque<>(nodesWithoutDependencies);
        while (!nodeQueue.isEmpty()) {
            final var currentNode = nodeQueue.poll();
            result.add(currentNode);

            Set<Bean> edges = graph.getNodeDependentsExact(currentNode);
            if (edges.isEmpty() && !graph.containsBeanOfSameTypeWhichIsExactDependencyOfOtherNode(currentNode)) {
                edges = graph.getNodeDependentsByType(currentNode);

            }

            for (Bean m : edges) {
                graph.removeEdge(m, currentNode);
                final boolean hasOtherDependencies = graph.hasDependencies(m);
                if (!hasOtherDependencies) {
                    result.add(m);
                }
            }
        }

        final Set<Bean> rootCandidates = graph.getRootCandidates();
        if (!rootCandidates.isEmpty()) {
            LOGGER.warn("Cycle detected");
        }
        return result;

    }
}
