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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import static de.mrapp.util.Condition.ensureNotNull;

/**
 * An extension of {@link ArrayList}, which automatically keeps its items in a sorted order,
 * whenever new items are added. Such a list does not allow to add items at specific indices.
 * Therefore an {@link UnsupportedOperationException} is thrown by the {@link
 * SortedArrayList#add(int, Object)} and {@link SortedArrayList#addAll(int, Collection)} methods.
 *
 * @param <T> The type of the items, which are contained by the list
 * @author Michael Rapp
 * @since 1.1.0
 */
public class SortedArrayList<T> extends ArrayList<T> {

    /**
     * The comparator, which is used to sort the list's items, or null, if the items' implementation
     * of the interface {@link Comparable} is used instead.
     */
    private final Comparator<? super T> comparator;

    /**
     * Creates a new list, which automatically keeps its items in a sorted order, whenever new items
     * are added. By default, the items' implementation of the interface {@link Comparable} is used
     * to sort the list's items.
     */
    public SortedArrayList() {
        this((Comparator<T>) null);
    }

    /**
     * Creates a new list, which automatically keeps its items in a sorted order, whenever new items
     * are added. By default, the items' implementation of the interface {@link Comparable} is used
     * to sort the list's items.
     *
     * @param items A collection, which contains the items, which should be added to the list, as an
     *              instance of the type {@link Collection} or an empty list, if no items should be
     *              added
     */
    public SortedArrayList(@NotNull final Collection<? extends T> items) {
        this((Comparator<T>) null);
        addAll(items);
    }

    /**
     * Creates a new list, which automatically keeps its items in a sorted order, whenever new items
     * are added. By default, the items' implementation of the interface {@link Comparable} is used
     * to sort the list's items.
     *
     * @param initialCapacity The initial capacity of the list as an {@link Integer} value
     */
    public SortedArrayList(final int initialCapacity) {
        this(initialCapacity, null);
    }

    /**
     * Creates a new list, which automatically keeps its items in a sorted order, whenever new items
     * are added.
     *
     * @param comparator The comparator, which should be used to sort the list's items, or null, if
     *                   the items' implementation of the interface {@link Comparable} should be
     *                   used instead
     */
    public SortedArrayList(@Nullable final Comparator<? super T> comparator) {
        this.comparator = comparator;
    }

    /**
     * Creates a new list, which automatically keeps its items in a sorted order, whenever new items
     * are added.
     *
     * @param items      A collection, which contains the items, which should be added to the list,
     *                   as an instance of the type {@link Collection} or an empty list, if no items
     *                   should be added
     * @param comparator The comparator, which should be used to sort the list's items, or null, if
     *                   the items' implementation of the interface {@link Comparable} should be
     *                   used instead
     */
    public SortedArrayList(@NotNull final Collection<? extends T> items,
                           @Nullable final Comparator<? super T> comparator) {
        this(comparator);
        addAll(items);
    }

    /**
     * Creates a new list, which automatically keeps its items in a sorted order, whenever new items
     * are added.
     *
     * @param initialCapacity The initial capacity of the list as an {@link Integer} value
     * @param comparator      The comparator, which should be used to sort the list's items, or
     *                        null, if the items' implementation of the interface {@link Comparable}
     *                        should be used instead
     */
    public SortedArrayList(final int initialCapacity,
                           @Nullable final Comparator<? super T> comparator) {
        super(initialCapacity);
        this.comparator = comparator;
    }

    /**
     * Returns the comparator, which is used to sort the list's items.
     *
     * @return The comparator, which is used to sort the list's items, as an instance of the type
     * {@link Comparator} or null, if the item's implementation of the interface {@link Comparable}
     * is used
     */
    @Nullable
    public Comparator<? super T> comparator() {
        return comparator;
    }

    @Override
    public boolean add(@NotNull final T item) {
        ensureNotNull(item, "The item may not be null");
        int index = Collections.binarySearch(this, item, comparator);

        if (index < 0) {
            index = ~index;
        }

        super.add(index, item);
        return true;
    }

    @Override
    public void add(final int index, @NotNull final T item) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(@NotNull final Collection<? extends T> items) {
        ensureNotNull(items, "The collection may not be null");

        if (items.size() > 0) {
            items.forEach(this::add);
            return true;
        }

        return false;
    }

    @Override
    public boolean addAll(final int index, @NotNull final Collection<? extends T> items) {
        throw new UnsupportedOperationException();
    }

}