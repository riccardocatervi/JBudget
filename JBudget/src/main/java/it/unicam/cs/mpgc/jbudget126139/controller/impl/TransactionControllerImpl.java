package it.unicam.cs.mpgc.jbudget126139.controller.impl;

import it.unicam.cs.mpgc.jbudget126139.controller.TransactionController;
import it.unicam.cs.mpgc.jbudget126139.model.TransactionDirection;
import it.unicam.cs.mpgc.jbudget126139.service.TransactionService;
import it.unicam.cs.mpgc.jbudget126139.service.dto.StatisticsDTO;
import it.unicam.cs.mpgc.jbudget126139.service.dto.TransactionDTO;
import it.unicam.cs.mpgc.jbudget126139.service.dto.TransactionFilterDTO;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.*;

/**
 * Implementation of {@link TransactionController} that delegates transaction-related
 * operations to a {@link TransactionService}.
 * <p>
 * Provides CRUD methods for transactions, as well as advanced queries such as
 * searching, filtering, statistics, and category-based spending analysis.
 * </p>
 */
public class TransactionControllerImpl implements TransactionController {

    private final TransactionService transactionService;

    /**
     * Creates a new {@code TransactionControllerImpl}.
     *
     * @param transactionService the service handling transaction operations (must not be null)
     */
    public TransactionControllerImpl(TransactionService transactionService) {
        this.transactionService = Objects.requireNonNull(transactionService);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TransactionDTO createTransaction(UUID accountId,
                                            OffsetDateTime valueDate,
                                            BigDecimal amount,
                                            TransactionDirection direction,
                                            String description,
                                            Set<UUID> tagIds) {
        requireNonNulls(accountId, valueDate, amount, direction);
        return transactionService.createTransaction(
                accountId, valueDate, amount, direction, description, tagIds, /*recurrenceId*/ null
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TransactionDTO getTransaction(UUID accountId, UUID transactionId) {
        requireNonNulls(accountId, transactionId);
        return transactionService.getTransaction(accountId, transactionId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TransactionDTO updateTransaction(UUID accountId,
                                            UUID transactionId,
                                            OffsetDateTime valueDate,
                                            BigDecimal amount,
                                            TransactionDirection direction,
                                            String description,
                                            Set<UUID> tagIds) {
        requireNonNulls(accountId, transactionId, valueDate, amount, direction);
        return transactionService.updateTransaction(
                accountId, transactionId, valueDate, amount, direction, description, tagIds
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TransactionDTO createTransactionWithRecurrence(UUID accountId,
                                                          OffsetDateTime valueDate,
                                                          BigDecimal amount,
                                                          TransactionDirection direction,
                                                          String description,
                                                          Set<UUID> tagIds,
                                                          UUID recurrenceId) {
        requireNonNulls(accountId, valueDate, amount, direction, recurrenceId);
        return transactionService.createTransaction(
                accountId, valueDate, amount, direction, description, tagIds, recurrenceId
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteTransaction(UUID accountId, UUID transactionId) {
        requireNonNulls(accountId, transactionId);
        transactionService.deleteTransaction(accountId, transactionId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TransactionDTO> listTransactions(UUID accountId) {
        Objects.requireNonNull(accountId, "accountId must not be null");
        return transactionService.listTransactions(accountId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TransactionDTO> listByTag(UUID accountId, UUID tagId) {
        requireNonNulls(accountId, tagId);
        return transactionService.listByTag(accountId, tagId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TransactionDTO> listByDirection(UUID accountId, TransactionDirection direction) {
        requireNonNulls(accountId, direction);
        return transactionService.listByDirection(accountId, direction);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TransactionDTO> listByDateRange(UUID accountId, OffsetDateTime start, OffsetDateTime end) {
        requireNonNulls(accountId, start, end);
        if (start.isAfter(end)) throw new IllegalArgumentException("start must be <= end");
        return transactionService.listByDateRange(accountId, start, end);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TransactionDTO> listByRecurrence(UUID recurrenceId) {
        Objects.requireNonNull(recurrenceId, "recurrenceId must not be null");
        return transactionService.listByRecurrence(recurrenceId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal getAccountBalance(UUID accountId) {
        Objects.requireNonNull(accountId, "accountId must not be null");
        return transactionService.getCurrentBalance(accountId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TransactionDTO> searchTransactions(UUID accountId, TransactionFilterDTO filter, int page, int size) {
        requireNonNulls(accountId, filter);
        if (page < 0) throw new IllegalArgumentException("page must be >= 0");
        if (size <= 0) throw new IllegalArgumentException("size must be > 0");
        return transactionService.searchTransactions(accountId, filter, page, size);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long countFilteredTransactions(UUID accountId, TransactionFilterDTO filter) {
        requireNonNulls(accountId, filter);
        return transactionService.countFilteredTransactions(accountId, filter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StatisticsDTO getAccountStatistics(UUID accountId, OffsetDateTime fromDate, OffsetDateTime toDate) {
        requireNonNulls(accountId, fromDate, toDate);
        if (fromDate.isAfter(toDate)) throw new IllegalArgumentException("fromDate must be <= toDate");
        return transactionService.getAccountStatistics(accountId, fromDate, toDate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal calculateIncome(UUID accountId, OffsetDateTime fromDate, OffsetDateTime toDate) {
        requireNonNulls(accountId, fromDate, toDate);
        if (fromDate.isAfter(toDate)) throw new IllegalArgumentException("fromDate must be <= toDate");
        return transactionService.calculateIncome(accountId, fromDate, toDate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal calculateExpenses(UUID accountId, OffsetDateTime fromDate, OffsetDateTime toDate) {
        requireNonNulls(accountId, fromDate, toDate);
        if (fromDate.isAfter(toDate)) throw new IllegalArgumentException("fromDate must be <= toDate");
        return transactionService.calculateExpenses(accountId, fromDate, toDate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, BigDecimal> getSpendingByCategory(UUID accountId, OffsetDateTime fromDate, OffsetDateTime toDate) {
        requireNonNulls(accountId, fromDate, toDate);
        if (fromDate.isAfter(toDate)) throw new IllegalArgumentException("fromDate must be <= toDate");
        return transactionService.getSpendingByCategory(accountId, fromDate, toDate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TransactionDTO> getRecentTransactions(UUID accountId, int limit) {
        Objects.requireNonNull(accountId, "accountId must not be null");
        if (limit <= 0) throw new IllegalArgumentException("limit must be > 0");
        return transactionService.getRecentTransactions(accountId, limit);
    }

    /**
     * Utility method that enforces that none of the provided arguments is {@code null}.
     *
     * @param values the values to check
     * @throws NullPointerException if any value is {@code null}
     */
    private void requireNonNulls(Object... values) {
        for (Object v : values) Objects.requireNonNull(v);
    }
}