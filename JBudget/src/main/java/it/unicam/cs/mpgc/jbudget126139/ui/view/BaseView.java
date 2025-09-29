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

package it.unicam.cs.mpgc.jbudget126139.ui.view;

import javafx.scene.Parent;

/**
 * Base interface for all UI views in the application.
 * <p>
 * Defines common methods for rendering the view's root node
 * and handling generic UI states such as loading and error display.
 * </p>
 * <p>
 * All specific views (e.g., accounts, dashboard, transactions)
 * should extend this interface to provide a consistent contract
 * across the UI layer.
 * </p>
 */
public interface BaseView {

    /**
     * Returns the root node of this view, which can be
     * attached to the main application layout.
     *
     * @return the root {@link Parent} node
     */
    Parent getRoot();

    /**
     * Displays a loading indicator in the view,
     * signaling that data is being processed or fetched.
     */
    void showLoading();

    /**
     * Hides the loading indicator previously shown
     * by {@link #showLoading()}.
     */
    void hideLoading();

    /**
     * Displays an error message in the view,
     * typically shown to notify the user of a failure.
     *
     * @param message the error message to display
     */
    void showError(String message);

    /**
     * Hides the error message previously displayed
     * by {@link #showError(String)}.
     */
    void hideError();
}