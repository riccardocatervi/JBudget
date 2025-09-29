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

import it.unicam.cs.mpgc.jbudget126139.service.dto.TransactionDTO;
import it.unicam.cs.mpgc.jbudget126139.service.dto.TagDTO;
import javafx.scene.Parent;

import java.util.List;

/**
 * View interface for managing and displaying transactions.
 * <p>
 * Provides methods for rendering a list of transactions,
 * handling category-based filters, and applying or clearing
 * search criteria such as type, category, or date range.
 * </p>
 * <p>
 * The implementing class is responsible for the UI rendering
 * and must react to updates from the controller layer.
 * </p>
 */
public interface TransactionsView extends BaseView {

    /**
     * Returns the root node of this view, to be attached
     * to the application's scene graph.
     *
     * @return the root {@link Parent} of the transactions view
     */
    Parent getRoot();

    /**
     * Displays the list of transactions in the view.
     *
     * @param transactions a list of {@link TransactionDTO} objects to display
     */
    void displayTransactions(List<TransactionDTO> transactions);

    /**
     * Updates the available filters (e.g., categories)
     * shown in the view.
     *
     * @param categories a list of {@link TagDTO} objects used as filter options
     */
    void updateFilters(List<TagDTO> categories);


    /**
     * Clears all currently applied filters and restores
     * the full list of transactions.
     */
    void clearFilters();
}