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

package it.unicam.cs.mpgc.jbudget126139.ui.navigation;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Sidebar navigation component for the application.
 * <p>
 * Provides navigation buttons for each {@link NavigationTarget}.
 * Each button displays an icon and label, and invokes a callback
 * when clicked to request navigation.
 * </p>
 * <p>
 * The currently active navigation target is highlighted using
 * a CSS style class.
 * </p>
 */
public class SidebarNavigator extends VBox {

    private final Map<NavigationTarget, Button> navigationButtons;
    private final Consumer<NavigationTarget> onNavigationRequested;
    private NavigationTarget activeTarget;


    /**
     * Creates a new sidebar navigator with a navigation callback.
     *
     * @param onNavigationRequested consumer invoked when a navigation
     *                              button is clicked
     */
    public SidebarNavigator(Consumer<NavigationTarget> onNavigationRequested) {
        this.onNavigationRequested = onNavigationRequested;
        this.navigationButtons = new HashMap<>();
        createNavigationButtons();
    }

    /**
     * Builds the navigation buttons for all available {@link NavigationTarget} values.
     * Dashboard is set as the initially active target.
     */
    private void createNavigationButtons() {
        for (NavigationTarget target : NavigationTarget.values()) {
            Button navButton = createNavigationButton(target);
            navigationButtons.put(target, navButton);
            getChildren().add(navButton);
        }
        setActiveTarget(NavigationTarget.DASHBOARD);
    }

    /**
     * Creates a styled navigation button for a specific target.
     *
     * @param target the navigation target to represent
     * @return the configured navigation button
     */
    private Button createNavigationButton(NavigationTarget target) {
        Button button = new Button();

        HBox content = new HBox(15);
        content.setAlignment(Pos.CENTER_LEFT);

        Label iconLabel = new Label(target.getIcon());
        iconLabel.getStyleClass().add("nav-icon");

        Label textLabel = new Label(target.getDisplayName());
        textLabel.getStyleClass().add("nav-text");

        content.getChildren().addAll(iconLabel, textLabel);
        button.setGraphic(content);

        button.getStyleClass().add("modern-nav-button");
        button.setPrefWidth(240);

        button.setOnAction(e -> {
            setActiveTarget(target);
            onNavigationRequested.accept(target);
        });

        return button;
    }

    /**
     * Sets the given navigation target as active, applying
     * a CSS style to highlight it.
     *
     * @param target the target to activate
     */
    public void setActiveTarget(NavigationTarget target) {
        navigationButtons.values().forEach(btn -> btn.getStyleClass().remove("active"));
        Button activeButton = navigationButtons.get(target);
        if (activeButton != null) {
            activeButton.getStyleClass().add("active");
            this.activeTarget = target;
        }
    }

    /**
     * Returns the currently active navigation target.
     *
     * @return the active target, or {@code null} if none is active
     */
    public NavigationTarget getActiveTarget() {
        return activeTarget;
    }
}