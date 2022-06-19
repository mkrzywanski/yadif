package io.mkrzywanski.yadif;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

class KahnTopologicalSort implements TopologicalSort<Class<?>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(KahnTopologicalSort.class);

    @Override
    public List<Class<?>> sort(final Graph graph) {
        return sortInternal(graph.copy());
    }

    public List<Class<?>> sortInternal(final Graph graph) {
        final List<Class<?>> result = new ArrayList<>();

        final var nodesWithoutDependencies = graph.getLeafs();

        final var nodeQueue = new ArrayDeque<>(nodesWithoutDependencies);
        while (!nodeQueue.isEmpty()) {
            final var currentNode = nodeQueue.poll();
            result.add(currentNode);

            final var edges = graph.getNodeDependents(currentNode);

            for (Class<?> m : edges) {
                graph.removeEdge(m, currentNode);
                final boolean hasOtherDependencies = graph.hasDependencies(m);
                if (!hasOtherDependencies) {
                    result.add(m);
                }
            }
        }

        final Set<Class<?>> rootCandidates = graph.getRootCandidates();
        if (!rootCandidates.isEmpty()) {
            LOGGER.warn("Cycle detected");
        }
        return result;

    }
}
