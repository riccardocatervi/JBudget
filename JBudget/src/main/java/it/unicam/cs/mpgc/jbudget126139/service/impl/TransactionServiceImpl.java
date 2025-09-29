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

import it.unicam.cs.mpgc.jbudget126139.model.*;
import it.unicam.cs.mpgc.jbudget126139.persistence.TransactionRepository;
import it.unicam.cs.mpgc.jbudget126139.service.TransactionService;
import it.unicam.cs.mpgc.jbudget126139.service.dto.StatisticsDTO;
import it.unicam.cs.mpgc.jbudget126139.service.dto.TransactionDTO;
import it.unicam.cs.mpgc.jbudget126139.service.dto.TransactionFilterDTO;
import it.unicam.cs.mpgc.jbudget126139.service.mapper.TransactionMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.function.Function;

/**
 * Default implementation of {@link TransactionService}.
 * <p>
 * Provides CRUD operations, search, and statistics for {@link Transaction} entities.
 * </p>
 */
public class TransactionServiceImpl implements TransactionService {

    private final EntityManagerFactory emf;
    private final Function<EntityManager, TransactionRepository<NormalTransaction>> transactionRepositoryProvider;

    /**
     * Creates a new {@code TransactionServiceImpl} instance.
     *
     * @param emf                          the {@link EntityManagerFactory}; must not be {@code null}
     * @param transactionRepositoryProvider a function to create a {@link TransactionRepository}; must not be {@code null}
     */
    public TransactionServiceImpl(EntityManagerFactory emf,
                                  Function<EntityManager, TransactionRepository<NormalTransaction>> transactionRepositoryProvider) {
        this.emf = Objects.requireNonNull(emf, "EntityManagerFactory must not be null");
        this.transactionRepositoryProvider = Objects.requireNonNull(transactionRepositoryProvider,
                "Transaction repository provider must not be null");
    }

    /** {@inheritDoc} */
    @Override
    public TransactionDTO createTransaction(UUID accountId,
                                            OffsetDateTime valueDate,
                                            BigDecimal amount,
                                            TransactionDirection direction,
                                            String description,
                                            Set<UUID> tagIds,
                                            UUID recurrenceId) {
        requireNonNulls(accountId, valueDate, amount, direction);

        return executeInTransaction(em -> {
            var repo = transactionRepositoryProvider.apply(em);
            NormalTransaction transaction = buildTransaction(em, accountId, valueDate, amount,
                    direction, description, tagIds, recurrenceId);
            repo.save(transaction);
            return TransactionMapper.INSTANCE.toDto(transaction);
        });
    }

    /** {@inheritDoc} */
    @Override
    public TransactionDTO getTransaction(UUID accountId, UUID transactionId) {
        requireNonNulls(accountId, transactionId);

        return executeReadOnly(em -> {
            var repo = transactionRepositoryProvider.apply(em);
            return repo.findByAccountAndId(accountId, transactionId)
                    .map(TransactionMapper.INSTANCE::toDto)
                    .orElseThrow(() -> new EntityNotFoundException(
                            String.format("Transaction with id %s not found for account %s", transactionId, accountId)));
        });
    }

    /** {@inheritDoc} */
    @Override
    public void deleteTransaction(UUID accountId, UUID transactionId) {
        requireNonNulls(accountId, transactionId);

        executeInTransaction(em -> {
            var repo = transactionRepositoryProvider.apply(em);
            var transaction = repo.findByAccountAndId(accountId, transactionId)
                    .orElseThrow(() -> new EntityNotFoundException(
                            String.format("Transaction with id %s not found for account %s", transactionId, accountId)));
            repo.delete(transaction);
            return null; // Void operation
        });
    }

    /** {@inheritDoc} */
    @Override
    public TransactionDTO updateTransaction(UUID accountId,
                                            UUID transactionId,
                                            OffsetDateTime valueDate,
                                            BigDecimal amount,
                                            TransactionDirection direction,
                                            String description,
                                            Set<UUID> tagIds) {
        requireNonNulls(accountId, transactionId, valueDate, amount, direction);

        return executeInTransaction(em -> {
            var repo = transactionRepositoryProvider.apply(em);
            var transaction = repo.findByAccountAndId(accountId, transactionId)
                    .orElseThrow(() -> new EntityNotFoundException(
                            String.format("Transaction with id %s not found for account %s", transactionId, accountId)));

            updateTransactionDetails(transaction, valueDate, amount, direction, description, tagIds);
            return TransactionMapper.INSTANCE.toDto(transaction);
        });
    }

    /** {@inheritDoc} */
    @Override
    public List<TransactionDTO> listTransactions(UUID accountId) {
        requireNonNulls(accountId);

        return executeReadOnly(em -> {
            var repo = transactionRepositoryProvider.apply(em);
            List<NormalTransaction> transactions = repo.findByAccountOrderByValueDateDesc(accountId);
            return mapToTransactionDTOs(transactions);
        });
    }

