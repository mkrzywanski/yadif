package io.mkrzywanski.yadif;

record Bean(Class<?> type, BeanId id) {
    boolean hasSameTypeAs(final Bean other) {
        return this.type == other.type;
    }

    boolean hasSameIdAs(final Bean other) {
        return this.id.equals(other.id);
    }

    boolean hasEmptyId() {
        return this.id.isEmpty();
    }

    boolean hasNonEmptyId() {
        return !this.id.isEmpty();
    }
}
