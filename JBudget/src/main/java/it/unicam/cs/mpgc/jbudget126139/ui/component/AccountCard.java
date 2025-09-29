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
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.util.function.Consumer;

/**
 * UI component representing an account card in the application's user interface.
 * <p>
 * Displays key information about an account, including its name, currency, and
 * current balance, and provides actions for editing and deleting the account.
 * </p>
 */
public class AccountCard extends VBox {

    private final AccountDTO account;
    private final Label balanceLabel;
    private final Consumer<AccountDTO> onEditAction;
    private final Consumer<AccountDTO> onDeleteAction;

    /**
     * Constructs a new {@code AccountCard}.
     *
     * @param account        the account data to display
     * @param balance        the initial balance of the account
     * @param onEditAction   the action to execute when the "Edit" button is clicked
     * @param onDeleteAction the action to execute when the "Delete" button is clicked
     */
    public AccountCard(AccountDTO account,
                       BigDecimal balance,
                       Consumer<AccountDTO> onEditAction,
                       Consumer<AccountDTO> onDeleteAction) {
        this.account = account;
        this.onEditAction = onEditAction;
        this.onDeleteAction = onDeleteAction;

        this.balanceLabel = new Label();

        initializeCard();
        updateBalance(balance);
    }

    /**
     * Initializes the card layout and its UI components.
     */
    private void initializeCard() {
        getStyleClass().add("account-card");
        setPrefWidth(300);
        setPrefHeight(180);
        setSpacing(20);
        HBox header = createCardHeader();
        balanceLabel.getStyleClass().add("card-balance");
        VBox accountInfo = createAccountInfo();
        HBox actions = createActions();
        getChildren().addAll(header, balanceLabel, accountInfo, actions);
    }

    /**
     * Creates the header section of the card, showing the currency and an icon.
     *
     * @return the header {@link HBox}
     */
    private HBox createCardHeader() {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        Label currency = new Label(account.currency().toString());
        currency.getStyleClass().add("card-type");

        Label icon = new Label("💳");
        icon.getStyleClass().add("card-icon");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.getChildren().addAll(currency, spacer, icon);
        return header;
    }

    /**
     * Creates the section of the card displaying the account name.
     *
     * @return the account info {@link VBox}
     */
    private VBox createAccountInfo() {
        VBox info = new VBox(8);

        Label accountName = new Label(account.name());
        accountName.getStyleClass().add("card-account-name");

        info.getChildren().add(accountName);
        return info;
    }

    /**
     * Creates the action buttons ("Edit" and "Delete") for the card.
     *
     * @return the actions {@link HBox}
     */
    private HBox createActions() {
        HBox actions = new HBox(10);

        Button editBtn = new Button("Edit");
        editBtn.getStyleClass().add("card-action-btn");
        editBtn.setOnAction(e -> onEditAction.accept(account));

        Button deleteBtn = new Button("Delete");
        deleteBtn.getStyleClass().add("card-delete-btn");
        deleteBtn.setOnAction(e -> onDeleteAction.accept(account));

        actions.getChildren().addAll(editBtn, deleteBtn);
        return actions;
    }

    /**
     * Updates the displayed balance on the card.
     *
     * @param balance the new balance value
     */
    public void updateBalance(BigDecimal balance) {
        String formattedBalance = FormatUtils.formatCurrency(balance, account.currency());
        balanceLabel.setText(formattedBalance);
    }

    /**
     * Returns the {@link AccountDTO} associated with this card.
     *
     * @return the account DTO
     */
    public AccountDTO getAccount() {
        return account;
    }
}