    /** {@inheritDoc} */
    @Override
    public List<TransactionDTO> listByTag(UUID accountId, UUID tagId) {
        requireNonNulls(accountId, tagId);

        return executeReadOnly(em -> {
            var repo = transactionRepositoryProvider.apply(em);
            List<NormalTransaction> transactions = repo.findByAccountAndTagIncludingDescendants(accountId, tagId);
            return mapToTransactionDTOs(transactions);
        });
    }

    /** {@inheritDoc} */
    @Override
    public List<TransactionDTO> listByDirection(UUID accountId, TransactionDirection direction) {
        requireNonNulls(accountId, direction);

        return executeReadOnly(em -> {
            var repo = transactionRepositoryProvider.apply(em);
            List<NormalTransaction> transactions = repo.findByAccountAndDirection(accountId, direction);
            return mapToTransactionDTOs(transactions);
        });
    }

    /** {@inheritDoc} */
    @Override
    public List<TransactionDTO> listByRecurrence(UUID recurrenceId) {
        requireNonNulls(recurrenceId);
        return executeReadOnly(em -> {
            var repo = transactionRepositoryProvider.apply(em);
            List<NormalTransaction> transactions = repo.findByRecurrenceId(recurrenceId);
            return mapToTransactionDTOs(transactions);
        });
    }

    /** {@inheritDoc} */
    @Override
    public List<TransactionDTO> listByDateRange(UUID accountId, OffsetDateTime startDate, OffsetDateTime endDate) {
        requireNonNulls(accountId, startDate, endDate);
        return executeReadOnly(em -> {
            var repo = transactionRepositoryProvider.apply(em);
            List<NormalTransaction> transactions = repo.findByAccountAndValueDateBetween(accountId, startDate, endDate);
            return mapToTransactionDTOs(transactions);
        });
    }

    /** {@inheritDoc} */
    @Override
    public List<TransactionDTO> listFutureTransactions(UUID accountId, OffsetDateTime fromDate) {
        requireNonNulls(accountId, fromDate);
        return executeReadOnly(em -> {
            var repo = transactionRepositoryProvider.apply(em);
            List<NormalTransaction> transactions = repo.findByAccountAndValueDateAfter(accountId, fromDate);
            return mapToTransactionDTOs(transactions);
        });
    }

    /** {@inheritDoc} */
    @Override
    public List<TransactionDTO> listPastTransactions(UUID accountId, OffsetDateTime toDate) {
        requireNonNulls(accountId, toDate);
        return executeReadOnly(em -> {
            var repo = transactionRepositoryProvider.apply(em);
            List<NormalTransaction> transactions = repo.findByAccountAndValueDateBeforeOrEqual(accountId, toDate);
            return mapToTransactionDTOs(transactions);
        });
    }

    /** {@inheritDoc} */
    @Override
    public BigDecimal getCurrentBalance(UUID accountId) {
        requireNonNulls(accountId);
        return executeReadOnly(em -> {
            var repo = transactionRepositoryProvider.apply(em);
            var txs = repo.findByAccountOrderByValueDateDesc(accountId);
            return txs.stream()
                    .map(t -> t.getDirection() == TransactionDirection.CREDIT
                            ? t.getAmount()
                            : t.getAmount().negate())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        });
    }

    /** {@inheritDoc} */
    @Override
    public BigDecimal getAccountBalance(UUID accountId) {
        Objects.requireNonNull(accountId, "accountId must not be null");

        return executeReadOnly(em -> {
            var repo = transactionRepositoryProvider.apply(em);
            return repo.calculateBalance(accountId);
        });
    }

    /** {@inheritDoc} */
    @Override
    public List<TransactionDTO> searchTransactions(UUID accountId, TransactionFilterDTO filter, int page, int size) {
        requireNonNulls(accountId, filter);

        return executeReadOnly(em -> {
            var repo = transactionRepositoryProvider.apply(em);
            int offset = page * size;
            List<NormalTransaction> transactions = repo.findByAccountWithFilters(accountId, filter, offset, size);
            return mapToTransactionDTOs(transactions);
        });
    }

    /** {@inheritDoc} */
    @Override
    public long countFilteredTransactions(UUID accountId, TransactionFilterDTO filter) {
        requireNonNulls(accountId, filter);

        return executeReadOnly(em -> {
            var repo = transactionRepositoryProvider.apply(em);
            return repo.countByAccountWithFilters(accountId, filter);
        });
    }

