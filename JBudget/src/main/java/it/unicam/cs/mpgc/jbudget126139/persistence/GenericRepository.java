/*
 * MIT License
 *
 * Copyright (c) 2025 Riccardo Catervi
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * This software was designed and implemented as part of the academic
 * requirements of the "Programming Methodologies" course at
 * University of Camerino.
 */

package it.unicam.cs.mpgc.jbudget126139.persistence;

import java.util.Optional;

/**
 * Generic repository interface defining basic CRUD operations for entities.
 *
 * @param <E>  the entity type managed by the repository
 * @param <ID> the type of the entity identifier
 */
public interface GenericRepository<E, ID> {

    /**
     * Persists a new entity or updates an existing one.
     *
     * @param entity the entity to save; must not be {@code null}.
     */
    void save(E entity);

    /**
     * Retrieves an entity by its identifier.
     *
     * @param id the entity identifier; must not be {@code null}.
     * @return an {@link Optional} containing the found entity,
     *         or an empty Optional if no entity exists with the given ID.
     */
    Optional<E> findById(ID id);

    /**
     * Deletes the given entity instance.
     *
     * @param entity the entity to delete; must not be {@code null}.
     */
    void delete(E entity);

    /**
     * Deletes an entity by its identifier.
     *
     * @param id the entity identifier; must not be {@code null}.
     */
    void deleteById(ID id);
}