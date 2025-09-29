package it.unicam.cs.mpgc.jbudget126139.controller;

import it.unicam.cs.mpgc.jbudget126139.service.dto.TagDTO;

import java.util.List;
import java.util.UUID;

/**
 * Controller interface for managing categories/tags.
 * <p>
 * Defines operations to create, retrieve, update, delete,
 * and list hierarchical tags used to classify transactions.
 * </p>
 */
public interface TagController {

    /**
     * Creates a new root-level tag (category).
     *
     * @param name        the tag name (must not be {@code null} or empty)
     * @param description an optional description, may be {@code null}
     * @return the created {@link TagDTO}
     */
    TagDTO createTag(String name, String description);

    /**
     * Creates a new child tag (subcategory) under a given parent.
     *
     * @param name        the tag name
     * @param description an optional description
     * @param parentId    the ID of the parent tag
     * @return the created {@link TagDTO}
     */
    TagDTO createChildTag(String name, String description, UUID parentId);

    /**
     * Retrieves a tag by its unique identifier.
     *
     * @param tagId the ID of the tag to retrieve
     * @return the {@link TagDTO}, or {@code null} if not found
     */
    TagDTO getTag(UUID tagId);

    /**
     * Updates an existing tag.
     *
     * @param tagId       the ID of the tag to update
     * @param name        the new name of the tag
     * @param description the new description, may be {@code null}
     * @return the updated {@link TagDTO}
     */
    TagDTO updateTag(UUID tagId, String name, String description);

    /**
     * Deletes a tag by its unique identifier.
     *
     * @param tagId the ID of the tag to delete
     */
    void deleteTag(UUID tagId);

    /**
     * Lists all root-level tags (categories without a parent).
     *
     * @return a list of {@link TagDTO} at the root level
     */
    List<TagDTO> listRootTags();

    /**
     * Lists all child tags (subcategories) of a given parent.
     *
     * @param parentId the ID of the parent tag
     * @return a list of {@link TagDTO} under the given parent
     */
    List<TagDTO> listChildTags(UUID parentId);

    /**
     * Lists all tags (both root and children).
     *
     * @return a list of all {@link TagDTO} objects
     */
    List<TagDTO> listAllTags();
}