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

import it.unicam.cs.mpgc.jbudget126139.model.AbstractAccount;
import it.unicam.cs.mpgc.jbudget126139.model.NormalAccount;
import it.unicam.cs.mpgc.jbudget126139.model.NormalRecurrence;
import it.unicam.cs.mpgc.jbudget126139.model.RecurrenceFrequency;
import it.unicam.cs.mpgc.jbudget126139.persistence.RecurrenceRepository;
import it.unicam.cs.mpgc.jbudget126139.service.RecurrenceService;
import it.unicam.cs.mpgc.jbudget126139.service.dto.RecurrenceDTO;
import it.unicam.cs.mpgc.jbudget126139.service.mapper.RecurrenceMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

/**
 * Default implementation of {@link RecurrenceService}.
 * <p>
 * Handles creation, retrieval, deletion, and listing of recurring transactions.
 * </p>
 */
public class RecurrenceServiceImpl implements RecurrenceService {

    private final EntityManagerFactory emf;
    private final Function<EntityManager, RecurrenceRepository<NormalRecurrence>> recurrenceRepositoryProvider;

    /**
     * Creates a new {@code RecurrenceServiceImpl} instance.
     *
     * @param emf                          the {@link EntityManagerFactory}; must not be {@code null}
     * @param recurrenceRepositoryProvider a function to create a {@link RecurrenceRepository}; must not be {@code null}
     */
    public RecurrenceServiceImpl(EntityManagerFactory emf,
                                 Function<EntityManager, RecurrenceRepository<NormalRecurrence>> recurrenceRepositoryProvider) {
        this.emf = Objects.requireNonNull(emf, "EntityManagerFactory must not be null");
        this.recurrenceRepositoryProvider = Objects.requireNonNull(recurrenceRepositoryProvider,
                "Recurrence repository provider must not be null");
    }

    /** {@inheritDoc} */
    @Override
    public RecurrenceDTO createRecurrence(UUID accountId,
                                          OffsetDateTime startDate,
                                          OffsetDateTime endDate,
                                          RecurrenceFrequency frequency) {
        requireNonNulls(accountId, startDate, frequency);
        validateDateRange(startDate, endDate);
        return executeInTransaction(em -> {
            var repo = recurrenceRepositoryProvider.apply(em);
            AbstractAccount account = em.getReference(NormalAccount.class, accountId);
            NormalRecurrence recurrence = new NormalRecurrence(account, startDate, endDate, frequency);
            repo.save(recurrence);
            return mapToRecurrenceDTO(recurrence);
        });
    }

    /** {@inheritDoc} */
    @Override
    public RecurrenceDTO getRecurrence(UUID recurrenceId) {
        requireNonNulls(recurrenceId);
        return executeReadOnly(em -> {
            var repo = recurrenceRepositoryProvider.apply(em);
            return repo.findById(recurrenceId)
                    .map(this::mapToRecurrenceDTO)
                    .orElseThrow(() -> new EntityNotFoundException(
                            String.format("Recurrence with id %s not found", recurrenceId)));
        });
    }

    /** {@inheritDoc} */
    @Override
    public void deleteRecurrence(UUID recurrenceId) {
        requireNonNulls(recurrenceId);
        executeInTransaction(em -> {
            var repo = recurrenceRepositoryProvider.apply(em);
            var recurrence = repo.findById(recurrenceId)
                    .orElseThrow(() -> new EntityNotFoundException(
                            String.format("Recurrence with id %s not found", recurrenceId)));
            repo.delete(recurrence);
            return null;
        });
    }

    /** {@inheritDoc} */
    @Override
    public List<RecurrenceDTO> listRecurrencesByAccount(UUID accountId) {
        requireNonNulls(accountId);
        return executeReadOnly(em -> {
            var repo = recurrenceRepositoryProvider.apply(em);
            List<NormalRecurrence> recurrences = repo.findByAccountOrderByStartDateDesc(accountId);
            return mapToRecurrenceDTOs(recurrences);
        });
    }

    /** {@inheritDoc} */
    @Override
    public List<RecurrenceDTO> listActiveRecurrences(OffsetDateTime asOf) {
        requireNonNulls(asOf);
        return executeReadOnly(em -> {
            var repo = recurrenceRepositoryProvider.apply(em);
            List<NormalRecurrence> recurrences = repo.findActiveRecurrences(asOf);
            return mapToRecurrenceDTOs(recurrences);
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

    private void validateDateRange(OffsetDateTime startDate, OffsetDateTime endDate) {
        if (endDate != null && startDate.isAfter(endDate))
            throw new IllegalArgumentException("startDate must be <= endDate");
    }

    private RecurrenceDTO mapToRecurrenceDTO(NormalRecurrence recurrence) {
        RecurrenceDTO dto = RecurrenceMapper.INSTANCE.toDto(recurrence);
        return new RecurrenceDTO(
                dto.id(),
                dto.createdAt(),
                recurrence.getAccount() != null ? recurrence.getAccount().getId() : null,
                dto.startDate(),
                dto.endDate(),
                dto.frequency()
        );
    }

    private List<RecurrenceDTO> mapToRecurrenceDTOs(List<NormalRecurrence> recurrences) {
        return recurrences.stream()
                .map(this::mapToRecurrenceDTO)
                .toList();
    }

    private void requireNonNulls(Object... objects) {
        for (Object obj : objects)
            Objects.requireNonNull(obj, "Parameter must not be null");
    }
}