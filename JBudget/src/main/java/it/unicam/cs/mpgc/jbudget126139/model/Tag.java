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

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Represents a user-defined label used to categorize transactions.
 * <p>
 * Tags may optionally be hierarchical, meaning a tag can have a parent tag.
 * </p>
 */
public interface Tag extends Identified<UUID> {

    /**
     * Returns the timestamp indicating when this tag was created.
     *
     * @return the creation date and time of the tag; never {@code null}.
     */
    OffsetDateTime getCreatedAt();

    /**
     * Returns the name of the tag.
     *
     * @return the tag name; never {@code null} or blank.
     */
    String getName();

    /**
     * Returns the description of the tag, if any.
     *
     * @return the tag description, or {@code null} if none is set.
     */
    String getDescription();

    /**
     * Returns the identifier of the parent tag, if any.
     *
     * @return the UUID of the parent tag, or {@code null} if this tag has no parent.
     */
    UUID getParentId();
}