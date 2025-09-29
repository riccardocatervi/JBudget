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

package it.unicam.cs.mpgc.jbudget126139.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Base class for all {@link Tag} implementations, providing common persistence mappings
 * and validation constraints for tag entities.
 * <p>
 * This abstract class manages the unique identifier, creation timestamp, name,
 * description, and optional parent tag reference.
 * </p>
 *
 * <p>
 * Mapped as a JPA {@link MappedSuperclass}, so its fields are inherited by subclasses
 * but it is not itself a database table.
 * </p>
 */
@MappedSuperclass
@Access(AccessType.FIELD)
public abstract sealed class AbstractTag implements Tag permits NormalTag {

    /**
     * Unique identifier of the tag, generated automatically using {@link GenerationType#UUID}.
     * <p>
     * This value is immutable after creation.
     * </p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /**
     * Timestamp indicating when the tag was created.
     * Automatically set by the persistence provider.
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private OffsetDateTime createdAt;

    /**
     * Name of the tag.
     * <p>
     * Must not be blank and must have a maximum length of 64 characters.
     * </p>
     */
    @NotBlank
    @Size(max = 64)
    @Column(name = "name", nullable = false, length = 64)
    private String name;

    /**
     * Optional description of the tag.
     * <p>
     * Can be {@code null} and must have a maximum length of 512 characters if present.
     * </p>
     */
    @Size(max = 512)
    @Column(name = "description", length = 512)
    private String description;

    /**
     * Optional reference to the parent tag's identifier.
     * <p>
     * May be {@code null}. Cannot be equal to this tag's own {@link #id}.
     * </p>
     */
    @Column(name = "parent_id")
    private UUID parentId;

    /**
     * Protected no-args constructor for JPA.
     */
    protected AbstractTag() {
    }

    /**
     * Constructs a new tag with the given name, description, and optional parent ID.
     *
     * @param name        the name of the tag; must not be blank.
     * @param description the description of the tag; may be {@code null}.
     * @param parentId    the identifier of the parent tag; may be {@code null}.
     * @throws IllegalArgumentException if {@code name} is blank or {@code parentId} equals this tag's ID.
     */
    public AbstractTag(String name, String description, UUID parentId) {
        setName(name);
        setDescription(description);
        setParentId(parentId);
    }

    /** {@inheritDoc} */
    @Override
    public UUID getId() {
        return id;
    }

    /** {@inheritDoc} */
    @Override
    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    /** {@inheritDoc} */
    @Override
    public String getName() {
        return name;
    }

    /** {@inheritDoc} */
    @Override
    public String getDescription() {
        return description;
    }

    /** {@inheritDoc} */
    @Override
    public UUID getParentId() {
        return parentId;
    }

    /**
     * Updates the tag's name.
     *
     * @param name the new name; must not be {@code null} or blank.
     * @throws IllegalArgumentException if {@code name} is blank.
     */
    public void setName(String name) {
        this.name = Objects.requireNonNull(name).trim();
        if (this.name.isEmpty()) throw new IllegalArgumentException("name cannot be blank");
    }

    /**
     * Updates the tag's description.
     *
     * @param description the new description; may be {@code null}.
     *                    If not {@code null}, it will be trimmed.
     */
    public void setDescription(String description) {
        this.description = (description == null) ? null : description.trim();
    }

    /**
     * Updates the tag's parent identifier.
     *
     * @param parentId the new parent tag ID; may be {@code null}.
     * @throws IllegalArgumentException if {@code parentId} equals this tag's own {@link #id}.
     */
    public void setParentId(UUID parentId) {
        if (parentId != null && parentId.equals(this.id))
            throw new IllegalArgumentException("A tag cannot be its own parent");
        this.parentId = parentId;
    }

    /**
     * Updates both the name and the description of the tag.
     *
     * @param name        the new name; must not be blank.
     * @param description the new description; may be {@code null}.
     * @throws IllegalArgumentException if {@code name} is blank.
     */
    public void updateDetails(String name, String description) {
        setName(name);
        setDescription(description);
    }
}