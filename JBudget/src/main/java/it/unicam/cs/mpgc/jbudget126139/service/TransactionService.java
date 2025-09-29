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

import it.unicam.cs.mpgc.jbudget126139.model.TransactionDirection;
import it.unicam.cs.mpgc.jbudget126139.service.dto.StatisticsDTO;
import it.unicam.cs.mpgc.jbudget126139.service.dto.TransactionDTO;
import it.unicam.cs.mpgc.jbudget126139.service.dto.TransactionFilterDTO;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Service interface for managing transactions.
 * <p>
 * Provides methods for creating, retrieving, updating, deleting,
 * and querying transactions with various filters and statistics.
 * </p>
 */
public interface TransactionService {

    /**
     * Creates a new transaction for the given account.
     *
     * @param accountId    the account identifier; must not be {@code null}
     * @param valueDate    the effective date of the transaction; must not be {@code null}
     * @param amount       the transaction amount; must not be {@code null} and positive
     * @param direction    the transaction direction; must not be {@code null}
     * @param description  an optional description; may be {@code null}
     * @param tagIds       the set of tag identifiers; may be empty or {@code null}
     * @param recurrenceId the identifier of the recurrence; may be {@code null}
     * @return the created {@link TransactionDTO}
     */
    TransactionDTO createTransaction(UUID accountId,
                                     OffsetDateTime valueDate,
                                     BigDecimal amount,
                                     TransactionDirection direction,
                                     String description,
                                     Set<UUID> tagIds,
                                     UUID recurrenceId);

    /**
     * Retrieves a transaction by its identifier.
     *
     * @param accountId     the account identifier; must not be {@code null}
     * @param transactionId the transaction identifier; must not be {@code null}
     * @return the matching {@link TransactionDTO}
     * @throws java.util.NoSuchElementException if no transaction exists with the given ID in the specified account
     */
    TransactionDTO getTransaction(UUID accountId, UUID transactionId);

    /**
     * Deletes a transaction by its identifier.
     *
     * @param accountId     the account identifier; must not be {@code null}
     * @param transactionId the transaction identifier; must not be {@code null}
     */
    void deleteTransaction(UUID accountId, UUID transactionId);

    /**
     * Updates an existing transaction.
     *
     * @param accountId     the account identifier; must not be {@code null}
     * @param transactionId the transaction identifier; must not be {@code null}
     * @param valueDate     the new value date; must not be {@code null}
     * @param amount        the new amount; must not be {@code null}
     * @param direction     the new direction; must not be {@code null}
     * @param description   the new description; may be {@code null}
     * @param tagIds        the updated set of tags; may be empty
     * @return the updated {@link TransactionDTO}
     */
    TransactionDTO updateTransaction(UUID accountId,
                                     UUID transactionId,
                                     OffsetDateTime valueDate,
                                     BigDecimal amount,
                                     TransactionDirection direction,
                                     String description,
                                     Set<UUID> tagIds);

    /**
     * Lists all transactions for the given account.
     *
     * @param accountId the account identifier; must not be {@code null}
     * @return a list of {@link TransactionDTO}; never {@code null}, may be empty
     */
    List<TransactionDTO> listTransactions(UUID accountId);

    /**
     * Lists all transactions associated with a specific tag.
     *
     * @param accountId the account identifier; must not be {@code null}
     * @param tagId     the tag identifier; must not be {@code null}
     * @return a list of {@link TransactionDTO}; never {@code null}, may be empty
     */
    List<TransactionDTO> listByTag(UUID accountId, UUID tagId);

    /**
     * Lists all transactions for an account filtered by direction.
     *
     * @param accountId the account identifier; must not be {@code null}
     * @param direction the transaction direction; must not be {@code null}
     * @return a list of {@link TransactionDTO}; never {@code null}, may be empty
     */
    List<TransactionDTO> listByDirection(UUID accountId, TransactionDirection direction);

    /**
     * Lists all transactions associated with a given recurrence.
     *
     * @param recurrenceId the recurrence identifier; must not be {@code null}
     * @return a list of {@link TransactionDTO}; never {@code null}, may be empty
     */
    List<TransactionDTO> listByRecurrence(UUID recurrenceId);

