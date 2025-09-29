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

import it.unicam.cs.mpgc.jbudget126139.model.Transaction;
import it.unicam.cs.mpgc.jbudget126139.model.TransactionDirection;
import it.unicam.cs.mpgc.jbudget126139.service.dto.TransactionFilterDTO;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.*;

/**
 * Repository interface for managing {@link Transaction} entities.
 *
 * @param <T> the concrete type of {@link Transaction} managed by this repository
 */
public interface TransactionRepository<T extends Transaction> extends GenericRepository<T, UUID> {

    /**
     * Finds a transaction by its account and transaction ID.
     *
     * @param accountId     the account identifier; must not be {@code null}.
     * @param transactionId the transaction identifier; must not be {@code null}.
     * @return an {@link Optional} containing the transaction if found, otherwise empty.
     */
    Optional<T> findByAccountAndId(UUID accountId, UUID transactionId);

    /**
     * Retrieves all transactions for an account, ordered by value date descending.
     *
     * @param accountId the account identifier; must not be {@code null}.
     * @return a list of transactions sorted from newest to oldest.
     */
    List<T> findByAccountOrderByValueDateDesc(UUID accountId);

    /**
     * Retrieves all transactions for an account occurring after the specified date.
     *
     * @param accountId the account identifier; must not be {@code null}.
     * @param asOf      the minimum value date; must not be {@code null}.
     * @return a list of transactions matching the criteria.
     */
    List<T> findByAccountAndValueDateAfter(UUID accountId, OffsetDateTime asOf);

    /**
     * Retrieves all transactions for an account occurring before or on the specified date.
     *
     * @param accountId the account identifier; must not be {@code null}.
     * @param asOf      the maximum value date; must not be {@code null}.
     * @return a list of transactions matching the criteria.
     */
    List<T> findByAccountAndValueDateBeforeOrEqual(UUID accountId, OffsetDateTime asOf);

    /**
     * Retrieves all transactions for an account that have any of the specified tags.
     *
     * @param accountId the account identifier; must not be {@code null}.
     * @param tagIds    the set of tag identifiers; must not be {@code null}.
     * @return a list of transactions matching the criteria.
     */
    List<T> findByAccountAndTagsIn(UUID accountId, Set<UUID> tagIds);

    /**
     * Retrieves all transactions for an account matching a specific direction.
     *
     * @param accountId  the account identifier; must not be {@code null}.
     * @param direction  the transaction direction; must not be {@code null}.
     * @return a list of transactions matching the criteria.
     */
    List<T> findByAccountAndDirection(UUID accountId, TransactionDirection direction);

    /**
     * Retrieves all transactions for an account within a specific date range.
     *
     * @param accountId the account identifier; must not be {@code null}.
     * @param startDate the start date; must not be {@code null}.
     * @param endDate   the end date; must not be {@code null}.
     * @return a list of transactions in the date range.
     */
    List<T> findByAccountAndValueDateBetween(UUID accountId,
                                             OffsetDateTime startDate,
                                             OffsetDateTime endDate);

    /**
     * Retrieves all transactions for an account that have the given tag or any of its descendants.
     *
     * @param accountId the account identifier; must not be {@code null}.
     * @param tagId     the root tag identifier; must not be {@code null}.
     * @return a list of transactions matching the criteria.
     */
    List<T> findByAccountAndTagIncludingDescendants(UUID accountId, UUID tagId);

    /**
     * Retrieves all transactions associated with a specific recurrence.
     *
     * @param recurrenceId the recurrence identifier; must not be {@code null}.
     * @return a list of transactions linked to the recurrence.
     */
    List<T> findByRecurrenceId(UUID recurrenceId);

    /**
     * Retrieves all transactions for an account applying dynamic filters, with pagination.
     *
     * @param accountId the account identifier; must not be {@code null}.
     * @param filter    the filter criteria; must not be {@code null}.
     * @param offset    the starting index; must be >= 0.
     * @param limit     the maximum number of results to return; must be > 0.
     * @return a list of filtered transactions.
     */
    List<T> findByAccountWithFilters(UUID accountId, TransactionFilterDTO filter, int offset, int limit);

    /**
     * Counts the number of transactions for an account that match the given filter.
     *
     * @param accountId the account identifier; must not be {@code null}.
     * @param filter    the filter criteria; must not be {@code null}.
     * @return the number of matching transactions.
     */
    long countByAccountWithFilters(UUID accountId, TransactionFilterDTO filter);

    /**
     * Calculates the current balance of an account.
     *
     * @param accountId the account identifier; must not be {@code null}.
     * @return the account balance.
     */
    BigDecimal calculateBalance(UUID accountId);

    /**
     * Calculates the total income of an account in the given period.
     *
     * @param accountId the account identifier; must not be {@code null}.
     * @param from      the start date; must not be {@code null}.
     * @param to        the end date; must not be {@code null}.
     * @return the total income.
     */
    BigDecimal calculateIncomeInPeriod(UUID accountId, OffsetDateTime from, OffsetDateTime to);

    /**
     * Calculates the total expenses of an account in the given period.
     *
     * @param accountId the account identifier; must not be {@code null}.
     * @param from      the start date; must not be {@code null}.
     * @param to        the end date; must not be {@code null}.
     * @return the total expenses.
     */
    BigDecimal calculateExpensesInPeriod(UUID accountId, OffsetDateTime from, OffsetDateTime to);

    /**
     * Calculates total spending grouped by category in the given period.
     *
     * @param accountId the account identifier; must not be {@code null}.
     * @param from      the start date; must not be {@code null}.
     * @param to        the end date; must not be {@code null}.
     * @return a map where the key is the category name and the value is the total amount spent.
     */
    Map<String, BigDecimal> calculateSpendingByCategory(UUID accountId, OffsetDateTime from, OffsetDateTime to);

    /**
     * Retrieves the most recent transactions for an account.
     *
     * @param accountId the account identifier; must not be {@code null}.
     * @param limit     the maximum number of results to return; must be > 0.
     * @return a list of recent transactions.
     */
    List<T> findRecentTransactions(UUID accountId, int limit);
}