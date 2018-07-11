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
package de.mrapp.apriori;

import java.io.Serializable;

/**
 * Defines the interface, an item, which is part of a transaction, must implement. The Apriori
 * algorithm relies on items to correctly implement the {@link Object#hashCode()} and {@link
 * Object#equals(Object)} methods to be able to compare items to each other. Furthermore, the
 * interface {@link Comparable} must be implemented. This allows to sort items (e.g. by their names)
 * in order to generate candidates in an efficient way, when searching for frequent item sets.
 *
 * @author Michael Rapp
 * @since 1.0.0
 */
public interface Item extends Comparable<Item>, Serializable {

}