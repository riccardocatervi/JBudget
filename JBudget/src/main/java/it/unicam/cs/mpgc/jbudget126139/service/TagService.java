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

package it.unicam.cs.mpgc.jbudget126139.service;

import it.unicam.cs.mpgc.jbudget126139.service.dto.TagDTO;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for managing tags.
 * <p>
 * Provides methods for creating, retrieving, updating, deleting,
 * and listing tags, including hierarchical tag structures.
 * </p>
 */
public interface TagService {

    /**
     * Creates a new tag.
     *
     * @param name        the tag name; must not be {@code null} or blank
     * @param description an optional description; may be {@code null}
     * @param parentId    the identifier of the parent tag; {@code null} if this is a root tag
     * @return the created {@link TagDTO}
     */
    TagDTO createTag(String name, String description, UUID parentId);

    /**
     * Retrieves a tag by its unique identifier.
     *
     * @param tagId the tag identifier; must not be {@code null}
     * @return the matching {@link TagDTO}
     * @throws java.util.NoSuchElementException if no tag exists with the given ID
     */
    TagDTO getTag(UUID tagId);

    /**
     * Updates an existing tag.
     *
     * @param tagId       the tag identifier; must not be {@code null}
     * @param name        the new name of the tag; must not be {@code null} or blank
     * @param description the new description; may be {@code null}
     * @return the updated {@link TagDTO}
     */
    TagDTO updateTag(UUID tagId, String name, String description);

    /**
     * Deletes a tag by its unique identifier.
     *
     * @param tagId the tag identifier; must not be {@code null}
     */
    void deleteTag(UUID tagId);

    /**
     * Retrieves all tags.
     *
     * @return a list of {@link TagDTO}; never {@code null}, may be empty
     */
    List<TagDTO> listTags();

    /**
     * Retrieves the child tags of a given parent tag.
     *
     * @param parentId the identifier of the parent tag; must not be {@code null}
     * @return a list of {@link TagDTO}; never {@code null}, may be empty
     */
    List<TagDTO> listChildTags(UUID parentId);

    /**
     * Retrieves all root tags (tags without a parent).
     *
     * @return a list of {@link TagDTO}; never {@code null}, may be empty
     */
    List<TagDTO> listRootTags();
}