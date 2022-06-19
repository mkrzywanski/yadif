package io.mkrzywanski.yadif;

import java.util.Set;

interface GraphCycleDetecting {
    Set<Path> detectCycles(Graph graph);
}
