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

package it.unicam.cs.mpgc.jbudget126139.service.impl;

import it.unicam.cs.mpgc.jbudget126139.model.RecurrenceFrequency;
import it.unicam.cs.mpgc.jbudget126139.model.TransactionDirection;
import it.unicam.cs.mpgc.jbudget126139.service.RecurrenceService;
import it.unicam.cs.mpgc.jbudget126139.service.RecurrenceTransactionService;
import it.unicam.cs.mpgc.jbudget126139.service.TransactionService;
import it.unicam.cs.mpgc.jbudget126139.service.dto.RecurrenceDTO;
import it.unicam.cs.mpgc.jbudget126139.service.dto.TransactionDTO;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.*;

/**
 * Default implementation of {@link RecurrenceTransactionService}.
 * <p>
 * Coordinates the creation of recurring transactions based on recurrence settings
 * and delegates persistence operations to {@link TransactionService} and
 * {@link RecurrenceService}.
 * </p>
 */
public class RecurrenceTransactionServiceImpl implements RecurrenceTransactionService {

    private final TransactionService transactionService;
    private final RecurrenceService recurrenceService;

    /**
     * Creates a new {@code RecurrenceTransactionServiceImpl} instance.
     *
     * @param transactionService the {@link TransactionService} to use; must not be {@code null}
     * @param recurrenceService  the {@link RecurrenceService} to use; must not be {@code null}
     */
    public RecurrenceTransactionServiceImpl(TransactionService transactionService,
                                            RecurrenceService recurrenceService) {
        this.transactionService = Objects.requireNonNull(transactionService);
        this.recurrenceService = Objects.requireNonNull(recurrenceService);
    }

    /** {@inheritDoc} */
    @Override
    public List<TransactionDTO> createRecurringTransactions(
            UUID accountId, OffsetDateTime startDate, OffsetDateTime endDate,
            RecurrenceFrequency frequency, BigDecimal amount,
            TransactionDirection direction, String description, Set<UUID> tagIds) {

        RecurrenceDTO recurrence = recurrenceService.createRecurrence(
                accountId, startDate, endDate, frequency);
        return generateTransactions(recurrence, accountId, amount, direction, description, tagIds);
    }

    /** {@inheritDoc} */
    @Override
    public List<TransactionDTO> listTransactionsByRecurrence(UUID recurrenceId) {
        return transactionService.listByRecurrence(recurrenceId);
    }

    /** {@inheritDoc} */
    @Override
    public void deleteRecurrence(UUID recurrenceId) {
        recurrenceService.deleteRecurrence(recurrenceId);
    }

    // Private helper methods

    private List<TransactionDTO> generateTransactions(
            RecurrenceDTO recurrence, UUID accountId, BigDecimal amount,
            TransactionDirection direction, String description, Set<UUID> tagIds) {

        List<TransactionDTO> transactions = new ArrayList<>();
        OffsetDateTime currentDate = recurrence.startDate();
        OffsetDateTime now = OffsetDateTime.now();

        OffsetDateTime effectiveEndDate = (recurrence.endDate() != null && recurrence.endDate().isBefore(now))
                ? recurrence.endDate() : now;

        while (!currentDate.isAfter(effectiveEndDate) && transactions.size() < 500) {
            TransactionDTO transaction = transactionService.createTransaction(
                    accountId, currentDate, amount, direction, description, tagIds, recurrence.id());
            transactions.add(transaction);
            currentDate = calculateNextDate(currentDate, recurrence.frequency());
        }

        return transactions;
    }

    private OffsetDateTime calculateNextDate(OffsetDateTime date, RecurrenceFrequency frequency) {
        return switch (frequency) {
            case DAILY -> date.plusDays(1);
            case WEEKLY -> date.plusWeeks(1);
            case MONTHLY -> date.plusMonths(1);
            case YEARLY -> date.plusYears(1);
        };
    }
}