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

import it.unicam.cs.mpgc.jbudget126139.model.Recurrence;
import it.unicam.cs.mpgc.jbudget126139.model.RecurrenceFrequency;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repository interface for managing {@link Recurrence} entities.
 *
 * @param <T> the concrete type of {@link Recurrence} managed by this repository
 */
public interface RecurrenceRepository<T extends Recurrence> extends GenericRepository<T, UUID> {

    /**
     * Retrieves all recurrences for a given account, ordered by start date in descending order.
     *
     * @param accountId the account identifier; must not be {@code null}.
     * @return a list of recurrences sorted from newest to oldest; never {@code null}, may be empty.
     */
    List<T> findByAccountOrderByStartDateDesc(UUID accountId);

    /**
     * Retrieves all recurrences for a given account that match a specific frequency.
     *
     * @param accountId  the account identifier; must not be {@code null}.
     * @param frequency  the recurrence frequency; must not be {@code null}.
     * @return a list of matching recurrences; never {@code null}, may be empty.
     */
    List<T> findByAccountAndFrequency(UUID accountId, RecurrenceFrequency frequency);

    /**
     * Retrieves all active recurrences as of a specific date.
     * <p>
     * A recurrence is considered active if its start date is before or equal to {@code asOf}
     * and either it has no end date or its end date is after {@code asOf}.
     * </p>
     *
     * @param asOf the date-time used to determine active recurrences; must not be {@code null}.
     * @return a list of active recurrences; never {@code null}, may be empty.
     */
    List<T> findActiveRecurrences(OffsetDateTime asOf);
}