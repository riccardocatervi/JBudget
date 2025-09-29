package it.unicam.cs.mpgc.jbudget126139.controller.impl;

import it.unicam.cs.mpgc.jbudget126139.controller.TagController;
import it.unicam.cs.mpgc.jbudget126139.service.TagService;
import it.unicam.cs.mpgc.jbudget126139.service.dto.TagDTO;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Implementation of {@link TagController} that delegates category/tag
 * operations to a {@link TagService}.
 * <p>
 * Provides methods to create, update, delete and list tags, including
 * support for hierarchical parent-child relationships.
 * </p>
 */
public class TagControllerImpl implements TagController {

    private final TagService tagService;

    /**
     * Creates a new {@code TagControllerImpl}.
     *
     * @param tagService the service handling tag operations (must not be null)
     */
    public TagControllerImpl(TagService tagService) {
        this.tagService = Objects.requireNonNull(tagService);
    }

    /**
     * Creates a new root tag (category).
     *
     * @param name        the tag name
     * @param description the optional description
     * @return the created {@link TagDTO}
     */
    @Override
    public TagDTO createTag(String name, String description) {
        return tagService.createTag(name, description, null);
    }

    /**
     * Creates a new child tag under an existing parent.
     *
     * @param name        the tag name
     * @param description the optional description
     * @param parentId    the parent tag ID (must not be null)
     * @return the created {@link TagDTO}
     * @throws NullPointerException if {@code parentId} is null
     */
    @Override
    public TagDTO createChildTag(String name, String description, UUID parentId) {
        Objects.requireNonNull(parentId, "parentId must not be null");
        return tagService.createTag(name, description, parentId);
    }

    /**
     * Retrieves a tag by its ID.
     *
     * @param tagId the tag ID
     * @return the {@link TagDTO}
     * @throws NullPointerException if {@code tagId} is null
     */
    @Override
    public TagDTO getTag(UUID tagId) {
        Objects.requireNonNull(tagId, "tagId must not be null");
        return tagService.getTag(tagId);
    }

    /**
     * Updates an existing tag.
     *
     * @param tagId       the tag ID
     * @param name        the new name
     * @param description the new description (optional)
     * @return the updated {@link TagDTO}
     * @throws NullPointerException if {@code tagId} is null
     */
    @Override
    public TagDTO updateTag(UUID tagId, String name, String description) {
        Objects.requireNonNull(tagId, "tagId must not be null");
        return tagService.updateTag(tagId, name, description);
    }

    /**
     * Deletes a tag by its ID.
     *
     * @param tagId the tag ID
     * @throws NullPointerException if {@code tagId} is null
     */
    @Override
    public void deleteTag(UUID tagId) {
        Objects.requireNonNull(tagId, "tagId must not be null");
        tagService.deleteTag(tagId);
    }

    /**
     * Lists all root-level tags (tags without a parent).
     *
     * @return a list of {@link TagDTO}
     */
    @Override
    public List<TagDTO> listRootTags() {
        return tagService.listRootTags();
    }

    /**
     * Lists all child tags under a given parent.
     *
     * @param parentId the parent tag ID
     * @return a list of {@link TagDTO}
     * @throws NullPointerException if {@code parentId} is null
     */
    @Override
    public List<TagDTO> listChildTags(UUID parentId) {
        Objects.requireNonNull(parentId, "parentId must not be null");
        return tagService.listChildTags(parentId);
    }

    /**
     * Lists all tags in the system, including root and child tags.
     *
     * @return a list of {@link TagDTO}
     */
    @Override
    public List<TagDTO> listAllTags() {
        return tagService.listTags();  // Usa il metodo esistente del service
    }
}