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

import it.unicam.cs.mpgc.jbudget126139.service.dto.AccountDTO;
import it.unicam.cs.mpgc.jbudget126139.ui.util.FormatUtils;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;

/**
 * UI component that displays a quick financial overview in the sidebar,
 * including current balance, total income, and total expenses.
 * <p>
 * The component is designed to be updated dynamically when account data changes.
 * </p>
 */
public class SidebarStats extends VBox {

    private Label balanceLabel;
    private Label incomeLabel;
    private Label expenseLabel;

    /**
     * Creates a new {@code SidebarStats} component with default placeholder values.
     */
    public SidebarStats() {
        initialize();
    }

    /**
     * Initializes the layout, labels, and styles for the sidebar stats.
     */
    private void initialize() {
        getStyleClass().add("sidebar-stats");
        setSpacing(15);

        Label title = new Label("Quick Overview");
        title.getStyleClass().add("stats-title");

        balanceLabel = new Label("$0.00");
        balanceLabel.getStyleClass().add("balance-amount");

        Label balanceText = new Label("Current Balance");
        balanceText.getStyleClass().add("balance-text");

        HBox incomeExpense = new HBox(20);

        VBox incomeBox = new VBox(5);
        incomeLabel = new Label("$0.00");
        incomeLabel.getStyleClass().add("income-amount");
        Label incomeText = new Label("Income");
        incomeText.getStyleClass().add("income-text");
        incomeBox.getChildren().addAll(incomeLabel, incomeText);

        VBox expenseBox = new VBox(5);
        expenseLabel = new Label("$0.00");
        expenseLabel.getStyleClass().add("expense-amount");
        Label expenseText = new Label("Expenses");
        expenseText.getStyleClass().add("expense-text");
        expenseBox.getChildren().addAll(expenseLabel, expenseText);

        incomeExpense.getChildren().addAll(incomeBox, expenseBox);

        getChildren().addAll(title, balanceLabel, balanceText, incomeExpense);
    }

    /**
     * Updates the sidebar statistics with the provided financial data.
     *
     * @param balance the current account balance
     * @param income  the total income amount
     * @param expenses the total expense amount
     * @param account the account whose currency should be used for formatting
     */
    public void updateStats(BigDecimal balance, BigDecimal income, BigDecimal expenses, AccountDTO account) {
        String formattedBalance = FormatUtils.formatCurrency(balance, account.currency());
        String formattedIncome = FormatUtils.formatCurrency(income, account.currency());
        String formattedExpenses = FormatUtils.formatCurrency(expenses, account.currency());

        balanceLabel.setText(formattedBalance);
        incomeLabel.setText(formattedIncome);
        expenseLabel.setText(formattedExpenses);
    }

    /**
     * Resets the sidebar statistics to zero values for the given account.
     * <p>
     * Useful when switching between accounts before statistics are loaded.
     * </p>
     *
     * @param account the account whose currency should be used for formatting
     */
    public void updateForAccount(AccountDTO account) {
        updateStats(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, account);
    }
}