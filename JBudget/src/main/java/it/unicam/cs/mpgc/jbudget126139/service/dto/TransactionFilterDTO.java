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

package it.unicam.cs.mpgc.jbudget126139.service.dto;

import it.unicam.cs.mpgc.jbudget126139.model.TransactionDirection;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) representing filter criteria for searching transactions.
 *
 * @param direction   the transaction direction to filter by; may be {@code null} for any
 * @param tagIds      the set of tag identifiers to filter by; may be {@code null} or empty for any
 * @param fromDate    the minimum value date (inclusive); may be {@code null} for no lower bound
 * @param toDate      the maximum value date (inclusive); may be {@code null} for no upper bound
 * @param description a substring to search for in transaction descriptions; may be {@code null} or empty
 */
public record TransactionFilterDTO(
        TransactionDirection direction,
        Set<UUID> tagIds,
        OffsetDateTime fromDate,
        OffsetDateTime toDate,
        String description
) {

    /**
     * Creates an empty {@link TransactionFilterDTO} with all fields set to {@code null}.
     *
     * @return an empty filter instance
     */
    public static TransactionFilterDTO empty() {
        return new TransactionFilterDTO(null, null, null, null, null);
    }

    /**
     * Checks whether the filter contains no criteria.
     *
     * @return {@code true} if all fields are {@code null} or empty; {@code false} otherwise
     */
    public boolean isEmpty() {
        return direction == null &&
                (tagIds == null || tagIds.isEmpty()) &&
                fromDate == null &&
                toDate == null &&
                (description == null || description.trim().isEmpty());
    }
}