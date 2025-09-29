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

package it.unicam.cs.mpgc.jbudget126139.ui.dialog;

import it.unicam.cs.mpgc.jbudget126139.controller.TagController;
import it.unicam.cs.mpgc.jbudget126139.service.dto.TagDTO;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.util.Collections;
import java.util.List;

/**
 * Dialog for creating a new category (tag) or subcategory.
 * <p>
 * If a parent category is provided, the dialog creates a subcategory under it.
 * Otherwise, the dialog allows selecting an optional parent or creating a root category.
 * On confirmation, the new category is persisted through the {@link TagController}.
 * </p>
 */
public class AddTagDialog extends BaseDialog<TagDTO> {

    private final TagDTO parent;
    private final TagController tagController;

    private TextField nameField;
    private TextArea descArea;
    private ComboBox<TagDTO> parentCombo;

    /**
     * Creates a new {@code AddTagDialog}.
     *
     * @param parent        the parent tag (or {@code null} if creating a root category)
     * @param tagController the controller responsible for managing categories
     */
    public AddTagDialog(TagDTO parent, TagController tagController) {
        super(parent == null ? "Add Category" : "Add Subcategory", 550, 500);
        this.parent = parent;
        this.tagController = tagController;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getDialogTitle() {
        return parent == null ? "Create New Category" : "Create New Subcategory";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getSaveButtonText() {
        return "Create Category";
    }

    /**
     * Builds the form content for this dialog.
     * <p>
     * Includes:
     * <ul>
     *   <li>Parent category (fixed if provided, selectable otherwise)</li>
     *   <li>Category name (required)</li>
     *   <li>Description (optional)</li>
     * </ul>
     * </p>
     *
     * @return a {@link VBox} containing the form layout
     */
    @Override
    protected VBox createFormContent() {
        VBox form = new VBox(20);
        form.getChildren().add(buildParentSection());
        form.getChildren().add(buildNameSection());
        form.getChildren().add(buildDescriptionSection());
        return form;
    }


    private VBox buildParentSection() {
        VBox box = new VBox(8);
        Label label = boldLabel("Parent Category:");
        if (parent == null) {
            parentCombo = createParentCombo();
            box.getChildren().addAll(label, parentCombo);
        } else {
            Label parentName = new Label(parent.name());
            parentName.setStyle("-fx-text-fill: #2196F3; -fx-font-weight: bold;");
            box.getChildren().addAll(label, parentName);
        }
        return box;
    }

    private ComboBox<TagDTO> createParentCombo() {
        ComboBox<TagDTO> combo = new ComboBox<>();
        combo.setPromptText("Select Parent (Optional - Leave empty for root category)");
        combo.getItems().add(null); // Root option
        combo.getItems().addAll(loadRootTagsSafe());
        combo.setConverter(parentTagConverter());
        combo.getStyleClass().add("dialog-field");
        return combo;
    }

    private List<TagDTO> loadRootTagsSafe() {
        try {
            return tagController.listRootTags();
        } catch (Exception e) {
            System.err.println("Error loading tags for parent selector: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    private StringConverter<TagDTO> parentTagConverter() {
        return new StringConverter<>() {
            @Override public String toString(TagDTO tag) { return tag != null ? tag.name() : "Root Category"; }
            @Override public TagDTO fromString(String s) { return null; }
        };
    }

    private VBox buildNameSection() {
        nameField = new TextField();
        nameField.setPromptText("Enter category name");
        nameField.getStyleClass().add("dialog-field");
        return createLabeledSection("Category Name*:", nameField);
    }

    private VBox buildDescriptionSection() {
        descArea = new TextArea();
        descArea.setPromptText("Category description (optional)");
        descArea.setPrefRowCount(4);
        descArea.setWrapText(true);
        descArea.getStyleClass().add("dialog-field");
        return createLabeledSection("Description:", descArea);
    }

    private Label boldLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-weight: bold;");
        return l;
    }

    /**
     * Validates the user input before creating the tag.
     * <p>
     * Ensures that the category name is not empty.
     * </p>
     *
     * @return {@code true} if the input is valid, {@code false} otherwise
     */
    @Override
    protected boolean validateInput() {
        String name = nameField.getText();
        if (name == null || name.trim().isEmpty()) {
            showError("Category name is required.");
            return false;
        }
        return true;
    }

    /**
     * Creates a new {@link TagDTO} based on the user input and persists it
     * through the {@link TagController}.
     * <p>
     * If a parent category is selected, the new tag is created as a child.
     * Otherwise, it is created as a root category.
     * </p>
     *
     * @return the created {@link TagDTO}
     * @throws RuntimeException if the creation operation fails
     */
    @Override
    protected TagDTO createResult() {
        try {
            TagDTO selectedParent = parent != null ? parent : (parentCombo != null ? parentCombo.getValue() : null);

            if (selectedParent != null) {
                return tagController.createChildTag(
                        nameField.getText().trim(),
                        descArea.getText().trim().isEmpty() ? null : descArea.getText().trim(),
                        selectedParent.id()
                );
            } else {
                return tagController.createTag(
                        nameField.getText().trim(),
                        descArea.getText().trim().isEmpty() ? null : descArea.getText().trim()
                );
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to create category: " + e.getMessage(), e);
        }
    }
}