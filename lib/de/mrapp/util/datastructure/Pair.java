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

/**
 * A data structure, which eases to pass around a pair of two objects. This object provides a
 * sensible implementation of equals(), returning true if equals() is true on each of the contained
 * objects.
 *
 * @param <F> The type of the first object
 * @param <S> The type of the second object
 * @author Michael Rapp
 * @since 1.0.0
 */
public class Pair<F, S> {

    /**
     * The first object.
     */
    public final F first;

    /**
     * The second object.
     */
    public final S second;

    /**
     * Creates a new pair.
     *
     * @param first  The first object as an instance of the generic type F
     * @param second The second object as an instance of the generic type S
     */
    public Pair(final F first, final S second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Creates a new pair.
     *
     * @param <F>    The type of the first object
     * @param <S>    The type of the second object
     * @param first  The first object as an instance of the generic type F
     * @param second The second object as an instance of the generic type S
     * @return The pair, which has been created, as an instance of the class {@link Pair}. The pair
     * may not be null
     */
    public static <F, S> Pair<F, S> create(final F first, final S second) {
        return new Pair<>(first, second);
    }

    @Override
    public final int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((first == null) ? 0 : first.hashCode());
        result = prime * result + ((second == null) ? 0 : second.hashCode());
        return result;
    }

    @Override
    public final boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Pair<?, ?> other = (Pair<?, ?>) obj;
        if (first == null) {
            if (other.first != null)
                return false;
        } else if (!first.equals(other.first))
            return false;
        if (second == null) {
            if (other.second != null)
                return false;
        } else if (!second.equals(other.second))
            return false;
        return true;
    }

}