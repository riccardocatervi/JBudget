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

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Optional;

/**
 * Base class for modal dialogs used in the application.
 * <p>
 * Provides a standardized layout with:
 * <ul>
 *   <li>a title section,</li>
 *   <li>a form area defined by subclasses,</li>
 *   <li>Cancel/Save buttons,</li>
 *   <li>basic error handling.</li>
 * </ul>
 * <p>
 * Subclasses must implement the abstract methods to provide
 * specific dialog content and behavior.
 * </p>
 *
 * @param <T> the type of result returned by the dialog
 */
public abstract class BaseDialog<T> extends Stage {

    private T result;
    private boolean confirmed = false;
    private final String dialogTitle;
    private final double dialogWidth;
    private final double dialogHeight;

    /**
     * Creates a new {@code BaseDialog}.
     *
     * @param title  the dialog window title
     * @param width  the preferred width of the dialog
     * @param height the preferred height of the dialog
     */
    public BaseDialog(String title, double width, double height) {
        this.dialogTitle = title;
        this.dialogWidth = width;
        this.dialogHeight = height;

        initModality(Modality.APPLICATION_MODAL);
        setTitle(title);
        setWidth(width);
        setHeight(height);
    }


    private void initialize() {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        VBox content = new VBox(25);
        content.getStyleClass().add("dialog-content");
        content.setPadding(new Insets(40));

        Label title = new Label(getDialogTitle());
        title.getStyleClass().add("dialog-title");

        VBox form = createFormContent();

        HBox buttons = createButtons();

        content.getChildren().addAll(title, form, buttons);
        scrollPane.setContent(content);

        Scene scene = new Scene(scrollPane, dialogWidth, dialogHeight);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        setScene(scene);
    }

    /**
     * Returns the dialog title text to display.
     *
     * @return the dialog title
     */
    protected abstract String getDialogTitle();

    /**
     * Creates the content form for this dialog.
     *
     * @return a {@link VBox} containing the form elements
     */
    protected abstract VBox createFormContent();

    /**
     * Validates the user input from the form.
     *
     * @return {@code true} if the input is valid, {@code false} otherwise
     */
    protected abstract boolean validateInput();

    /**
     * Creates the result object based on the dialog input.
     *
     * @return the created result of type {@code T}
     */
    protected abstract T createResult();

    private HBox createButtons() {
        HBox buttons = new HBox(20);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        Button cancelBtn = new Button("Cancel");
        cancelBtn.getStyleClass().add("dialog-cancel-btn");
        cancelBtn.setPrefWidth(120);
        cancelBtn.setOnAction(e -> {
            confirmed = false;
            close();
        });

        Button saveBtn = new Button(getSaveButtonText());
        saveBtn.getStyleClass().add("dialog-save-btn");
        saveBtn.setPrefWidth(140);
        saveBtn.setOnAction(e -> handleSave());

        buttons.getChildren().addAll(cancelBtn, saveBtn);
        return buttons;
    }

    /**
     * Returns the text for the Save button.
     * Subclasses may override to provide custom labels.
     *
     * @return the save button text
     */
    protected String getSaveButtonText() {
        return "Save";
    }

    private void handleSave() {
        if (validateInput()) {
            try {
                result = createResult();
                confirmed = true;
                close();
            } catch (Exception e) {
                showError("Error: " + e.getMessage());
            }
        }
    }

    /**
     * Displays an error message inside the dialog.
     * <p>
     * By default, the error is printed to {@code System.err}.
     * Subclasses may override to display styled error messages in the UI.
     * </p>
     *
     * @param message the error message
     */
    protected void showError(String message) {
        System.err.println("Dialog Error: " + message);
    }

    /**
     * Shows the dialog and waits for user input.
     * <p>
     * If the user confirms and input is valid, returns an {@link Optional}
     * containing the result. Otherwise, returns {@link Optional#empty()}.
     * </p>
     *
     * @return the optional result of the dialog
     */
    public Optional<T> showDialogAndWait() {
        initialize();
        super.showAndWait();
        return confirmed ? Optional.ofNullable(result) : Optional.empty();
    }

    /**
     * Utility method to create a labeled form section.
     *
     * @param labelText the label text
     * @param content   the input node associated with the label
     * @return a {@link VBox} containing the labeled section
     */
    protected VBox createLabeledSection(String labelText, javafx.scene.Node content) {
        VBox section = new VBox(8);
        Label label = new Label(labelText);
        label.setStyle("-fx-font-weight: bold;");
        section.getChildren().addAll(label, content);
        return section;
    }
}