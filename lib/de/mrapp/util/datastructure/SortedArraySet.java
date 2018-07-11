/*
 * Copyright 2017 Michael Rapp
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package de.mrapp.util.datastructure;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static de.mrapp.util.Condition.*;

/**
 * A set, whose items are sorted based on a specific {@link Comparator} or according to their
 * implementation of the interface {@link Comparable}. In contrast to a {@link TreeSet}, two items
 * are considered as duplicates, if their hash codes are equal, instead of evaluating the result of
 * the {@link Comparator} or {@link Comparable#compareTo(Object)} method.
 *
 * The set internally uses a {@link SortedArrayList} to keep the items in a sorted order whenever
 * updated. In addition the hash codes of all items, which are contained by the set, are maintained
 * in a {@link HashSet} to prevent duplicates from being added to the set.
 *
 * @author Michael Rapp
 * @since 1.1.0
 */
public class SortedArraySet<T> implements SortedSet<T> {

    /**
     * A sorted array list, which contains the items, which are contained by the set.
     */
    private final SortedArrayList<T> sortedArrayList;

    /**
     * A set, which contains the hash codes of the items, which are contained by the set.
     */
    private final Set<Integer> hashCodes;

    /**
     * Creates a new set, whose items are sorted. By default, the items are sorted according to
     * their implementation of the interface {@link Comparable}.
     */
    public SortedArraySet() {
        this((Comparator<T>) null);
    }

    /**
     * Creates a new set, whose items are sorted. By default, the items are sorted according to
     * their implementation of the interface {@link Comparable}.
     *
     * @param items A collection, which contains the items, which should be added to the set, as an
     *              instance of the type {@link Collection} or an empty collection, if no items
     *              should be added
     */
    public SortedArraySet(final Collection<T> items) {
        this((Comparator<T>) null);
        addAll(items);
    }

    /**
     * Creates a new set, whose items are sorted. By default, the items are sorted according to
     * their implementation of the interface {@link Comparable}.
     *
     * @param initialCapacity The initial capacity of the set as an {@link Integer} value
     */
    public SortedArraySet(final int initialCapacity) {
        this(initialCapacity, null);
    }

    /**
     * Creates a new set, whose items are sorted.
     *
     * @param comparator The comparator, which should be used to sort the set's items, or null, if
     *                   the items' implementation of the interface {@link Comparable} should be
     *                   used instead
     */
    public SortedArraySet(@Nullable final Comparator<? super T> comparator) {
        this.sortedArrayList = new SortedArrayList<>(comparator);
        this.hashCodes = new HashSet<>();
    }

    /**
     * Creates a new set, whose items are sorted. By default, the items are sorted according to
     * their implementation of the interface {@link Comparable}.
     *
     * @param items      A collection, which contains the items, which should be added to the set,
     *                   as an instance of the type {@link Collection} or an empty collection, if no
     *                   items should be added
     * @param comparator The comparator, which should be used to sort the set's items, or null, if
     *                   the items' implementation of the interface {@link Comparable} should be
     *                   used instead
     */
    public SortedArraySet(@NotNull final Collection<T> items,
                          @Nullable final Comparator<? super T> comparator) {
        this(comparator);
        addAll(items);
    }

    /**
     * Creates a new set, whose items are sorted.
     *
     * @param initialCapacity The initial capacity of the set as an {@link Integer} value
     * @param comparator      The comparator, which should be used to sort the set's items, or null,
     *                        if the items' implementation of the interface {@link Comparable}
     *                        should be used instead
     */
    public SortedArraySet(final int initialCapacity,
                          @Nullable final Comparator<? super T> comparator) {
        this.sortedArrayList = new SortedArrayList<>(initialCapacity, comparator);
        this.hashCodes = new HashSet<>(initialCapacity);
    }

    @Nullable
    @Override
    public Comparator<? super T> comparator() {
        return sortedArrayList.comparator();
    }

    @NotNull
    @Override
    public SortedSet<T> subSet(@NotNull final T fromElement, @NotNull final T toElement) {
        ensureNotNull(fromElement, "The fromElement may not be null");
        ensureNotNull(toElement, "The toElement may not be null");
        int start = sortedArrayList.indexOf(fromElement);
        ensureNotEqual(start, -1, "fromElement not contained by set", NoSuchElementException.class);
        int end = sortedArrayList.indexOf(toElement);
        ensureNotEqual(end, -1, "toElement not contained by set", NoSuchElementException.class);
        ensureFalse(start > end, "fromElement greater than toElement");
        SortedSet<T> subSet = new SortedArraySet<>((end - start) + 1);

        for (int i = start; i <= end; i++) {
            subSet.add(sortedArrayList.get(i));
        }

        return subSet;
    }

