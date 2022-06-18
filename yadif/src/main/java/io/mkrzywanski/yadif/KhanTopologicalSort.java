package io.mkrzywanski.yadif;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class KhanTopologicalSort implements TopologicalSort<Class<?>> {
    @Override
    public List<Class<?>> sort(final Map<Class<?>, List<Class<?>>> nodeToEdge) {
        final List<Class<?>> result = new ArrayList<>();

        final Graph graph = new Graph(nodeToEdge);
        final var nodesWithoutDepndencies = graph.getLeafs();

        final var nodeQueue = new ArrayDeque<>(nodesWithoutDepndencies);
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
        return result;
    }
}