    /** {@inheritDoc} */
    @Override
    public StatisticsDTO getAccountStatistics(UUID accountId, OffsetDateTime fromDate, OffsetDateTime toDate) {
        requireNonNulls(accountId, fromDate, toDate);

        return executeReadOnly(em -> {
            var repo = transactionRepositoryProvider.apply(em);

            BigDecimal balance = repo.calculateBalance(accountId);
            BigDecimal income = repo.calculateIncomeInPeriod(accountId, fromDate, toDate);
            BigDecimal expenses = repo.calculateExpensesInPeriod(accountId, fromDate, toDate);
            BigDecimal netAmount = income.subtract(expenses);

            Map<String, BigDecimal> spendingByCategory = repo.calculateSpendingByCategory(accountId, fromDate, toDate);
            List<NormalTransaction> recentTransactions = repo.findRecentTransactions(accountId, 10);
            List<TransactionDTO> recentTransactionDTOs = mapToTransactionDTOs(recentTransactions);

            long totalCount = repo.countByAccountWithFilters(accountId, TransactionFilterDTO.empty());

            return new StatisticsDTO(
                    balance, income, expenses, netAmount,
                    spendingByCategory, recentTransactionDTOs, (int) totalCount
            );
        });
    }

    /** {@inheritDoc} */
    @Override
    public BigDecimal calculateIncome(UUID accountId, OffsetDateTime fromDate, OffsetDateTime toDate) {
        requireNonNulls(accountId, fromDate, toDate);

        return executeReadOnly(em -> {
            var repo = transactionRepositoryProvider.apply(em);
            return repo.calculateIncomeInPeriod(accountId, fromDate, toDate);
        });
    }

    /** {@inheritDoc} */
    @Override
    public BigDecimal calculateExpenses(UUID accountId, OffsetDateTime fromDate, OffsetDateTime toDate) {
        requireNonNulls(accountId, fromDate, toDate);

        return executeReadOnly(em -> {
            var repo = transactionRepositoryProvider.apply(em);
            return repo.calculateExpensesInPeriod(accountId, fromDate, toDate);
        });
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, BigDecimal> getSpendingByCategory(UUID accountId, OffsetDateTime fromDate, OffsetDateTime toDate) {
        requireNonNulls(accountId, fromDate, toDate);

        return executeReadOnly(em -> {
            var repo = transactionRepositoryProvider.apply(em);
            return repo.calculateSpendingByCategory(accountId, fromDate, toDate);
        });
    }

    /** {@inheritDoc} */
    @Override
    public List<TransactionDTO> getRecentTransactions(UUID accountId, int limit) {
        requireNonNulls(accountId);

        return executeReadOnly(em -> {
            var repo = transactionRepositoryProvider.apply(em);
            List<NormalTransaction> transactions = repo.findRecentTransactions(accountId, limit);
            return mapToTransactionDTOs(transactions);
        });
    }


    // Private helper methods

    private <T> T executeInTransaction(Function<EntityManager, T> operation) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            T result = operation.apply(em);
            em.getTransaction().commit();
            return result;
        } catch (RuntimeException e) {
            rollbackSafely(em);
            throw e;
        } finally {
            em.close();
        }
    }

    private <T> T executeReadOnly(Function<EntityManager, T> operation) {
        EntityManager em = emf.createEntityManager();
        try {
            return operation.apply(em);
        } finally {
            em.close();
        }
    }

    private void rollbackSafely(EntityManager em) {
        try {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
        } catch (RuntimeException ignored) {
        }
    }

    private NormalTransaction buildTransaction(EntityManager em, UUID accountId, OffsetDateTime valueDate,
                                               BigDecimal amount, TransactionDirection direction,
                                               String description, Set<UUID> tagIds, UUID recurrenceId) {
        AbstractAccount account = em.getReference(NormalAccount.class, accountId);
        Set<UUID> safeTags = tagIds != null ? tagIds : Collections.emptySet();
        return recurrenceId == null
                ? new NormalTransaction(valueDate, amount, direction, account, description, safeTags)
                : new NormalTransaction(valueDate, amount, direction, account, description, safeTags, recurrenceId);
    }

    private void updateTransactionDetails(NormalTransaction transaction, OffsetDateTime valueDate,
                                          BigDecimal amount, TransactionDirection direction,
                                          String description, Set<UUID> tagIds) {
        Set<UUID> safeTags = tagIds != null ? tagIds : Collections.emptySet();
        transaction.updateDetails(valueDate, amount, direction, description, safeTags);
    }

    private List<TransactionDTO> mapToTransactionDTOs(List<NormalTransaction> transactions) {
        return transactions.stream().map(TransactionMapper.INSTANCE::toDto).toList();
    }

    private void requireNonNulls(Object... objects) {
        for (Object obj : objects)
            Objects.requireNonNull(obj, "Parameter must not be null");
    }
}