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

import it.unicam.cs.mpgc.jbudget126139.model.TransactionDirection;
import it.unicam.cs.mpgc.jbudget126139.service.dto.TransactionDTO;
import it.unicam.cs.mpgc.jbudget126139.ui.util.FormatUtils;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;
import java.util.Currency;

/**
 * A UI component that represents a single transaction entry in a list or dashboard.
 * <p>
 * Each {@code TransactionItem} displays:
 * <ul>
 *     <li>An icon indicating the transaction direction (credit ↗ or debit ↘)</li>
 *     <li>The description of the transaction</li>
 *     <li>The date of the transaction (formatted as "MMM dd")</li>
 *     <li>The transaction amount, formatted with the given currency</li>
 * </ul>
 * </p>
 */
public class TransactionItem extends HBox {

    private final TransactionDTO transaction;
    private final Currency currency;

    /**
     * Creates a {@code TransactionItem} with the specified transaction and currency.
     *
     * @param transaction the transaction data to display
     * @param currency    the currency used to format the amount
     */
    public TransactionItem(TransactionDTO transaction, Currency currency) {
        this.transaction = transaction;
        this.currency = currency;
        initializeItem();
    }

    /**
     * Initializes the layout, styling, and content of the transaction item.
     */
    private void initializeItem() {
        getStyleClass().add("transaction-item");
        setAlignment(Pos.CENTER_LEFT);
        setSpacing(15);

        Label icon = new Label(transaction.direction() == TransactionDirection.CREDIT ? "↗" : "↘");
        icon.getStyleClass().add(transaction.direction() == TransactionDirection.CREDIT
                ? "income-icon" : "expense-icon");

        VBox details = createDetails();
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label amount = createAmountLabel();

        getChildren().addAll(icon, details, spacer, amount);
    }

    /**
     * Creates the details section of the transaction, showing description and date.
     *
     * @return a VBox containing description and date labels
     */
    private VBox createDetails() {
        VBox details = new VBox(2);

        Label description = new Label(transaction.description() != null
                ? transaction.description()
                : "Transaction");
        description.getStyleClass().add("transaction-description");

        Label date = new Label(transaction.valueDate().format(DateTimeFormatter.ofPattern("MMM dd")));
        date.getStyleClass().add("transaction-date");

        details.getChildren().addAll(description, date);
        return details;
    }

    /**
     * Creates the amount label for the transaction, formatted with currency
     * and styled based on its direction (positive or negative).
     *
     * @return a styled label showing the transaction amount
     */
    private Label createAmountLabel() {
        String prefix = transaction.direction() == TransactionDirection.CREDIT ? "+" : "-";
        String formattedAmount = FormatUtils.formatCurrency(transaction.amount(), currency);

        Label amount = new Label(prefix + formattedAmount);
        amount.getStyleClass().add(transaction.direction() == TransactionDirection.CREDIT
                ? "amount-positive" : "amount-negative");

        return amount;
    }

    /**
     * Returns the underlying transaction represented by this component.
     *
     * @return the {@code TransactionDTO} instance
     */
    public TransactionDTO getTransaction() {
        return transaction;
    }
}