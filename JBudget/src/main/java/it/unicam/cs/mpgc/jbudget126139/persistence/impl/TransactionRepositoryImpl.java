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

package it.unicam.cs.mpgc.jbudget126139.persistence.impl;

import it.unicam.cs.mpgc.jbudget126139.model.Transaction;
import it.unicam.cs.mpgc.jbudget126139.model.TransactionDirection;
import it.unicam.cs.mpgc.jbudget126139.persistence.TransactionRepository;
import it.unicam.cs.mpgc.jbudget126139.service.dto.TransactionFilterDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.*;

/**
 * JPA-based implementation of {@link TransactionRepository} for managing {@link Transaction} entities.
 * <p>
 * Provides CRUD operations and transaction-specific queries using an injected {@link EntityManager}.
 * </p>
 *
 * @param <T> the concrete subtype of {@link Transaction} handled by this repository
 */
public class TransactionRepositoryImpl<T extends Transaction> implements TransactionRepository<T> {

    /** The JPA {@link EntityManager} used for persistence operations. */
    private final EntityManager em;

    /** The concrete transaction entity type managed by this repository. */
    private final Class<T> entityType;

    /**
     * Creates a new repository instance for the given transaction entity type.
     *
     * @param em         the {@link EntityManager}; must not be {@code null}.
     * @param entityType the concrete transaction entity type; must not be {@code null}.
     * @throws NullPointerException if {@code em} or {@code entityType} is {@code null}.
     */
    public TransactionRepositoryImpl(EntityManager em, Class<T> entityType) {
        this.em = Objects.requireNonNull(em, "EntityManager must not be null");
        this.entityType = Objects.requireNonNull(entityType, "entityType must not be null");
    }

