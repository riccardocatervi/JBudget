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

package it.unicam.cs.mpgc.jbudget126139.ui.component;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * A reusable UI component that displays a simple statistic in a card format.
 * <p>
 * Each card shows a title, a main value, and a subtitle. It is commonly used
 * in dashboards or overviews to highlight key financial or contextual information.
 * </p>
 */
public class StatCard extends VBox {

    private final Label titleLabel;
    private final Label valueLabel;
    private final Label subtitleLabel;

    /**
     * Creates a new {@code StatCard} with the given title, initial value, and subtitle.
     *
     * @param title        the title text displayed at the top of the card
     * @param initialValue the initial value to display (e.g., formatted currency or percentage)
     * @param subtitle     the subtitle text displayed below the value
     */
    public StatCard(String title, String initialValue, String subtitle) {
        this.titleLabel = new Label(title);
        this.valueLabel = new Label(initialValue);
        this.subtitleLabel = new Label(subtitle);

        initializeCard();
    }

    /**
     * Initializes the layout, alignment, styles, and structure of the card.
     */
    private void initializeCard() {
        getStyleClass().add("stat-card");
        setAlignment(Pos.CENTER);
        setSpacing(10);

        titleLabel.getStyleClass().add("stat-title");
        valueLabel.getStyleClass().add("stat-value");
        subtitleLabel.getStyleClass().add("stat-subtitle");

        getChildren().addAll(titleLabel, valueLabel, subtitleLabel);
    }

    /**
     * Updates the displayed value in the card.
     *
     * @param newValue the new value text (e.g., updated balance or percentage)
     */
    public void updateValue(String newValue) {
        valueLabel.setText(newValue);
    }

    /**
     * Updates the displayed title in the card.
     *
     * @param newTitle the new title text
     */
    public void updateTitle(String newTitle) {
        titleLabel.setText(newTitle);
    }

    /**
     * Updates the displayed subtitle in the card.
     *
     * @param newSubtitle the new subtitle text
     */
    public void updateSubtitle(String newSubtitle) {
        subtitleLabel.setText(newSubtitle);
    }
}