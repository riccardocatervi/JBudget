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

import it.unicam.cs.mpgc.jbudget126139.model.AbstractAccount;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for managing {@link AbstractAccount} entities.
 * <p>
 * Extends {@link GenericRepository} with methods specific to account retrieval,
 * including user-based filtering and alphabetical ordering.
 * </p>
 *
 * @param <T> the specific type of {@link AbstractAccount} managed by this repository
 */
public interface AccountRepository<T extends AbstractAccount> extends GenericRepository<T, UUID> {

    /**
     * Retrieves all accounts, ordered by their {@code name} in ascending order.
     *
     * @return a list of accounts sorted alphabetically by name; never {@code null}, may be empty.
     */
    List<T> findAllOrderByNameAsc();

}