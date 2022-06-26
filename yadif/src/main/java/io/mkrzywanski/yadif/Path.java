package io.mkrzywanski.yadif;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class Path {

    private List<Bean> path;

    Path() {
        this.path = new ArrayList<>();
    }

    Path copy() {
        final Path path = new Path();
        path.path = new ArrayList<>(this.path);
        return path;
    }

    static Path withInitial(final Bean clazz) {
        final Path path = new Path();
        path.add(clazz);
        return path;
    }

    void add(final Bean clz) {
        path.add(clz);
    }

    boolean isCycle() {
//        boolean b = path.stream()
//                .collect(Collectors.groupingBy(clazz -> clazz, Collectors.counting()))
//                .entrySet().stream()
//                .anyMatch(classLongEntry -> classLongEntry.getValue() > 1);
//        if (b) {
//            return b;
//        }
        final Bean first = path.get(0);
        final Bean last = path.get(path.size() - 1);

        if (first.hasSameTypeAs(last)) {
            return !first.hasNonEmptyId() || !last.hasNonEmptyId() || first.hasSameIdAs(last);
        } else {
            return false;
        }

    }

    List<Bean> getPath() {
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