    @NotNull
    @Override
    public SortedSet<T> headSet(@NotNull final T toElement) {
        ensureNotNull(toElement, "The toElement may not be null");
        int end = sortedArrayList.indexOf(toElement);
        ensureNotEqual(end, -1, "toElement not contained by set", NoSuchElementException.class);
        SortedSet<T> headSet = new SortedArraySet<>(end + 1);

        for (int i = 0; i <= end; i++) {
            headSet.add(sortedArrayList.get(i));
        }

        return headSet;
    }

    @NotNull
    @Override
    public SortedSet<T> tailSet(@NotNull final T fromElement) {
        ensureNotNull(fromElement, "The fromElement may not be null");
        int start = sortedArrayList.indexOf(fromElement);
        ensureNotEqual(start, -1, "fromElement not contained by set", NoSuchElementException.class);
        SortedSet<T> tailSet = new SortedArraySet<>(size() - start);

        for (int i = start; i < size(); i++) {
            tailSet.add(sortedArrayList.get(i));
        }

        return tailSet;
    }

    @NotNull
    @Override
    public T first() {
        ensureFalse(isEmpty(), "Set is empty", NoSuchElementException.class);
        return sortedArrayList.get(0);
    }

    @NotNull
    @Override
    public T last() {
        ensureFalse(isEmpty(), "Set is empty", NoSuchElementException.class);
        return sortedArrayList.get(sortedArrayList.size() - 1);
    }

    @Override
    public int size() {
        return sortedArrayList.size();
    }

    @Override
    public boolean isEmpty() {
        return sortedArrayList.isEmpty();
    }

    @Override
    public boolean contains(@NotNull final Object item) {
        ensureNotNull(item, "The item may not be null");
        return hashCodes.contains(item.hashCode());
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return sortedArrayList.iterator();
    }

    @NotNull
    @Override
    public Object[] toArray() {
        return sortedArrayList.toArray();
    }

    @SuppressWarnings("SuspiciousToArrayCall")
    @NotNull
    @Override
    public <T1> T1[] toArray(@NotNull final T1[] array) {
        ensureNotNull(array, "The array may not be null");
        return sortedArrayList.toArray(array);
    }

    @Override
    public boolean add(@NotNull final T item) {
        ensureNotNull(item, "The item may not be null");
        int hashCode = item.hashCode();

        if (!hashCodes.contains(hashCode)) {
            sortedArrayList.add(item);
            hashCodes.add(hashCode);
            return true;
        }

        return false;
    }

    @Override
    public boolean remove(@NotNull final Object item) {
        ensureNotNull(item, "The item may not be null");
        int hashCode = item.hashCode();

        if (hashCodes.remove(hashCode)) {
            sortedArrayList.remove(item);
            return true;
        }

        return false;
    }

    @Override
    public boolean containsAll(@NotNull final Collection<?> items) {
        ensureNotNull(items, "The collection may not be null");
        return items.stream().map(this::contains).reduce(true, (a, b) -> a && b);
    }

    @Override
    public boolean addAll(@NotNull final Collection<? extends T> items) {
        ensureNotNull(items, "The collection may not be null");

        if (!items.isEmpty()) {
            return items.stream().map(this::add).reduce(true, (a, b) -> a && b);
        }

        return false;
    }

    @Override
    public boolean retainAll(@NotNull final Collection<?> items) {
        ensureNotNull(items, "The collection may not be null");

        if (!items.isEmpty()) {
            boolean result = false;

            for (int i = size() - 1; i >= 0; i--) {
                T item = sortedArrayList.get(i);

                if (!items.contains(item)) {
                    int hashCode = item.hashCode();
                    sortedArrayList.remove(i);
                    hashCodes.remove(hashCode);
                    result = true;
                }
            }

            return result;
        }

        return false;
    }

    @Override
    public boolean removeAll(@NotNull final Collection<?> items) {
        ensureNotNull(items, "The collection may not be null");

        if (!items.isEmpty()) {
            return items.stream().map(this::remove).reduce(true, (a, b) -> a && b);
        }

        return false;
    }

    @Override
    public void clear() {
        hashCodes.clear();
        sortedArrayList.clear();
    }

    @Override
    public String toString() {
        return sortedArrayList.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + hashCodes.hashCode();
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SortedArraySet<?> other = (SortedArraySet<?>) obj;
        return hashCodes.equals(other.hashCodes);
    }

}