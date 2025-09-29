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
import javafx.scene.Parent;

import java.math.BigDecimal;
import java.util.List;

/**
 * View interface for the dashboard screen of the application.
 * <p>
 * Provides methods for displaying account statistics, updating charts,
 * and showing recent transactions. The dashboard serves as the main
 * overview of the user's financial data.
 * </p>
 * <p>
 * The implementing class is responsible for rendering the UI components
 * and updating them when the controller provides new data.
 * </p>
 */
public interface DashboardView extends BaseView {

    /**
     * Returns the root node of the dashboard view, which can be
     * attached to the main layout.
     *
     * @return the root {@link Parent} node
     */
    Parent getRoot();

    /**
     * Updates the displayed account balance in the dashboard.
     *
     * @param balance the current account balance
     */
    void updateBalance(BigDecimal balance);

    /**
     * Updates the displayed income and expenses for the current period.
     *
     * @param income   the total income
     * @param expenses the total expenses
     */
    void updateIncomeExpenses(BigDecimal income, BigDecimal expenses);

    /**
     * Updates the list of recent transactions displayed on the dashboard.
     *
     * @param transactions the list of recent {@link TransactionDTO} objects
     */
    void updateRecentTransactions(List<TransactionDTO> transactions);

    /**
     * Updates the expense chart with the given income, expenses,
     * and period label (e.g., "This Month", "Last 3 Months").
     *
     * @param income   the total income
     * @param expenses the total expenses
     * @param period   the label representing the selected time period
     */
    void updateExpenseChart(BigDecimal income, BigDecimal expenses, String period);

    /**
     * Displays an empty state in the dashboard,
     * typically used when no account is selected.
     */
    void showEmptyState();

    /**
     * Hides the empty state, restoring the normal dashboard view.
     */
    void hideEmptyState();
}