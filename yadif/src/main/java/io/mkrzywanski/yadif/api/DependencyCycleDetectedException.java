package io.mkrzywanski.yadif.api;

import java.util.Set;

public class DependencyCycleDetectedException extends RuntimeException {
    private final Set<CyclePath> cycles;

    public DependencyCycleDetectedException(final Set<CyclePath> cycles) {
        this.cycles = cycles;
    }

    public Set<CyclePath> getCycles() {
        return cycles;
    }
}
