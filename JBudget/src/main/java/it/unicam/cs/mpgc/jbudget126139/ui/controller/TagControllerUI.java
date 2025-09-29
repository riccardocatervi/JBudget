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

package it.unicam.cs.mpgc.jbudget126139.ui.controller;

import it.unicam.cs.mpgc.jbudget126139.controller.TagController;
import it.unicam.cs.mpgc.jbudget126139.service.dto.TagDTO;
import it.unicam.cs.mpgc.jbudget126139.ui.event.DataRefreshEvent;
import it.unicam.cs.mpgc.jbudget126139.ui.event.UIEventBus;
import it.unicam.cs.mpgc.jbudget126139.ui.service.DialogService;
import it.unicam.cs.mpgc.jbudget126139.ui.view.TagsView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * UI controller responsible for managing categories (tags).
 * <p>
 * Handles CRUD operations for tags, interacts with the {@link TagController},
 * and updates the {@link TagsView}. Also integrates with {@link DialogService}
 * for user interaction.
 * </p>
 */
public class TagControllerUI implements UIController {

    private final TagsView view;
    private final TagController tagController;
    private final DialogService dialogService;
    private final UIEventBus eventBus;

    /**
     * Creates a new {@code TagControllerUI}.
     *
     * @param view          the view for displaying tags
     * @param tagController the controller for tag operations
     * @param dialogService the dialog service for user prompts
     * @param eventBus      the UI event bus
     */
    public TagControllerUI(TagsView view,
                           TagController tagController,
                           DialogService dialogService,
                           UIEventBus eventBus) {
        this.view = view;
        this.tagController = tagController;
        this.dialogService = dialogService;
        this.eventBus = eventBus;
    }

    /**
     * Initializes the controller and loads the tag hierarchy.
     */
    @Override
    public void initialize() {
        loadTags();
    }

    /**
     * Cleans up controller resources if needed.
     */
    @Override
    public void cleanup() {
    }

    /**
     * Returns the associated view.
     *
     * @return the {@link TagsView}
     */
    public TagsView getView() {
        return view;
    }

    private void loadTags() {
        try {
            view.showLoading();
            List<TagDTO> rootTags = tagController.listRootTags();

            List<TagDTO> allTags = new ArrayList<>(rootTags);
            for (TagDTO rootTag : List.copyOf(rootTags)) {
                addChildTagsRecursively(rootTag, allTags);
            }

            view.displayTags(allTags);
            view.hideLoading();
        } catch (Exception e) {
            view.showError("Failed to load categories: " + e.getMessage());
            view.hideLoading();
        }
    }

    /**
     * Returns the child tags of a given parent tag.
     *
     * @param parentId the parent tag ID
     * @return the list of child tags, or an empty list on error
     */
    public List<TagDTO> getChildTags(UUID parentId) {
        try {
            return tagController.listChildTags(parentId);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private void addChildTagsRecursively(TagDTO parentTag, List<TagDTO> allTags) {
        try {
            List<TagDTO> children = tagController.listChildTags(parentTag.id());
            for (TagDTO child : children) {
                if (allTags.stream().noneMatch(tag -> tag.id().equals(child.id()))) {
                    allTags.add(child);
                    addChildTagsRecursively(child, allTags);
                }
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * Opens the dialog to add a new tag.
     *
     * @param parent the parent tag (can be {@code null} for a root tag)
     */
    public void showAddTagDialog(TagDTO parent) {
        dialogService.showAddTagDialog(
                parent,
                tag -> {
                    loadTags();
                    dialogService.showSuccessAlert("Category created successfully!");
                },
                error -> view.showError(error)
        );
    }

    /**
     * Opens the dialog to edit an existing tag.
     *
     * @param tag the tag to edit
     */
    public void showEditTagDialog(TagDTO tag) {
        dialogService.showEditTagDialog(
                tag,
                updatedTag -> {
                    loadTags();
                    dialogService.showSuccessAlert("Category \"" + updatedTag.name() + "\" updated successfully!");
                },
                error -> view.showError(error)
        );
    }

    /**
     * Confirms and deletes the given tag.
     *
     * @param tag the tag to delete
     */
    public void showDeleteTagDialog(TagDTO tag) {
        boolean confirmed = dialogService.showConfirmationDialog(
                "Delete Category",
                "Delete Category?",
                "Are you sure you want to delete \"" + tag.name() + "\"? This action cannot be undone."
        );

        if (confirmed) {
            try {
                tagController.deleteTag(tag.id());
                loadTags();
                eventBus.publish(new DataRefreshEvent(DataRefreshEvent.DataType.TRANSACTIONS, null));
                dialogService.showSuccessAlert("Category deleted successfully!");
            } catch (Exception e) {
                view.showError("Failed to delete category: " + e.getMessage());
            }
        }
    }
}