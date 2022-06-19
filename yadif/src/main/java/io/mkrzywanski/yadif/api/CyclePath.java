package io.mkrzywanski.yadif.api;

import java.util.List;
import java.util.Objects;

public class CyclePath {

    private final List<Class<?>> cycle;

    public CyclePath(final List<Class<?>> cycle) {
        this.cycle = cycle;
    }

    List<Class<?>> getCycle() {
        return cycle;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final CyclePath cyclePath = (CyclePath) o;
        return getCycle().equals(cyclePath.getCycle());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCycle());
    }
}
