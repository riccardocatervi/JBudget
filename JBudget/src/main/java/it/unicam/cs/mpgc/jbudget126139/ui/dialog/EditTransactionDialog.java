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
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Dialog for editing an existing transaction.
 * <p>
 * Provides a form pre-filled with the transaction’s current details:
 * <ul>
 *   <li>Date</li>
 *   <li>Amount</li>
 *   <li>Type (Income/Expense)</li>
 *   <li>Description (optional)</li>
 *   <li>Categories (tags)</li>
 * </ul>
 * On confirmation, the transaction is updated through the
 * {@link TransactionController}.
 * </p>
 */
public class EditTransactionDialog extends BaseDialog<TransactionDTO> {

    private final TransactionDTO originalTransaction;
    private final AccountDTO currentAccount;
    private final List<TagDTO> availableTags;
    private final TransactionController transactionController;

    private DatePicker datePicker;
    private TextField amountField;
    private ToggleGroup typeGroup;
    private RadioButton incomeBtn;
    private RadioButton expenseBtn;
    private TextArea descField;
    private ComboBox<TagDTO> categoryCombo;
    private FlowPane selectedTagsFlow;

    private Set<UUID> selectedTagIds = new HashSet<>();

    /**
     * Creates a new {@code EditTransactionDialog}.
     *
     * @param transaction           the transaction to be edited
     * @param currentAccount        the account to which the transaction belongs
     * @param availableTags         the list of available categories/tags
     * @param transactionController the controller responsible for managing transactions
     */
    public EditTransactionDialog(TransactionDTO transaction, AccountDTO currentAccount,
                                 List<TagDTO> availableTags, TransactionController transactionController) {
        super("Edit Transaction", 600, 700);
        this.originalTransaction = transaction;
        this.currentAccount = currentAccount;
        this.availableTags = availableTags;
        this.transactionController = transactionController;

        this.selectedTagIds = new HashSet<>(transaction.tagIds());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getDialogTitle() {
        return "Edit Transaction";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getSaveButtonText() {
        return "Save Changes";
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
        VBox form = new VBox(20);

        datePicker = new DatePicker(originalTransaction.valueDate().toLocalDate());
        datePicker.getStyleClass().add("dialog-field");
        VBox dateSection = createLabeledSection("Date*:", datePicker);

        amountField = new TextField(originalTransaction.amount().toString());
        amountField.getStyleClass().add("dialog-field");
        VBox amountSection = createLabeledSection("Amount*:", amountField);

        typeGroup = new ToggleGroup();
        incomeBtn = new RadioButton("Income");
        incomeBtn.setToggleGroup(typeGroup);
        incomeBtn.getStyleClass().add("type-radio");
        incomeBtn.setSelected(originalTransaction.direction() == TransactionDirection.CREDIT);

        expenseBtn = new RadioButton("Expense");
        expenseBtn.setToggleGroup(typeGroup);
        expenseBtn.getStyleClass().add("type-radio");
        expenseBtn.setSelected(originalTransaction.direction() == TransactionDirection.DEBIT);

        HBox typeButtons = new HBox(15);
        typeButtons.getChildren().addAll(incomeBtn, expenseBtn);
        VBox typeSection = createLabeledSection("Type*:", typeButtons);

        descField = new TextArea(originalTransaction.description() != null ? originalTransaction.description() : "");
        descField.setPrefRowCount(3);
        descField.setWrapText(true);
        descField.getStyleClass().add("dialog-field");
        VBox descSection = createLabeledSection("Description:", descField);

        VBox categorySection = createCategoriesSection();

        form.getChildren().addAll(dateSection, amountSection, typeSection, descSection, categorySection);
        return form;
    }

    private VBox createCategoriesSection() {
        VBox section = new VBox(8);
        section.getChildren().addAll(
                createCategoryLabel(),
                createCategoryControls(),
                createSelectedTagsBox()
        );
        return section;
    }

    private Label createCategoryLabel() {
        Label label = new Label("Categories:");
        label.setStyle("-fx-font-weight: bold;");
        return label;
    }

    private HBox createCategoryControls() {
        HBox controls = new HBox(10);
        controls.setAlignment(Pos.CENTER_LEFT);
        controls.getChildren().addAll(
                buildCategoryCombo(),
                buildClearAllTagsButton()
        );
        return controls;
    }

    private VBox createSelectedTagsBox() {
        VBox box = new VBox(8);
        box.getStyleClass().add("selected-tags-container");
        Label title = createSelectedTagsLabel();
        selectedTagsFlow = createSelectedTagsFlow();

        updateSelectedTagsDisplay();

        box.getChildren().addAll(title, selectedTagsFlow);
        return box;
    }

    private Label createSelectedTagsLabel() {
        Label label = new Label("Selected Categories:");
        label.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
        return label;
    }

    private FlowPane createSelectedTagsFlow() {
        FlowPane flow = new FlowPane(8, 8);
        flow.getStyleClass().add("selected-tags-flow");
        return flow;
    }

    private ComboBox<TagDTO> buildCategoryCombo() {
        categoryCombo = new ComboBox<>();
        categoryCombo.setPromptText("Select Category to Add");
        categoryCombo.getItems().addAll(availableTags);
        categoryCombo.setConverter(tagConverter());
        categoryCombo.getStyleClass().add("dialog-field");
        categoryCombo.setOnAction(this::handleCategorySelected);
        return categoryCombo;
    }

    private Button buildClearAllTagsButton() {
        Button btn = new Button("Clear All");
        btn.getStyleClass().add("clear-tags-btn");
        btn.setStyle(
                "-fx-background-color: #f44336; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 11px; " +
                        "-fx-padding: 4 8 4 8; " +
                        "-fx-border-radius: 4px; " +
                        "-fx-background-radius: 4px;"
        );
        btn.setOnAction(e -> clearAllTags());
        return btn;
    }

    private void handleCategorySelected(ActionEvent e) {
        TagDTO selectedTag = categoryCombo.getValue();
        if (selectedTag != null && !selectedTagIds.contains(selectedTag.id())) {
            selectedTagIds.add(selectedTag.id());
            updateSelectedTagsDisplay();
            categoryCombo.setValue(null);
        }
    }

    private void clearAllTags() {
        selectedTagIds.clear();
        updateSelectedTagsDisplay();
    }

    private static StringConverter<TagDTO> tagConverter() {
        return new StringConverter<>() {
            @Override
            public String toString(TagDTO tag) {
                return tag != null ? tag.name() : "";
            }
            @Override
            public TagDTO fromString(String string) {
                return null;
            }
        };
    }

    private void updateSelectedTagsDisplay() {
        selectedTagsFlow.getChildren().clear();

        if (selectedTagIds.isEmpty()) {
            Label noTagsLabel = new Label("No categories selected");
            noTagsLabel.getStyleClass().add("no-tags-label");
            noTagsLabel.setStyle("-fx-text-fill: #888; -fx-font-style: italic;");
            selectedTagsFlow.getChildren().add(noTagsLabel);
        } else {
            for (UUID tagId : selectedTagIds) {
                availableTags.stream()
                        .filter(tag -> tag.id().equals(tagId))
                        .findFirst()
                        .ifPresent(tag -> {
                            HBox tagChip = createTagChip(tag);
                            selectedTagsFlow.getChildren().add(tagChip);
                        });
            }
        }
    }

    private HBox createTagChip(TagDTO tag) {
        HBox tagChip = new HBox(8);
        tagChip.getStyleClass().add("tag-chip");
        tagChip.setAlignment(Pos.CENTER_LEFT);
        tagChip.setStyle(
                "-fx-background-color: #e3f2fd; " +
                        "-fx-border-color: #2196f3; " +
                        "-fx-border-width: 1px; " +
                        "-fx-border-radius: 15px; " +
                        "-fx-background-radius: 15px; " +
                        "-fx-padding: 5 12 5 12;"
        );

        Label tagNameLabel = new Label(tag.name());
        tagNameLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #1976d2;");

        Button removeBtn = new Button("×");
        removeBtn.getStyleClass().add("tag-remove-btn");
        removeBtn.setStyle(
                "-fx-background-color: transparent; " +
                        "-fx-text-fill: #1976d2; " +
                        "-fx-font-size: 14px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 0 4 0 4; " +
                        "-fx-border-width: 0; " +
                        "-fx-cursor: hand;"
        );
        removeBtn.setOnAction(e -> {
            selectedTagIds.remove(tag.id());
            updateSelectedTagsDisplay();
        });

        tagChip.getChildren().addAll(tagNameLabel, removeBtn);
        return tagChip;
    }

    /**
     * Validates the user input before updating the transaction.
     * <p>
     * Ensures that date, amount, and type are provided and valid.
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

        String amountText = amountField.getText();
        if (amountText == null || amountText.trim().isEmpty()) {
            showError("Amount is required.");
            return false;
        }

        try {
            BigDecimal amount = new BigDecimal(amountText.trim());
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                showError("Amount must be greater than zero.");
                return false;
            }
        } catch (NumberFormatException e) {
            showError("Please enter a valid amount.");
            return false;
        }

        if (typeGroup.getSelectedToggle() == null) {
            showError("Please select transaction type (Income or Expense).");
            return false;
        }

        return true;
    }

    /**
     * Creates the updated {@link TransactionDTO} based on the user input
     * and applies the changes through the {@link TransactionController}.
     *
     * @return the updated {@link TransactionDTO}
     * @throws RuntimeException if the update operation fails
     */
    @Override
    protected TransactionDTO createResult() {
        try {
            TransactionDirection direction = incomeBtn.isSelected() ?
                    TransactionDirection.CREDIT : TransactionDirection.DEBIT;

            return transactionController.updateTransaction(
                    currentAccount.id(),
                    originalTransaction.id(),
                    datePicker.getValue().atStartOfDay().atOffset(ZoneOffset.UTC),
                    new BigDecimal(amountField.getText().trim()),
                    direction,
                    descField.getText().trim().isEmpty() ? null : descField.getText().trim(),
                    selectedTagIds
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to update transaction: " + e.getMessage(), e);
        }
    }
}