    /**
     * Lists all transactions within a date range.
     *
     * @param accountId the account identifier; must not be {@code null}
     * @param startDate the start date (inclusive); must not be {@code null}
     * @param endDate   the end date (inclusive); must not be {@code null}
     * @return a list of {@link TransactionDTO}; never {@code null}, may be empty
     */
    List<TransactionDTO> listByDateRange(UUID accountId, OffsetDateTime startDate, OffsetDateTime endDate);

    /**
     * Lists all future transactions from a given date.
     *
     * @param accountId the account identifier; must not be {@code null}
     * @param fromDate  the start date; must not be {@code null}
     * @return a list of {@link TransactionDTO}; never {@code null}, may be empty
     */
    List<TransactionDTO> listFutureTransactions(UUID accountId, OffsetDateTime fromDate);

    /**
     * Lists all past transactions up to a given date.
     *
     * @param accountId the account identifier; must not be {@code null}
     * @param toDate    the end date; must not be {@code null}
     * @return a list of {@link TransactionDTO}; never {@code null}, may be empty
     */
    List<TransactionDTO> listPastTransactions(UUID accountId, OffsetDateTime toDate);

    /**
     * Calculates the current balance for an account.
     *
     * @param accountId the account identifier; must not be {@code null}
     * @return the current balance; never {@code null}
     */
    BigDecimal getCurrentBalance(UUID accountId);

    /**
     * Calculates the total account balance.
     *
     * @param accountId the account identifier; must not be {@code null}
     * @return the total balance; never {@code null}
     */
    BigDecimal getAccountBalance(UUID accountId);

    /**
     * Searches for transactions matching the specified filter with pagination.
     *
     * @param accountId the account identifier; must not be {@code null}
     * @param filter    the transaction filter; must not be {@code null}
     * @param page      the page number (0-based)
     * @param size      the maximum number of results per page
     * @return a list of {@link TransactionDTO}; never {@code null}, may be empty
     */
    List<TransactionDTO> searchTransactions(UUID accountId, TransactionFilterDTO filter, int page, int size);

    /**
     * Counts transactions matching the specified filter.
     *
     * @param accountId the account identifier; must not be {@code null}
     * @param filter    the transaction filter; must not be {@code null}
     * @return the number of matching transactions
     */
    long countFilteredTransactions(UUID accountId, TransactionFilterDTO filter);

    /**
     * Retrieves account statistics for a given period.
     *
     * @param accountId the account identifier; must not be {@code null}
     * @param fromDate  the start date; must not be {@code null}
     * @param toDate    the end date; must not be {@code null}
     * @return a {@link StatisticsDTO} containing the statistics
     */
    StatisticsDTO getAccountStatistics(UUID accountId, OffsetDateTime fromDate, OffsetDateTime toDate);

    /**
     * Calculates the total income for a given period.
     *
     * @param accountId the account identifier; must not be {@code null}
     * @param fromDate  the start date; must not be {@code null}
     * @param toDate    the end date; must not be {@code null}
     * @return the total income; never {@code null}
     */
    BigDecimal calculateIncome(UUID accountId, OffsetDateTime fromDate, OffsetDateTime toDate);

    /**
     * Calculates the total expenses for a given period.
     *
     * @param accountId the account identifier; must not be {@code null}
     * @param fromDate  the start date; must not be {@code null}
     * @param toDate    the end date; must not be {@code null}
     * @return the total expenses; never {@code null}
     */
    BigDecimal calculateExpenses(UUID accountId, OffsetDateTime fromDate, OffsetDateTime toDate);

    /**
     * Retrieves spending grouped by category for a given period.
     *
     * @param accountId the account identifier; must not be {@code null}
     * @param fromDate  the start date; must not be {@code null}
     * @param toDate    the end date; must not be {@code null}
     * @return a map where the key is the category name and the value is the total amount spent
     */
    Map<String, BigDecimal> getSpendingByCategory(UUID accountId, OffsetDateTime fromDate, OffsetDateTime toDate);

    /**
     * Retrieves the most recent transactions for an account.
     *
     * @param accountId the account identifier; must not be {@code null}
     * @param limit     the maximum number of transactions to retrieve
     * @return a list of {@link TransactionDTO}; never {@code null}, may be empty
     */
    List<TransactionDTO> getRecentTransactions(UUID accountId, int limit);
}