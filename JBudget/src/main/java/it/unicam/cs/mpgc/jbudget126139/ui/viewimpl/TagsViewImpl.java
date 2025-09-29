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

package it.unicam.cs.mpgc.jbudget126139.ui.viewimpl;

import it.unicam.cs.mpgc.jbudget126139.service.dto.TagDTO;
import it.unicam.cs.mpgc.jbudget126139.ui.controller.TagControllerUI;
import it.unicam.cs.mpgc.jbudget126139.ui.view.TagsView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;

import java.util.List;
import java.util.function.Consumer;

/**
 * Implementation of the {@link TagsView} interface for displaying and managing categories (tags).
 * <p>
 * Provides a hierarchical view of categories, supporting nesting of child categories.
 * Includes action buttons for adding, editing, and deleting categories at any hierarchy level.
 * </p>
 */
public class TagsViewImpl extends BaseViewImpl implements TagsView {

    private VBox root;
    private VBox hierarchicalView;
    private TagControllerUI controller;

    private Runnable onAddTag;
    private Consumer<TagDTO> onAddChildTag;
    private Consumer<TagDTO> onEditTag;
    private Consumer<TagDTO> onDeleteTag;

    /**
     * Creates a new instance of the Tags view.
     */
    public TagsViewImpl() {
        initialize();
    }

    private void initialize() {
        initializeBaseComponents();

        root = new VBox(30);
        root.getStyleClass().add("tags-container");

        createHeader();
        createTagsContainer();

        StackPane containerWithOverlays = new StackPane();
        containerWithOverlays.getChildren().addAll(root, getLoadingOverlay(), getErrorContainer());
    }

    private void createHeader() {
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Categories");
        title.getStyleClass().add("page-title");

        Button addButton = new Button("+ Add Category");
        addButton.getStyleClass().add("add-category-btn");
        addButton.setOnAction(e -> {
            if (onAddTag != null) onAddTag.run();
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.getChildren().addAll(title, spacer, addButton);
        root.getChildren().add(header);
    }

    private void createTagsContainer() {
        VBox container = new VBox(20);
        container.getStyleClass().addAll("categories-grid-container");

        hierarchicalView = new VBox(15);
        hierarchicalView.getStyleClass().add("hierarchical-view");

        ScrollPane scrollPane = new ScrollPane(hierarchicalView);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.getStyleClass().add("categories-scroll");

        container.getChildren().add(scrollPane);
        root.getChildren().add(container);
    }

    public void setController(TagControllerUI controller) {
        this.controller = controller;
    }

    /** {@inheritDoc} */
    @Override
    public Parent getRoot() {
        return root;
    }

    /** {@inheritDoc} */
    @Override
    public void displayTags(List<TagDTO> tags) {
        hierarchicalView.getChildren().clear();

        if (tags.isEmpty()) {
            Label emptyLabel = new Label("No categories yet. Create your first category!");
            emptyLabel.getStyleClass().add("empty-state");
            hierarchicalView.getChildren().add(emptyLabel);
        } else {
            for (TagDTO rootTag : tags) {
                VBox tagSection = createHierarchicalTagSection(rootTag, 0);
                hierarchicalView.getChildren().add(tagSection);
            }
        }
    }

    private VBox createHierarchicalTagSection(TagDTO tag, int level) {
        VBox section = new VBox(12);
        section.getStyleClass().add("hierarchical-tag-section");

        HBox tagCard = createHierarchicalTagCard(tag, level);
        section.getChildren().add(tagCard);

        if (controller != null) {
            try {
                List<TagDTO> children = controller.getChildTags(tag.id());
                if (!children.isEmpty()) {
                    VBox childrenContainer = new VBox(10);
                    childrenContainer.getStyleClass().add("hierarchical-children-container");
                    childrenContainer.setPadding(new Insets(0, 0, 0, 30 + (level * 20)));

                    for (TagDTO child : children) {
                        VBox childSection = createHierarchicalTagSection(child, level + 1);
                        childrenContainer.getChildren().add(childSection);
                    }

                    section.getChildren().add(childrenContainer);
                }
            } catch (Exception e) {
                System.err.println("Error loading child tags for " + tag.id() + ": " + e.getMessage());
            }
        }

        return section;
    }

    private HBox createHierarchicalTagCard(TagDTO tag, int level) {
        HBox card = new HBox(15);
        card.getStyleClass().add("hierarchical-tag-card");
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(15, 20, 15, 20 + (level * 25)));

        if (level > 0) {
            Label levelIndicator = new Label("└─");
            levelIndicator.getStyleClass().add("level-indicator");
            card.getChildren().add(levelIndicator);
        }

        Label icon = new Label("🏷️");
        icon.getStyleClass().add("tag-icon");

        VBox tagInfo = new VBox(4);

        Label name = new Label(tag.name());
        if (level == 0) {
            name.getStyleClass().add("tag-name-root");
        } else if (level == 1) {
            name.getStyleClass().add("tag-name-level1");
        } else {
            name.getStyleClass().add("tag-name-level2");
        }

        Label description = new Label(tag.description() != null ? tag.description() : "No description");
        if (level == 0) {
            description.getStyleClass().add("tag-description-root");
        } else if (level == 1) {
            description.getStyleClass().add("tag-description-level1");
        } else {
            description.getStyleClass().add("tag-description-level2");
        }

        tagInfo.getChildren().addAll(name, description);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox actions = new HBox(10);
        actions.setAlignment(Pos.CENTER_RIGHT);

        Button addChildBtn = new Button("+ Child");
        addChildBtn.getStyleClass().add("tag-add-child-btn");
        addChildBtn.setOnAction(e -> {
            if (onAddChildTag != null) onAddChildTag.accept(tag);
        });

        Button editBtn = new Button("Edit");
        editBtn.getStyleClass().add("tag-edit-btn");
        editBtn.setOnAction(e -> {
            if (onEditTag != null) onEditTag.accept(tag);
        });

        Button deleteBtn = new Button("Delete");
        deleteBtn.getStyleClass().add("tag-delete-btn");
        deleteBtn.setOnAction(e -> {
            if (onDeleteTag != null) onDeleteTag.accept(tag);
        });

        actions.getChildren().addAll(addChildBtn, editBtn, deleteBtn);
        card.getChildren().addAll(icon, tagInfo, spacer, actions);

        return card;
    }

    /**
     * Sets the handler for adding a new root category.
     *
     * @param handler the action to execute
     */
    public void setOnAddTag(Runnable handler) {
        this.onAddTag = handler;
    }

    /**
     * Sets the handler for adding a child category under a specific tag.
     *
     * @param handler the action to execute with the parent tag
     */
    public void setOnAddChildTag(Consumer<TagDTO> handler) {
        this.onAddChildTag = handler;
    }

    /**
     * Sets the handler for editing a tag.
     *
     * @param handler the action to execute with the tag
     */
    public void setOnEditTag(Consumer<TagDTO> handler) {
        this.onEditTag = handler;
    }

    /**
     * Sets the handler for deleting a tag.
     *
     * @param handler the action to execute with the tag
     */
    public void setOnDeleteTag(Consumer<TagDTO> handler) {
        this.onDeleteTag = handler;
    }
}