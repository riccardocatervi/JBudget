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

import it.unicam.cs.mpgc.jbudget126139.model.AbstractAccount;
import it.unicam.cs.mpgc.jbudget126139.persistence.AccountRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * JPA-based implementation of {@link AccountRepository} for managing {@link AbstractAccount} entities.
 * <p>
 * Provides CRUD operations and custom queries using an injected {@link EntityManager}.
 * </p>
 *
 * @param <A> the concrete subtype of {@link AbstractAccount} handled by this repository
 */
public class AccountRepositoryImpl<A extends AbstractAccount> implements AccountRepository<A> {

    /** The JPA {@link EntityManager} used for persistence operations. */
    private final EntityManager em;

    /** The concrete account entity type managed by this repository. */
    private final Class<A> entityType;

    /**
     * Creates a new repository instance for the given account entity type.
     *
     * @param em         the {@link EntityManager}; must not be {@code null}.
     * @param entityType the concrete account entity type; must not be {@code null}.
     * @throws NullPointerException if {@code em} or {@code entityType} is {@code null}.
     */
    public AccountRepositoryImpl(EntityManager em, Class<A> entityType) {
        this.em = Objects.requireNonNull(em, "EntityManager must be not null");
        this.entityType = Objects.requireNonNull(entityType, "entityType must be not null");
    }

    /** {@inheritDoc} */
    @Override
    public List<A> findAllOrderByNameAsc() {
        return em.createQuery("SELECT a FROM " + getEntityName() + " a ORDER BY a.name", entityType)
                .getResultList();
    }


    /** {@inheritDoc} */
    @Override
    public void save(A entity) {
        Objects.requireNonNull(entity, "entity must not be null");
        if (entity.getId() == null) {
            em.persist(entity);
        } else {
            em.merge(entity);
        }
    }

    /** {@inheritDoc} */
    @Override
    public Optional<A> findById(UUID uuid) {
        Objects.requireNonNull(uuid, "uuid must not be null");
        try {
            A account = createQuery("SELECT a FROM " + getEntityName() + " a WHERE a.id = :uuid")
                    .setParameter("uuid", uuid)
                    .getSingleResult();
            return Optional.of(account);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void delete(A entity) {
        Objects.requireNonNull(entity);
        A managed = getManagedEntity(entity);
        if (managed != null) {
            em.remove(managed);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void deleteById(UUID uuid) {
        Objects.requireNonNull(uuid);
        A entity = em.find(entityType, uuid);
        if (entity != null) {
            em.remove(entity);
        }
    }

    // Private helper methods
    private String getEntityName() {
        return entityType.getSimpleName();
    }

    private TypedQuery<A> createQuery(String jpql) {
        return em.createQuery(jpql, entityType);
    }

    private A getManagedEntity(A entity) {
        if (em.contains(entity)) {
            return entity;
        }
        return em.find(entityType, entity.getId());
    }
}