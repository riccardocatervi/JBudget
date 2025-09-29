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
import it.unicam.cs.mpgc.jbudget126139.model.TransactionDirection;
import it.unicam.cs.mpgc.jbudget126139.service.dto.TransactionDTO;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Service interface for managing transactions generated from recurrences.
 * <p>
 * Provides methods for creating recurring transactions, listing them by recurrence,
 * and deleting entire recurrences along with their transactions.
 * </p>
 */
public interface RecurrenceTransactionService {

    /**
     * Creates a set of transactions for a given account according to the specified recurrence parameters.
     *
     * @param accountId   the account identifier; must not be {@code null}
     * @param startDate   the start date of the recurrence; must not be {@code null}
     * @param endDate     the optional end date of the recurrence; may be {@code null}
     * @param frequency   the recurrence frequency; must not be {@code null}
     * @param amount      the transaction amount; must not be {@code null} and positive
     * @param direction   the transaction direction; must not be {@code null}
     * @param description an optional description for the transactions; may be {@code null}
     * @param tagIds      the set of tag identifiers associated with the transactions; may be empty
     * @return a list of created {@link TransactionDTO} objects; never {@code null}, may be empty
     */
    List<TransactionDTO> createRecurringTransactions(
            UUID accountId,
            OffsetDateTime startDate,
            OffsetDateTime endDate,
            RecurrenceFrequency frequency,
            BigDecimal amount,
            TransactionDirection direction,
            String description,
            Set<UUID> tagIds
    );

    /**
     * Retrieves all transactions linked to a specific recurrence.
     *
     * @param recurrenceId the recurrence identifier; must not be {@code null}
     * @return a list of {@link TransactionDTO} objects; never {@code null}, may be empty
     */
    List<TransactionDTO> listTransactionsByRecurrence(UUID recurrenceId);

    /**
     * Deletes a recurrence and all its associated transactions.
     *
     * @param recurrenceId the recurrence identifier; must not be {@code null}
     */
    void deleteRecurrence(UUID recurrenceId);
}