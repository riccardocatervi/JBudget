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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/**
 * Dialog for editing an existing category (tag).
 * <p>
 * Provides a form pre-filled with the tag's current details
 * (name and optional description) and allows the user to update them.
 * On confirmation, the changes are applied through the {@link TagController}.
 * </p>
 */
public class EditTagDialog extends BaseDialog<TagDTO> {

    private final TagDTO originalTag;
    private final TagController tagController;

    private TextField nameField;
    private TextArea descArea;

    /**
     * Creates a new {@code EditTagDialog}.
     *
     * @param tag          the tag to be edited
     * @param tagController the controller responsible for managing tags
     */
    public EditTagDialog(TagDTO tag, TagController tagController) {
        super("Edit Category", 550, 450);
        this.originalTag = tag;
        this.tagController = tagController;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getDialogTitle() {
        return "Edit Category";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getSaveButtonText() {
        return "Save Changes";
    }

    /**
     * Builds the form content for this dialog.
     * <p>
     * The form includes:
     * <ul>
     *   <li>Category name (required)</li>
     *   <li>Description (optional)</li>
     * </ul>
     * Pre-fills the fields with the tag’s current values.
     * </p>
     *
     * @return a {@link VBox} containing the form layout
     */
    @Override
    protected VBox createFormContent() {
        VBox form = new VBox(20);

        nameField = new TextField(originalTag.name());
        nameField.getStyleClass().add("dialog-field");
        VBox nameSection = createLabeledSection("Category Name*:", nameField);

        descArea = new TextArea(originalTag.description() != null ? originalTag.description() : "");
        descArea.setPrefRowCount(4);
        descArea.setWrapText(true);
        descArea.getStyleClass().add("dialog-field");
        VBox descSection = createLabeledSection("Description:", descArea);

        form.getChildren().addAll(nameSection, descSection);
        return form;
    }

    /**
     * Validates the user input before updating the tag.
     * <p>
     * Ensures that the category name is provided and not empty.
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
     * Creates the updated {@link TagDTO} based on the user input
     * and applies the changes through the {@link TagController}.
     *
     * @return the updated {@link TagDTO}
     * @throws RuntimeException if the update operation fails
     */
    @Override
    protected TagDTO createResult() {
        try {
            return tagController.updateTag(
                    originalTag.id(),
                    nameField.getText().trim(),
                    descArea.getText().trim().isEmpty() ? null : descArea.getText().trim()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to update category: " + e.getMessage(), e);
        }
    }
}