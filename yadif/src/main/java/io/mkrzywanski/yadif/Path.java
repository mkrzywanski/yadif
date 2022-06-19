package io.mkrzywanski.yadif;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

class Path {

    private List<Class<?>> path;

    Path() {
        this.path = new ArrayList<>();
    }

    Path copy() {
        final Path path = new Path();
        path.path = new ArrayList<>(this.path);
        return path;
    }

    static Path withInitial(final Class<?> clazz) {
        final Path path = new Path();
        path.add(clazz);
        return path;
    }

    void add(final Class<?> clz) {
        path.add(clz);
    }

    boolean isCycle() {
        return path.stream()
                .collect(Collectors.groupingBy(clazz -> clazz, Collectors.counting()))
                .entrySet().stream()
                .anyMatch(classLongEntry -> classLongEntry.getValue() > 1);
    }

    List<Class<?>> getPath() {
        return new ArrayList<>(path);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Path path1 = (Path) o;
        return path.equals(path1.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }
}
