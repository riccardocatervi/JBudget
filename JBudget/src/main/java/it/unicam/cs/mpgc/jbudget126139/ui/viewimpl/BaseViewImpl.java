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

import it.unicam.cs.mpgc.jbudget126139.ui.view.BaseView;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Base implementation of {@link BaseView} providing common UI components
 * for displaying loading overlays and error messages.
 * <p>
 * Subclasses should call {@link #initializeBaseComponents()} during their
 * initialization to include these shared UI elements.
 * </p>
 */
public abstract class BaseViewImpl implements BaseView {

    private StackPane loadingOverlay;
    private VBox errorContainer;

    /**
     * Initializes the base UI components (loading overlay and error container).
     * <p>
     * This method should be called by subclasses in their initialization logic
     * before composing their specific layout.
     * </p>
     */
    protected void initializeBaseComponents() {
        createLoadingOverlay();
        createErrorContainer();
    }

    private void createLoadingOverlay() {
        loadingOverlay = new StackPane();
        loadingOverlay.getStyleClass().add("loading-overlay");
        loadingOverlay.setVisible(false);

        VBox loadingContent = new VBox(10);
        loadingContent.setAlignment(Pos.CENTER);

        ProgressIndicator spinner = new ProgressIndicator();
        spinner.getStyleClass().add("loading-spinner");

        Label loadingText = new Label("Loading...");
        loadingText.getStyleClass().add("loading-text");

        loadingContent.getChildren().addAll(spinner, loadingText);
        loadingOverlay.getChildren().add(loadingContent);
    }

    private void createErrorContainer() {
        errorContainer = new VBox(10);
        errorContainer.getStyleClass().add("error-container");
        errorContainer.setVisible(false);
        errorContainer.setAlignment(Pos.CENTER);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showLoading() {
        if (loadingOverlay != null) {
            loadingOverlay.setVisible(true);
            loadingOverlay.toFront();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void hideLoading() {
        if (loadingOverlay != null) {
            loadingOverlay.setVisible(false);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showError(String message) {
        if (errorContainer != null) {
            errorContainer.getChildren().clear();

            Label errorIcon = new Label("⚠️");
            errorIcon.getStyleClass().add("error-icon");

            Label errorText = new Label(message);
            errorText.getStyleClass().add("error-text");
            errorText.setWrapText(true);

            errorContainer.getChildren().addAll(errorIcon, errorText);
            errorContainer.setVisible(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void hideError() {
        if (errorContainer != null) {
            errorContainer.setVisible(false);
        }
    }

    /**
     * Returns the overlay node used to display a loading spinner and message.
     *
     * @return the {@link StackPane} containing the loading overlay
     */
    protected StackPane getLoadingOverlay() {
        return loadingOverlay;
    }

    /**
     * Returns the container used to display error messages.
     *
     * @return the {@link VBox} containing error message UI
     */
    protected VBox getErrorContainer() {
        return errorContainer;
    }
}