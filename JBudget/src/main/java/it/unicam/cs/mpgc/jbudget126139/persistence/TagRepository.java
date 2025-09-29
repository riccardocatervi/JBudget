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

import it.unicam.cs.mpgc.jbudget126139.model.Tag;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for managing {@link Tag} entities.
 *
 * @param <T> the concrete type of {@link Tag} managed by this repository
 */
public interface TagRepository<T extends Tag> extends GenericRepository<T, UUID> {

    /**
     * Retrieves all tags, ordered by their {@code name} in ascending order.
     *
     * @return a list of all tags sorted alphabetically by name;
     *         never {@code null}, may be empty.
     */
    List<T> findAllOrderByNameAsc();

    /**
     * Retrieves all tags that have the specified parent tag,
     * ordered by their {@code name} in ascending order.
     *
     * @param parentId the UUID of the parent tag; must not be {@code null}.
     * @return a list of child tags sorted alphabetically by name;
     *         never {@code null}, may be empty.
     */
    List<T> findByParentIdOrderByNameAsc(UUID parentId);

    /**
     * Retrieves all root tags (tags without a parent),
     * ordered by their {@code name} in ascending order.
     *
     * @return a list of root tags sorted alphabetically by name;
     *         never {@code null}, may be empty.
     */
    List<T> findRootTagsOrderByNameAsc();
}