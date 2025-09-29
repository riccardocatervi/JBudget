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

import it.unicam.cs.mpgc.jbudget126139.controller.TransactionController;
import it.unicam.cs.mpgc.jbudget126139.model.TransactionDirection;
import it.unicam.cs.mpgc.jbudget126139.service.dto.AccountDTO;
import it.unicam.cs.mpgc.jbudget126139.service.dto.TagDTO;
import it.unicam.cs.mpgc.jbudget126139.service.dto.TransactionDTO;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;

/**
 * Dialog for creating a new transaction.
 * <p>
 * Provides a form with fields for:
 * <ul>
 *   <li>Date</li>
 *   <li>Amount</li>
 *   <li>Type (Income/Expense)</li>
 *   <li>Description (optional)</li>
 *   <li>Category (optional)</li>
 * </ul>
 * On confirmation, the transaction is persisted via the {@link TransactionController}.
 * </p>
 */
public class AddTransactionDialog extends BaseDialog<TransactionDTO> {

    private final AccountDTO account;
    private final List<TagDTO> availableTags;
    private final TransactionController transactionController;

    private DatePicker datePicker;
    private TextField amountField;
    private RadioButton incomeBtn;
    private TextArea descField;
    private ComboBox<TagDTO> categoryCombo;

    /**
     * Creates a new {@code AddTransactionDialog}.
     *
     * @param account               the account to which the transaction will belong
     * @param availableTags         the list of available categories (can be empty)
     * @param transactionController the controller responsible for managing transactions
     */
    public AddTransactionDialog(AccountDTO account,
                                List<TagDTO> availableTags,
                                TransactionController transactionController) {
        super("Add Transaction", 600, 650);
        this.account = account;
        this.transactionController = transactionController;
        this.availableTags = (availableTags != null) ? new ArrayList<>(availableTags) : new ArrayList<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getDialogTitle() {
        return "Add Transaction";
    }

    /**
     * Builds the form content for this dialog.
     * <p>
     * Includes date, amount, type, description, and category selection.
     * </p>
     *
     * @return a {@link VBox} containing the form layout
     */
    @Override
    protected VBox createFormContent() {
        debugAvailableTags();
        VBox form = new VBox(20);
        form.getChildren().addAll(
                buildDateSection(),
                buildAmountSection(),
                buildTypeSection(),
                buildDescriptionSection(),
                buildCategorySection()
        );
        return form;
    }

    private VBox buildDateSection() {
        VBox box = new VBox(8);
        Label label = boldLabel("Date*:");
        datePicker = new DatePicker(LocalDate.now());
        datePicker.getStyleClass().add("dialog-field");
        box.getChildren().addAll(label, datePicker);
        return box;
    }

    private VBox buildAmountSection() {
        VBox box = new VBox(8);
        Label label = boldLabel("Amount*:");
        amountField = new TextField();
        amountField.setPromptText("0.00");
        amountField.getStyleClass().add("dialog-field");
        box.getChildren().addAll(label, amountField);
        return box;
    }

    private VBox buildTypeSection() {
        VBox box = new VBox(8);
        Label label = boldLabel("Type*:");
        ToggleGroup group = new ToggleGroup();
        HBox buttons = new HBox(15);

        incomeBtn = new RadioButton("Income");
        incomeBtn.setToggleGroup(group);
        incomeBtn.getStyleClass().add("type-radio");
        incomeBtn.setSelected(true);

        RadioButton expenseBtn = new RadioButton("Expense");
        expenseBtn.setToggleGroup(group);
        expenseBtn.getStyleClass().add("type-radio");

        buttons.getChildren().addAll(incomeBtn, expenseBtn);
        box.getChildren().addAll(label, buttons);
        return box;
    }

    private VBox buildDescriptionSection() {
        VBox box = new VBox(8);
        Label label = boldLabel("Description:");
        descField = new TextArea();
        descField.setPromptText("Transaction description");
        descField.setPrefRowCount(3);
        descField.setWrapText(true);
        descField.getStyleClass().add("dialog-field");
        box.getChildren().addAll(label, descField);
        return box;
    }

    private VBox buildCategorySection() {
        VBox box = new VBox(8);
        Label label = boldLabel("Category:");
        categoryCombo = new ComboBox<>();
        categoryCombo.setPromptText("Select Category (Optional)");
        addAvailableTagsIfPresent(categoryCombo);
        categoryCombo.setConverter(tagConverter());
        categoryCombo.getStyleClass().add("dialog-field");
        box.getChildren().addAll(label, categoryCombo);
        return box;
    }

    private void addAvailableTagsIfPresent(ComboBox<TagDTO> combo) {
        if (availableTags != null && !availableTags.isEmpty()) {
            combo.getItems().addAll(availableTags);
            System.out.println("Added " + availableTags.size() + " tags to combo");
        } else {
            System.out.println("No tags to add to combo (null or empty)");
        }
    }

    private StringConverter<TagDTO> tagConverter() {
        return new StringConverter<>() {
            @Override public String toString(TagDTO tag) { return tag != null ? tag.name() : ""; }
            @Override public TagDTO fromString(String s) { return null; }
        };
    }

    private Label boldLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-weight: bold;");
        return l;
    }

    private void debugAvailableTags() {
        System.out.println("createFormContent called");
        System.out.println("availableTags is null? " + (availableTags == null));
        if (availableTags == null) {
            System.out.println("ERROR: availableTags is null in createFormContent!");
        }
    }

    /**
     * Validates the user input before creating the transaction.
     * <p>
     * Ensures that:
     * <ul>
     *   <li>The date is provided</li>
     *   <li>The amount is valid and greater than zero</li>
     * </ul>
     * </p>
     *
     * @return {@code true} if the input is valid, {@code false} otherwise
     */
    @Override
    protected boolean validateInput() {
        if (datePicker.getValue() == null) {
            showError("Date is required.");
            return false;
        }
        if (amountField.getText() == null || amountField.getText().trim().isEmpty()) {
            showError("Amount is required.");
            return false;
        }
        try {
            BigDecimal amount = new BigDecimal(amountField.getText().trim());
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                showError("Amount must be greater than zero.");
                return false;
            }
        } catch (NumberFormatException e) {
            showError("Please enter a valid amount.");
            return false;
        }
        return true;
    }

    /**
     * Creates the {@link TransactionDTO} based on user input
     * and persists it through the {@link TransactionController}.
     *
     * @return the created {@link TransactionDTO}
     * @throws RuntimeException if transaction creation fails
     */
    @Override
    protected TransactionDTO createResult() {
        TransactionDirection direction = incomeBtn.isSelected()
                ? TransactionDirection.CREDIT
                : TransactionDirection.DEBIT;

        Set<UUID> tagIds = new HashSet<>();
        if (categoryCombo.getValue() != null) {
            tagIds.add(categoryCombo.getValue().id());
        }

        return transactionController.createTransaction(
                account.id(),
                datePicker.getValue().atStartOfDay().atOffset(ZoneOffset.UTC),
                new BigDecimal(amountField.getText().trim()),
                direction,
                (descField.getText() == null || descField.getText().trim().isEmpty())
                        ? null
                        : descField.getText().trim(),
                tagIds
        );
    }
}