    /** {@inheritDoc} */
    @Override
    public Optional<T> findByAccountAndId(UUID accountId, UUID transactionId) {
        requireNonNulls(accountId, transactionId);
        try {
            TypedQuery<T> query = createQuery(
                    "SELECT t FROM " + getEntityName() + " t " +
                            "WHERE t.account.id = :accountId AND t.id = :transactionId");
            T transaction = query
                    .setParameter("accountId", accountId)
                    .setParameter("transactionId", transactionId)
                    .getSingleResult();
            return Optional.of(transaction);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<T> findByAccountOrderByValueDateDesc(UUID accountId) {
        Objects.requireNonNull(accountId, "accountId must not be null");
        return createQuery(
                "SELECT t FROM " + getEntityName() + " t " +
                        "WHERE t.account.id = :accountId " +
                        "ORDER BY t.valueDate DESC")
                .setParameter("accountId", accountId)
                .getResultList();
    }

    /** {@inheritDoc} */
    @Override
    public List<T> findByAccountAndValueDateAfter(UUID accountId, OffsetDateTime asOf) {
        requireNonNulls(accountId, asOf);
        return createQuery(
                "SELECT t FROM " + getEntityName() + " t " +
                        "WHERE t.account.id = :accountId AND t.valueDate > :asOf " +
                        "ORDER BY t.valueDate ASC")
                .setParameter("accountId", accountId)
                .setParameter("asOf", asOf)
                .getResultList();
    }

    /** {@inheritDoc} */
    @Override
    public List<T> findByAccountAndValueDateBeforeOrEqual(UUID accountId, OffsetDateTime asOf) {
        requireNonNulls(accountId, asOf);
        return createQuery(
                "SELECT t FROM " + getEntityName() + " t " +
                        "WHERE t.account.id = :accountId AND t.valueDate <= :asOf " +
                        "ORDER BY t.valueDate DESC")
                .setParameter("accountId", accountId)
                .setParameter("asOf", asOf)
                .getResultList();
    }

    /** {@inheritDoc} */
    @Override
    public List<T> findByAccountAndValueDateBetween(UUID accountId, OffsetDateTime startDate, OffsetDateTime endDate) {
        requireNonNulls(accountId, startDate, endDate);
        validateDateRange(startDate, endDate);

        return createQuery(
                "SELECT t FROM " + getEntityName() + " t " +
                        "WHERE t.account.id = :accountId " +
                        "AND t.valueDate >= :startDate AND t.valueDate <= :endDate " +
                        "ORDER BY t.valueDate DESC")
                .setParameter("accountId", accountId)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getResultList();
    }

    /** {@inheritDoc} */
    @Override
    public List<T> findByAccountAndDirection(UUID accountId, TransactionDirection direction) {
        requireNonNulls(accountId, direction);
        return createQuery(
                "SELECT t FROM " + getEntityName() + " t " +
                        "WHERE t.account.id = :accountId AND t.direction = :direction " +
                        "ORDER BY t.valueDate DESC")
                .setParameter("accountId", accountId)
                .setParameter("direction", direction)
                .getResultList();
    }

    /** {@inheritDoc} */
    @Override
    public List<T> findByAccountAndTagsIn(UUID accountId, Set<UUID> tagIds) {
        requireNonNulls(accountId, tagIds);
        validateTagIds(tagIds);

        return createQuery(
                "SELECT DISTINCT t FROM " + getEntityName() + " t " +
                        "JOIN t.tags tag " +
                        "WHERE t.account.id = :accountId AND tag IN :tagIds " +
                        "ORDER BY t.valueDate DESC")
                .setParameter("accountId", accountId)
                .setParameter("tagIds", tagIds)
                .getResultList();
    }

    /** {@inheritDoc} */
    @Override
    public List<T> findByAccountAndTagIncludingDescendants(UUID accountId, UUID tagId) {
        requireNonNulls(accountId, tagId);
        Set<UUID> allTagIds = collectDescendantTagIds(tagId);
        return findByAccountAndTagsIn(accountId, allTagIds);
    }

    /** {@inheritDoc} */
    @Override
    public List<T> findByRecurrenceId(UUID recurrenceId) {
        Objects.requireNonNull(recurrenceId, "recurrenceId must not be null");
        return createQuery(
                "SELECT t FROM " + getEntityName() + " t " +
                        "WHERE t.recurrenceId = :recurrenceId " +
                        "ORDER BY t.valueDate ASC")
                .setParameter("recurrenceId", recurrenceId)
                .getResultList();
    }

    /** {@inheritDoc} */
    @Override
    public void save(T entity) {
        Objects.requireNonNull(entity, "entity must not be null");
        if (entity.getId() == null)
            em.persist(entity);
        else em.merge(entity);
    }

    /** {@inheritDoc} */
    @Override
    public Optional<T> findById(UUID id) {
        Objects.requireNonNull(id, "id must not be null");
        return Optional.ofNullable(em.find(entityType, id));
    }

    /** {@inheritDoc} */
    @Override
    public void delete(T entity) {
        Objects.requireNonNull(entity, "entity must not be null");
        T managed = getManagedEntity(entity);
        if (managed != null)
            em.remove(managed);
    }

    /** {@inheritDoc} */
    @Override
    public void deleteById(UUID id) {
        Objects.requireNonNull(id, "id must not be null");
        T entity = em.find(entityType, id);
        if (entity != null)
            em.remove(entity);
    }

    /** {@inheritDoc} */
    @Override
    public List<T> findByAccountWithFilters(UUID accountId, TransactionFilterDTO filter, int offset, int limit) {
        requireNonNulls(accountId, filter);

        StringBuilder jpql = new StringBuilder("SELECT t FROM " + getEntityName() + " t WHERE t.account.id = :accountId");
        addFilterConditions(jpql, filter);
        jpql.append(" ORDER BY t.valueDate DESC");

        TypedQuery<T> query = createQuery(jpql.toString()).setParameter("accountId", accountId);
        setFilterParameters(query, filter);

        return query.setFirstResult(offset).setMaxResults(limit).getResultList();
    }

    /** {@inheritDoc} */
    @Override
    public long countByAccountWithFilters(UUID accountId, TransactionFilterDTO filter) {
        requireNonNulls(accountId, filter);

        StringBuilder jpql = new StringBuilder("SELECT COUNT(t) FROM " + getEntityName() + " t WHERE t.account.id = :accountId");
        addFilterConditions(jpql, filter);

        TypedQuery<Long> query = em.createQuery(jpql.toString(), Long.class).setParameter("accountId", accountId);
        setFilterParameters(query, filter);

        return query.getSingleResult();
    }

    /** {@inheritDoc} */
    @Override
    public BigDecimal calculateBalance(UUID accountId) {
        Objects.requireNonNull(accountId, "accountId must not be null");

        try {
            BigDecimal result = em.createQuery(
                            "SELECT COALESCE(SUM(CASE WHEN t.direction = :credit THEN t.amount ELSE -t.amount END), 0) " +
                                    "FROM " + getEntityName() + " t WHERE t.account.id = :accountId", BigDecimal.class)
                    .setParameter("accountId", accountId)
                    .setParameter("credit", TransactionDirection.CREDIT)
                    .getSingleResult();

            return result != null ? result : BigDecimal.ZERO;
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    /** {@inheritDoc} */
    @Override
    public BigDecimal calculateIncomeInPeriod(UUID accountId, OffsetDateTime fromDate, OffsetDateTime toDate) {
        requireNonNulls(accountId, fromDate, toDate);

        try {
            BigDecimal result = em.createQuery(
                            "SELECT COALESCE(SUM(t.amount), 0) FROM " + getEntityName() + " t " +
                                    "WHERE t.account.id = :accountId AND t.direction = :credit " +
                                    "AND t.valueDate >= :fromDate AND t.valueDate <= :toDate", BigDecimal.class)
                    .setParameter("accountId", accountId)
                    .setParameter("credit", TransactionDirection.CREDIT)
                    .setParameter("fromDate", fromDate)
                    .setParameter("toDate", toDate)
                    .getSingleResult();

            return result != null ? result : BigDecimal.ZERO;
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    /** {@inheritDoc} */
    @Override
    public BigDecimal calculateExpensesInPeriod(UUID accountId, OffsetDateTime fromDate, OffsetDateTime toDate) {
        requireNonNulls(accountId, fromDate, toDate);

        try {
            BigDecimal result = em.createQuery(
                            "SELECT COALESCE(SUM(t.amount), 0) FROM " + getEntityName() + " t " +
                                    "WHERE t.account.id = :accountId AND t.direction = :debit " +
                                    "AND t.valueDate >= :fromDate AND t.valueDate <= :toDate", BigDecimal.class)
                    .setParameter("accountId", accountId)
                    .setParameter("debit", TransactionDirection.DEBIT)
                    .setParameter("fromDate", fromDate)
                    .setParameter("toDate", toDate)
                    .getSingleResult();

            return result != null ? result : BigDecimal.ZERO;
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, BigDecimal> calculateSpendingByCategory(UUID accountId, OffsetDateTime fromDate, OffsetDateTime toDate) {
        requireNonNulls(accountId, fromDate, toDate);

        try {
            // Prima recuperiamo le transazioni con i loro importi
            List<Object[]> results = em.createQuery(
                            "SELECT t.id, t.amount FROM " + getEntityName() + " t " +
                                    "WHERE t.account.id = :accountId AND t.direction = :debit " +
                                    "AND t.valueDate >= :fromDate AND t.valueDate <= :toDate", Object[].class)
                    .setParameter("accountId", accountId)
                    .setParameter("debit", TransactionDirection.DEBIT)
                    .setParameter("fromDate", fromDate)
                    .setParameter("toDate", toDate)
                    .getResultList();

            Map<String, BigDecimal> spendingMap = new LinkedHashMap<>();

            for (Object[] row : results) {
                UUID transactionId = (UUID) row[0];
                BigDecimal amount = (BigDecimal) row[1];

                List<String> tagNames = getTagNamesForTransaction(transactionId);

                if (tagNames.isEmpty()) {
                    spendingMap.merge("Uncategorized", amount, BigDecimal::add);
                } else {
                    // Prendi il primo tag come categoria principale
                    String categoryName = tagNames.get(0);
                    spendingMap.merge(categoryName, amount, BigDecimal::add);
                }
            }

            return spendingMap;

        } catch (Exception e) {
            System.err.println("Error calculating spending by category: " + e.getMessage());
            return new LinkedHashMap<>();
        }
    }


    /** {@inheritDoc} */
    @Override
    public List<T> findRecentTransactions(UUID accountId, int limit) {
        Objects.requireNonNull(accountId, "accountId must not be null");

        return createQuery("SELECT t FROM " + getEntityName() + " t " +
                "WHERE t.account.id = :accountId ORDER BY t.valueDate DESC")
                .setParameter("accountId", accountId)
                .setMaxResults(limit)
                .getResultList();
    }

    // Private helper methods

    private List<String> getTagNamesForTransaction(UUID transactionId) {
        try {
            T transaction = em.find(entityType, transactionId);
            if (transaction == null || transaction.getTags().isEmpty()) {
                return List.of();
            }

            List<String> tagNames = new ArrayList<>();
            for (UUID tagId : transaction.getTags()) {
                try {
                    String tagName = em.createQuery(
                                    "SELECT t.name FROM " + getTagEntityName() + " t WHERE t.id = :tagId", String.class)
                            .setParameter("tagId", tagId)
                            .getSingleResult();
                    tagNames.add(tagName);
                } catch (Exception ignored) {
                }
            }
            return tagNames;

        } catch (Exception e) {
            return List.of();
        }
    }

    private void addFilterConditions(StringBuilder jpql, TransactionFilterDTO filter) {
        if (filter.direction() != null)
            jpql.append(" AND t.direction = :direction");
        if (filter.fromDate() != null)
            jpql.append(" AND t.valueDate >= :fromDate");
        if (filter.toDate() != null)
            jpql.append(" AND t.valueDate <= :toDate");
        if (filter.description() != null && !filter.description().trim().isEmpty())
            jpql.append(" AND LOWER(t.description) LIKE LOWER(:description)");
        if (filter.tagIds() != null && !filter.tagIds().isEmpty())
            jpql.append(" AND EXISTS (SELECT 1 FROM t.tags tag WHERE tag.id IN :tagIds)");
    }

    private void setFilterParameters(TypedQuery<?> query, TransactionFilterDTO filter) {
        if (filter.direction() != null)
            query.setParameter("direction", filter.direction());
        if (filter.fromDate() != null)
            query.setParameter("fromDate", filter.fromDate());
        if (filter.toDate() != null)
            query.setParameter("toDate", filter.toDate());
        if (filter.description() != null && !filter.description().trim().isEmpty())
            query.setParameter("description", "%" + filter.description().trim() + "%");
        if (filter.tagIds() != null && !filter.tagIds().isEmpty())
            query.setParameter("tagIds", filter.tagIds());
    }


    private TypedQuery<T> createQuery(String jpql) {
        return em.createQuery(jpql, entityType);
    }

    private String getEntityName() {
        return entityType.getSimpleName();
    }

    private void requireNonNulls(Object... objs) {
        for (Object obj : objs)
            Objects.requireNonNull(obj, "Parameter must not be null");
    }

    private void validateDateRange(OffsetDateTime startDate, OffsetDateTime endDate) {
        if (startDate.isAfter(endDate))
            throw new IllegalArgumentException("startDate must be <= endDate");
    }

    private void validateTagIds(Set<UUID> tagIds) {
        if (tagIds.isEmpty())
            throw new IllegalArgumentException("tagIds must not be empty");
    }

    private T getManagedEntity(T entity) {
        if (em.contains(entity))
            return entity;
        return em.find(entityType, entity.getId());
    }

    private String getTagEntityName() {
        return "NormalTag";
    }

    private Set<UUID> collectDescendantTagIds(UUID rootTagId) {
        Set<UUID> result = new HashSet<>();
        Deque<UUID> toProcess = new ArrayDeque<>();
        toProcess.push(rootTagId);

        while (!toProcess.isEmpty()) {
            UUID current = toProcess.pop();
            if (result.add(current)) {
                List<UUID> children = em.createQuery(
                                "SELECT t.id FROM " + getTagEntityName() + " t WHERE t.parentId = :parentId", UUID.class)
                        .setParameter("parentId", current)
                        .getResultList();
                children.stream()
                        .filter(child -> !result.contains(child))
                        .forEach(toProcess::push);
            }
        }
        return result;
    }
}