package com.gallery.service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * Difference class
 * Finds the difference between two sets
 * which represent current and previous states
 *
 * @author Dennis Obukhov
 * @date 2019-05-02 14:22 [Thursday]
 */
public class Difference<T> {
    private final Set<T> removed;
    private final Set<T> added;

    /**
     * @param current set of entities
     * @param previous set of entities
     */
    public Difference(Set<T> current, Set<T> previous) {
        // elements which were removed
        Set<T> removed = new TreeSet<>(previous);
        removed.removeAll(current);
        this.removed = removed;

        // root list of new files
        Set<T> added = new TreeSet<>(current);
        added.removeAll(previous);
        this.added = added;
    }

    public Set<T> getRemoved() {
        return removed;
    }

    public Set<T> getAdded() {
        return added;
    }

    @Override
    public String toString() {
        return String.format(
                "Diff [ -%s; +%s]", removed.size(), added.size());
    }

    public int addedCount() {
        return added.size();
    }

    public int removedCount() {
        return removed.size();
    }
}