package io.mkrzywanski.yadif;

import java.util.Set;

public class DependencyCycleDetectedException extends RuntimeException {
    private final Set<Path> cycles;

    DependencyCycleDetectedException(final Set<Path> cycles) {
        this.cycles = cycles;
    }

    public Set<Path> getCycles() {
        return cycles;
    }
}
