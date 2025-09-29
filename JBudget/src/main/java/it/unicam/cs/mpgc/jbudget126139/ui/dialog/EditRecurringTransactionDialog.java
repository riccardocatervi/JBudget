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

import it.unicam.cs.mpgc.jbudget126139.controller.RecurrenceController;
import it.unicam.cs.mpgc.jbudget126139.controller.TransactionController;
import it.unicam.cs.mpgc.jbudget126139.model.RecurrenceFrequency;
import it.unicam.cs.mpgc.jbudget126139.model.TransactionDirection;
import it.unicam.cs.mpgc.jbudget126139.service.RecurrenceService;
import it.unicam.cs.mpgc.jbudget126139.service.dto.RecurrenceDTO;
import it.unicam.cs.mpgc.jbudget126139.service.dto.TransactionDTO;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

/**
 * Dialog for editing an existing recurring transaction.
 * <p>
 * Provides a form pre-filled with the details of the recurrence
 * (description, amount, type, frequency, start date, and end date).
 * On confirmation, the old recurrence is removed and replaced with a new one,
 * keeping past transactions unchanged but applying updates to all future occurrences.
 * </p>
 */
public class EditRecurringTransactionDialog extends BaseDialog<RecurrenceDTO> {

    private final RecurrenceDTO originalRecurrence;
    private final TransactionController transactionController;
    private final RecurrenceController recurrenceController;
    private final RecurrenceService recurrenceService;

    private TextField descField;
    private TextField amountField;
    private ToggleGroup typeGroup;
    private RadioButton incomeBtn;
    private RadioButton expenseBtn;
    private ComboBox<RecurrenceFrequency> frequencyCombo;
    private DatePicker startDate;
    private DatePicker endDate;
    private CheckBox noEndDateCheck;

