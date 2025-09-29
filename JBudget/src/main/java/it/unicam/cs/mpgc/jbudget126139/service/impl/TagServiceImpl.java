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

import it.unicam.cs.mpgc.jbudget126139.model.NormalTag;
import it.unicam.cs.mpgc.jbudget126139.persistence.TagRepository;
import it.unicam.cs.mpgc.jbudget126139.service.TagService;
import it.unicam.cs.mpgc.jbudget126139.service.dto.TagDTO;
import it.unicam.cs.mpgc.jbudget126139.service.mapper.TagMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

/**
 * Default implementation of {@link TagService}.
 * <p>
 * Manages tag creation, retrieval, updates, deletion, and hierarchical queries.
 * </p>
 */
public class TagServiceImpl implements TagService {

    private final EntityManagerFactory emf;
    private final Function<EntityManager, TagRepository<NormalTag>> tagRepositoryProvider;

    /**
     * Creates a new {@code TagServiceImpl} instance.
     *
     * @param emf                   the {@link EntityManagerFactory}; must not be {@code null}
     * @param tagRepositoryProvider a function to create a {@link TagRepository}; must not be {@code null}
     */
    public TagServiceImpl(EntityManagerFactory emf,
                          Function<EntityManager, TagRepository<NormalTag>> tagRepositoryProvider) {
        this.emf = Objects.requireNonNull(emf, "EntityManagerFactory must not be null");
        this.tagRepositoryProvider = Objects.requireNonNull(tagRepositoryProvider,
                "Tag repository provider must not be null");
    }

    /** {@inheritDoc} */
    @Override
    public TagDTO createTag(String name, String description, UUID parentId) {
        requireNonNulls(name);
        validateName(name);
        return executeInTransaction(em -> {
            var repo = tagRepositoryProvider.apply(em);
            if (parentId != null)
                validateParentExists(repo, parentId);
            NormalTag tag = new NormalTag(name, description, parentId);
            repo.save(tag);
            return TagMapper.INSTANCE.toDto(tag);
        });
    }

    /** {@inheritDoc} */
    @Override
    public TagDTO getTag(UUID tagId) {
        requireNonNulls(tagId);
        return executeReadOnly(em -> {
            var repo = tagRepositoryProvider.apply(em);
            return repo.findById(tagId)
                    .map(TagMapper.INSTANCE::toDto)
                    .orElseThrow(() -> new EntityNotFoundException(
                            String.format("Tag with id %s not found", tagId)));
        });
    }

    /** {@inheritDoc} */
    @Override
    public TagDTO updateTag(UUID tagId, String name, String description) {
        requireNonNulls(tagId, name);
        validateName(name);
        return executeInTransaction(em -> {
            var repo = tagRepositoryProvider.apply(em);
            var tag = repo.findById(tagId)
                    .orElseThrow(() -> new EntityNotFoundException(
                            String.format("Tag with id %s not found", tagId)));
            tag.updateDetails(name, description);
            return TagMapper.INSTANCE.toDto(tag);
        });
    }

    /** {@inheritDoc} */
    @Override
    public void deleteTag(UUID tagId) {
        requireNonNulls(tagId);
        executeInTransaction(em -> {
            var repo = tagRepositoryProvider.apply(em);
            var tag = repo.findById(tagId)
                    .orElseThrow(() -> new EntityNotFoundException(
                            String.format("Tag with id %s not found", tagId)));
            validateTagCanBeDeleted(repo, tagId);
            repo.delete(tag);
            return null;
        });
    }

    /** {@inheritDoc} */
    @Override
    public List<TagDTO> listTags() {
        return executeReadOnly(em -> {
            var repo = tagRepositoryProvider.apply(em);
            List<NormalTag> tags = repo.findAllOrderByNameAsc();
            return mapToTagDTOs(tags);
        });
    }

    /** {@inheritDoc} */
    @Override
    public List<TagDTO> listChildTags(UUID parentId) {
        requireNonNulls(parentId);
        return executeReadOnly(em -> {
            var repo = tagRepositoryProvider.apply(em);
            List<NormalTag> tags = repo.findByParentIdOrderByNameAsc(parentId);
            return mapToTagDTOs(tags);
        });
    }

    /** {@inheritDoc} */
    @Override
    public List<TagDTO> listRootTags() {
        return executeReadOnly(em -> {
            var repo = tagRepositoryProvider.apply(em);
            List<NormalTag> tags = repo.findRootTagsOrderByNameAsc();
            return mapToTagDTOs(tags);
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

    private void validateParentExists(TagRepository<NormalTag> repo, UUID parentId) {
        if (repo.findById(parentId).isEmpty())
            throw new EntityNotFoundException(
                    String.format("Parent tag with id %s not found", parentId));
    }

    private void validateTagCanBeDeleted(TagRepository<NormalTag> repo, UUID tagId) {
        List<NormalTag> children = repo.findByParentIdOrderByNameAsc(tagId);
        if (!children.isEmpty())
            throw new IllegalStateException(
                    String.format("Cannot delete tag %s: it has %d child tags", tagId, children.size()));
    }

    private List<TagDTO> mapToTagDTOs(List<NormalTag> tags) {
        return tags.stream()
                .map(TagMapper.INSTANCE::toDto)
                .toList();
    }

    private void validateName(String name) {
        if (name.trim().isEmpty())
            throw new IllegalArgumentException("Tag name must not be empty");
    }

    private void requireNonNulls(Object... objects) {
        for (Object obj : objects)
            Objects.requireNonNull(obj, "Parameter must not be null");
    }
}