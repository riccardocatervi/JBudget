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

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.UUID;

/**
 * Concrete implementation of a {@link Tag} based on the {@link AbstractTag} superclass.
 * <p>
 * Represents a standard, user-defined tag for categorizing transactions,
 * with an optional parent tag to allow hierarchical organization.
 * </p>
 *
 * <p>
 * Instances are persisted in the {@code tags} table.
 * </p>
 */
@Entity
@Table(name = "tags")
@Access(AccessType.FIELD)
public final class NormalTag extends AbstractTag {

    /**
     * Protected no-args constructor for JPA.
     */
    public NormalTag() {
        super();
    }

    /**
     * Creates a new tag with the given details.
     *
     * @param name        the tag name; must not be blank.
     * @param description the optional description; may be {@code null}.
     * @param parentId    the optional identifier of the parent tag; may be {@code null}.
     * @throws IllegalArgumentException if {@code name} is blank or {@code parentId} equals this tag's own ID.
     */
    public NormalTag(String name, String description, UUID parentId) {
        super(name, description, parentId);
    }
}