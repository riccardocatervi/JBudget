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

package it.unicam.cs.mpgc.jbudget126139.model;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Represents a recurring transaction pattern associated with an {@link AbstractAccount}.
 * <p>
 * A recurrence defines a start date, an optional end date, and a frequency
 * that determines how often the transaction should repeat.
 * </p>
 */
public interface Recurrence extends Identified<UUID> {

    /**
     * Returns the timestamp indicating when this recurrence entity was created.
     *
     * @return the creation date and time of the recurrence.
     */
    OffsetDateTime getCreatedAt();

    /**
     * Returns the date when the recurrence starts.
     *
     * @return the start date of the recurrence; never {@code null}.
     */
    OffsetDateTime getStartDate();

    /**
     * Returns the date when the recurrence ends, if any.
     *
     * @return the end date of the recurrence, or {@code null} if it has no end date.
     */
    OffsetDateTime getEndDate();

    /**
     * Returns the frequency at which the recurrence repeats.
     *
     * @return the recurrence frequency; never {@code null}.
     */
    RecurrenceFrequency getFrequency();

    /**
     * Returns the account associated with this recurrence.
     *
     * @return the associated {@link AbstractAccount}; never {@code null}.
     */
    AbstractAccount getAccount();
}