    /**
     * Creates a new {@code EditRecurringTransactionDialog}.
     *
     * @param recurrence           the recurrence to be edited
     * @param transactionController the controller managing transactions
     * @param recurrenceController  the controller managing recurrences
     * @param recurrenceService     the service responsible for recurrence logic
     */
    public EditRecurringTransactionDialog(RecurrenceDTO recurrence,
                                          TransactionController transactionController,
                                          RecurrenceController recurrenceController,
                                          RecurrenceService recurrenceService) {
        super("Edit Recurring Transaction", 650, 750);
        this.originalRecurrence = recurrence;
        this.transactionController = transactionController;
        this.recurrenceController = recurrenceController;
        this.recurrenceService = recurrenceService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getDialogTitle() {
        return "Edit Recurring Transaction";
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
     * Pre-fills the form fields using the first transaction of the recurrence,
     * along with the recurrence pattern details.
     * </p>
     *
     * @return a {@link VBox} containing the form layout
     */
    @Override
    protected VBox createFormContent() {
        VBox form = new VBox(20);

        TransactionDTO firstTransaction = getFirstTransaction();

        descField = new TextField();
        if (firstTransaction != null && firstTransaction.description() != null) {
            descField.setText(firstTransaction.description());
        }
        descField.getStyleClass().add("dialog-field");
        VBox descSection = createLabeledSection("Description*:", descField);

        amountField = new TextField();
        if (firstTransaction != null) {
            amountField.setText(firstTransaction.amount().toString());
        }
        amountField.getStyleClass().add("dialog-field");
        VBox amountSection = createLabeledSection("Amount*:", amountField);

        typeGroup = new ToggleGroup();
        incomeBtn = new RadioButton("Income");
        incomeBtn.setToggleGroup(typeGroup);
        incomeBtn.getStyleClass().add("type-radio");

        expenseBtn = new RadioButton("Expense");
        expenseBtn.setToggleGroup(typeGroup);
        expenseBtn.getStyleClass().add("type-radio");

        if (firstTransaction != null) {
            incomeBtn.setSelected(firstTransaction.direction() == TransactionDirection.CREDIT);
            expenseBtn.setSelected(firstTransaction.direction() == TransactionDirection.DEBIT);
        }

        HBox typeButtons = new HBox(15);
        typeButtons.getChildren().addAll(incomeBtn, expenseBtn);
        VBox typeSection = createLabeledSection("Type*:", typeButtons);

        frequencyCombo = new ComboBox<>();
        frequencyCombo.getItems().addAll(RecurrenceFrequency.values());
        frequencyCombo.setValue(originalRecurrence.frequency());
        frequencyCombo.getStyleClass().add("dialog-field");
        VBox frequencySection = createLabeledSection("Frequency*:", frequencyCombo);

        startDate = new DatePicker(originalRecurrence.startDate().toLocalDate());
        startDate.getStyleClass().add("dialog-field");
        VBox startDateSection = createLabeledSection("Start Date*:", startDate);

        VBox endDateSection = createEndDateSection();

        form.getChildren().addAll(descSection, amountSection, typeSection,
                frequencySection, startDateSection, endDateSection);
        return form;
    }

    private TransactionDTO getFirstTransaction() {
        try {
            List<TransactionDTO> transactions = transactionController.listByRecurrence(originalRecurrence.id());
            return transactions.isEmpty() ? null : transactions.get(0);
        } catch (Exception e) {
            return null;
        }
    }

    private VBox createEndDateSection() {
        VBox endDateSection = new VBox(8);

        Label endDateLabel = new Label("End Date:");
        endDateLabel.setStyle("-fx-font-weight: bold;");

        boolean hasEndDate = originalRecurrence.endDate() != null;
        LocalDate initialEndDate = hasEndDate ? originalRecurrence.endDate().toLocalDate() : LocalDate.now().plusYears(1);

        endDate = new DatePicker(initialEndDate);
        endDate.getStyleClass().add("dialog-field");
        endDate.setDisable(!hasEndDate);

        noEndDateCheck = new CheckBox("No end date");
        noEndDateCheck.setSelected(!hasEndDate);

        noEndDateCheck.setOnAction(e -> {
            boolean noEndDate = noEndDateCheck.isSelected();
            endDate.setDisable(noEndDate);
            if (noEndDate) {
                endDate.setValue(null);
            } else {
                endDate.setValue(LocalDate.now().plusYears(1));
            }
        });

        endDateSection.getChildren().addAll(endDateLabel, endDate, noEndDateCheck);
        return endDateSection;
    }

    /**
     * Validates the user input before updating the recurrence.
     * <p>
     * Ensures that description, amount, type, frequency, and start date are valid,
     * and checks that the start date is not after the end date.
     * </p>
     *
     * @return {@code true} if the input is valid, {@code false} otherwise
     */
    @Override
    protected boolean validateInput() {
        String description = descField.getText();
        if (description == null || description.trim().isEmpty()) {
            showError("Description is required.");
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

        if (startDate.getValue() == null) {
            showError("Start date is required.");
            return false;
        }

        LocalDate finalEndDate = noEndDateCheck.isSelected() ? null : endDate.getValue();
        if (finalEndDate != null && startDate.getValue().isAfter(finalEndDate)) {
            showError("Start date must be before or equal to end date.");
            return false;
        }

        if (frequencyCombo.getValue() == null) {
            showError("Frequency is required.");
            return false;
        }

        return true;
    }

    /**
     * Updates the recurrence with the new user input.
     * <p>
     * The old recurrence is deleted, and a new one is created
     * with the updated values. A confirmation prompt is shown
     * before applying the changes.
     * </p>
     *
     * @return the updated {@link RecurrenceDTO}, or {@code null} if the user cancels
     * @throws RuntimeException if the update fails
     */
    @Override
    protected RecurrenceDTO createResult() {
        try {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirm Changes");
            confirmAlert.setHeaderText("Update Recurring Transaction");
            confirmAlert.setContentText("This will update the recurring pattern and all future transactions. Existing transactions will not be modified. Continue?");

            Optional<ButtonType> confirmResult = confirmAlert.showAndWait();
            if (confirmResult.isEmpty() || confirmResult.get() != ButtonType.OK)
                return null;

            LocalDate finalEndDate = noEndDateCheck.isSelected() ? null : endDate.getValue();

            recurrenceController.deleteRecurrence(originalRecurrence.id());

            return recurrenceService.createRecurrence(
                    originalRecurrence.accountId(),
                    startDate.getValue().atStartOfDay().atOffset(ZoneOffset.UTC),
                    finalEndDate != null ? finalEndDate.atTime(23, 59, 59).atOffset(ZoneOffset.UTC) : null,
                    frequencyCombo.getValue()
            );

        } catch (Exception e) {
            throw new RuntimeException("Failed to update recurring transaction: " + e.getMessage(), e);
        }
    }
}