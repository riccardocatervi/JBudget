package it.unicam.cs.mpgc.jbudget126139.controller;

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
 * Controller interface for managing financial transactions.
 * <p>
 * Defines operations to create, retrieve, update, delete,
 * filter, and analyze transactions associated with accounts.
 * </p>
 */
public interface TransactionController {

    /**
     * Creates a new transaction in an account.
     *
     * @param accountId   the ID of the account
     * @param valueDate   the transaction date
     * @param amount      the transaction amount
     * @param direction   the transaction type (income/expense)
     * @param description an optional description
     * @param tagIds      optional set of category IDs
     * @return the created {@link TransactionDTO}
     */
    TransactionDTO createTransaction(UUID accountId, OffsetDateTime valueDate,
                                     BigDecimal amount, TransactionDirection direction, String description, Set<UUID> tagIds);

    /**
     * Creates a new transaction linked to a recurrence.
     *
     * @param accountId    the ID of the account
     * @param valueDate    the transaction date
     * @param amount       the transaction amount
     * @param direction    the transaction type
     * @param description  an optional description
     * @param tagIds       optional set of category IDs
     * @param recurrenceId the recurrence ID
     * @return the created {@link TransactionDTO}
     */
    TransactionDTO createTransactionWithRecurrence(UUID accountId,
                                                   OffsetDateTime valueDate,
                                                   BigDecimal amount,
                                                   TransactionDirection direction,
                                                   String description,
                                                   Set<UUID> tagIds,
                                                   UUID recurrenceId);

    /**
     * Retrieves a transaction by ID.
     *
     * @param accountId     the ID of the account
     * @param transactionId the ID of the transaction
     * @return the {@link TransactionDTO}, or {@code null} if not found
     */
    TransactionDTO getTransaction(UUID accountId, UUID transactionId);

    /**
     * Updates an existing transaction.
     *
     * @param accountId     the ID of the account
     * @param transactionId the ID of the transaction
     * @param valueDate     the new date
     * @param amount        the new amount
     * @param direction     the new type
     * @param description   the new description, may be {@code null}
     * @param tagIds        the new set of categories
     * @return the updated {@link TransactionDTO}
     */
    TransactionDTO updateTransaction(UUID accountId, UUID transactionId,
                                     OffsetDateTime valueDate, BigDecimal amount, TransactionDirection direction,
                                     String description, Set<UUID> tagIds);

    /**
     * Deletes a transaction by ID.
     *
     * @param accountId     the ID of the account
     * @param transactionId the ID of the transaction
     */
    void deleteTransaction(UUID accountId, UUID transactionId);

    /**
     * Lists all transactions of an account.
     *
     * @param accountId the ID of the account
     * @return the list of transactions
     */
    List<TransactionDTO> listTransactions(UUID accountId);

    /**
     * Lists transactions filtered by category.
     *
     * @param accountId the ID of the account
     * @param tagId     the ID of the category
     * @return the list of matching transactions
     */
    List<TransactionDTO> listByTag(UUID accountId, UUID tagId);

    /**
     * Lists transactions filtered by type (income or expense).
     *
     * @param accountId  the ID of the account
     * @param direction  the transaction type
     * @return the list of matching transactions
     */
    List<TransactionDTO> listByDirection(UUID accountId, TransactionDirection direction);

    /**
     * Lists transactions within a date range.
     *
     * @param accountId the ID of the account
     * @param start     the start date (inclusive)
     * @param end       the end date (inclusive)
     * @return the list of matching transactions
     */
    List<TransactionDTO> listByDateRange(UUID accountId, OffsetDateTime start, OffsetDateTime end);

    /**
     * Lists transactions linked to a recurrence.
     *
     * @param recurrenceId the recurrence ID
     * @return the list of transactions
     */
    List<TransactionDTO> listByRecurrence(UUID recurrenceId);

    /**
     * Gets the current balance of an account.
     *
     * @param accountId the ID of the account
     * @return the balance as {@link BigDecimal}
     */
    BigDecimal getAccountBalance(UUID accountId);

    /**
     * Searches transactions with filters and pagination.
     *
     * @param accountId the ID of the account
     * @param filter    the filtering criteria
     * @param page      the page index (0-based)
     * @param size      the number of results per page
     * @return the list of matching transactions
     */
    List<TransactionDTO> searchTransactions(UUID accountId, TransactionFilterDTO filter, int page, int size);

    /**
     * Counts transactions matching a given filter.
     *
     * @param accountId the ID of the account
     * @param filter    the filtering criteria
     * @return the total number of matching transactions
     */
    long countFilteredTransactions(UUID accountId, TransactionFilterDTO filter);

    /**
     * Retrieves account statistics (income, expenses, balance) within a period.
     *
     * @param accountId the ID of the account
     * @param fromDate  the start date
     * @param toDate    the end date
     * @return a {@link StatisticsDTO} with the aggregated data
     */
    StatisticsDTO getAccountStatistics(UUID accountId, OffsetDateTime fromDate, OffsetDateTime toDate);

    /**
     * Calculates total income for a period.
     *
     * @param accountId the ID of the account
     * @param fromDate  the start date
     * @param toDate    the end date
     * @return the total income
     */
    BigDecimal calculateIncome(UUID accountId, OffsetDateTime fromDate, OffsetDateTime toDate);

    /**
     * Calculates total expenses for a period.
     *
     * @param accountId the ID of the account
     * @param fromDate  the start date
     * @param toDate    the end date
     * @return the total expenses
     */
    BigDecimal calculateExpenses(UUID accountId, OffsetDateTime fromDate, OffsetDateTime toDate);

    /**
     * Returns spending grouped by category.
     *
     * @param accountId the ID of the account
     * @param fromDate  the start date
     * @param toDate    the end date
     * @return a map where the key is the category name and the value is the amount spent
     */
    Map<String, BigDecimal> getSpendingByCategory(UUID accountId, OffsetDateTime fromDate, OffsetDateTime toDate);

    /**
     * Returns the most recent transactions of an account.
     *
     * @param accountId the ID of the account
     * @param limit     the maximum number of transactions
     * @return the list of recent transactions
     */
    List<TransactionDTO> getRecentTransactions(UUID accountId, int limit);
}