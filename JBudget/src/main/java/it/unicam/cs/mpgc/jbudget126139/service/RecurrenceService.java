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

package it.unicam.cs.mpgc.jbudget126139.service;

import it.unicam.cs.mpgc.jbudget126139.model.RecurrenceFrequency;
import it.unicam.cs.mpgc.jbudget126139.service.dto.RecurrenceDTO;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service interface for managing recurring transactions.
 * <p>
 * Provides methods for creating, retrieving, deleting, and listing recurrences.
 * </p>
 */
public interface RecurrenceService {

    /**
     * Creates a new recurrence for the specified account.
     *
     * @param accountId  the identifier of the account; must not be {@code null}
     * @param startDate  the start date of the recurrence; must not be {@code null}
     * @param endDate    the optional end date of the recurrence; may be {@code null}
     * @param frequency  the recurrence frequency; must not be {@code null}
     * @return the created {@link RecurrenceDTO}
     */
    RecurrenceDTO createRecurrence(UUID accountId,
                                   OffsetDateTime startDate,
                                   OffsetDateTime endDate,
                                   RecurrenceFrequency frequency);

    /**
     * Retrieves a recurrence by its identifier.
     *
     * @param recurrenceId the recurrence identifier; must not be {@code null}
     * @return the matching {@link RecurrenceDTO}
     * @throws java.util.NoSuchElementException if no recurrence exists with the given ID
     */
    RecurrenceDTO getRecurrence(UUID recurrenceId);

    /**
     * Deletes a recurrence by its identifier.
     *
     * @param recurrenceId the recurrence identifier; must not be {@code null}
     */
    void deleteRecurrence(UUID recurrenceId);

    /**
     * Lists all recurrences for a given account.
     *
     * @param accountId the account identifier; must not be {@code null}
     * @return a list of {@link RecurrenceDTO} objects; never {@code null}, may be empty
     */
    List<RecurrenceDTO> listRecurrencesByAccount(UUID accountId);

    /**
     * Lists all active recurrences at a specific date and time.
     *
     * @param asOf the reference date and time; must not be {@code null}
     * @return a list of active {@link RecurrenceDTO} objects; never {@code null}, may be empty
     */
    List<RecurrenceDTO> listActiveRecurrences(OffsetDateTime asOf);
}