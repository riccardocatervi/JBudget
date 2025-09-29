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

import it.unicam.cs.mpgc.jbudget126139.model.Tag;
import it.unicam.cs.mpgc.jbudget126139.persistence.TagRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * JPA-based implementation of {@link TagRepository} for managing {@link Tag} entities.
 * <p>
 * Provides CRUD operations and tag-specific queries using an injected {@link EntityManager}.
 * </p>
 *
 * @param <T> the concrete subtype of {@link Tag} handled by this repository
 */
public class TagRepositoryImpl<T extends Tag> implements TagRepository<T> {

    /** The JPA {@link EntityManager} used for persistence operations. */
    private final EntityManager em;

    /** The concrete tag entity type managed by this repository. */
    private final Class<T> entityType;

    /**
     * Creates a new repository instance for the given tag entity type.
     *
     * @param em         the {@link EntityManager}; must not be {@code null}.
     * @param entityType the concrete tag entity type; must not be {@code null}.
     * @throws NullPointerException if {@code em} or {@code entityType} is {@code null}.
     */
    public TagRepositoryImpl(EntityManager em, Class<T> entityType) {
        this.em = Objects.requireNonNull(em, "EntityManager must not be null");
        this.entityType = Objects.requireNonNull(entityType, "entityType must not be null");
    }

    /** {@inheritDoc} */
    @Override
    public List<T> findAllOrderByNameAsc() {
        return em.createQuery("SELECT t FROM " + getEntityName() + " t ORDER BY t.name ASC", entityType)
                .getResultList();
    }

    /** {@inheritDoc} */
    @Override
    public List<T> findByParentIdOrderByNameAsc(UUID parentId) {
        Objects.requireNonNull(parentId, "parentId must not be null");
        return createQuery(
                "SELECT t FROM " + getEntityName() + " t " +
                        "WHERE t.parentId = :parentId " +
                        "ORDER BY t.name ASC")
                .setParameter("parentId", parentId)
                .getResultList();
    }

    /** {@inheritDoc} */
    @Override
    public List<T> findRootTagsOrderByNameAsc() {
        return createQuery(
                "SELECT t FROM " + getEntityName() + " t " +
                        "WHERE t.parentId IS NULL " +
                        "ORDER BY t.name ASC")
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

    // Private helper methods
    private String getEntityName() {
        return entityType.getSimpleName();
    }

    private TypedQuery<T> createQuery(String jpql) {
        return em.createQuery(jpql, entityType);
    }

    private T getManagedEntity(T entity) {
        if (em.contains(entity))
            return entity;
        return em.find(entityType, entity.getId());
    }
}