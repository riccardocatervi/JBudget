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
import it.unicam.cs.mpgc.jbudget126139.model.RecurrenceFrequency;
import it.unicam.cs.mpgc.jbudget126139.model.TransactionDirection;
import it.unicam.cs.mpgc.jbudget126139.service.RecurrenceService;
import it.unicam.cs.mpgc.jbudget126139.service.dto.AccountDTO;
import it.unicam.cs.mpgc.jbudget126139.service.dto.RecurrenceDTO;
import it.unicam.cs.mpgc.jbudget126139.service.dto.TagDTO;
import it.unicam.cs.mpgc.jbudget126139.service.dto.TransactionDTO;
import it.unicam.cs.mpgc.jbudget126139.ui.event.TransactionChangedEvent;
import it.unicam.cs.mpgc.jbudget126139.ui.event.TransactionEvent;
import it.unicam.cs.mpgc.jbudget126139.ui.event.UIEventBus;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Dialog for creating a new recurring transaction.
 * <p>
 * Provides a form with fields for description, amount, type (income/expense),
 * category, recurrence frequency, start date, and optional end date.
 * On confirmation, a new {@link RecurrenceDTO} is created through the
 * {@link RecurrenceService}, and transactions up to the current date
 * are generated via the {@link TransactionController}.
 * </p>
 */
public class AddRecurringTransactionDialog extends BaseDialog<RecurrenceDTO> {

    private final AccountDTO currentAccount;
    private final List<TagDTO> availableTags;
    private final RecurrenceService recurrenceService;
    private final TransactionController transactionController;
    private final UIEventBus eventBus;

    private TextField descField;
    private TextField amountField;
    private ToggleGroup typeGroup;
    private RadioButton incomeBtn;
    private RadioButton expenseBtn;
    private ComboBox<TagDTO> categoryCombo;
    private ComboBox<RecurrenceFrequency> frequencyCombo;
    private DatePicker startDate;
    private DatePicker endDate;
    private CheckBox noEndDateCheck;

    /**
     * Creates a new {@code AddRecurringTransactionDialog}.
     *
     * @param currentAccount        the account to which the recurring transaction belongs
     * @param availableTags         the list of available categories/tags
     * @param recurrenceService     the service responsible for recurrence logic
     * @param transactionController the controller for managing transactions
     */
    public AddRecurringTransactionDialog(AccountDTO currentAccount, List<TagDTO> availableTags,
                                         RecurrenceService recurrenceService,
                                         TransactionController transactionController,
                                         UIEventBus eventBus) {
        super("Add Recurring Transaction", 650, 750);
        this.currentAccount = currentAccount;
        this.availableTags = availableTags;
        this.recurrenceService = recurrenceService;
        this.transactionController = transactionController;
        this.eventBus = eventBus;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getDialogTitle() {
        return "Create Recurring Transaction";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getSaveButtonText() {
        return "Create Recurring";
    }

    /**
     * Builds the form content for this dialog.
     * <p>
     * Includes description, amount, type, category, frequency,
     * start date, and end date.
     * </p>
     *
     * @return a {@link VBox} containing the form layout
     */
    @Override
    protected VBox createFormContent() {
        VBox form = new VBox(20);
        form.getChildren().addAll(
                buildDescriptionSection(),
                buildAmountSection(),
                buildTypeSection(),
                buildCategorySection(),
                buildFrequencySection(),
                buildStartDateSection(),
                createEndDateSection()
        );
        return form;
    }

    private VBox buildDescriptionSection() {
        descField = new TextField();
        descField.setPromptText("Enter description (e.g., 'Monthly rent', 'Weekly groceries')");
        descField.getStyleClass().add("dialog-field");
        return createLabeledSection("Description*:", descField);
    }

    private VBox buildAmountSection() {
        amountField = new TextField();
        amountField.setPromptText("0.00");
        amountField.getStyleClass().add("dialog-field");
        return createLabeledSection("Amount*:", amountField);
    }

    private VBox buildTypeSection() {
        typeGroup = new ToggleGroup();

        incomeBtn = new RadioButton("Income");
        incomeBtn.setToggleGroup(typeGroup);
        incomeBtn.getStyleClass().add("type-radio");

        expenseBtn = new RadioButton("Expense");
        expenseBtn.setToggleGroup(typeGroup);
        expenseBtn.getStyleClass().add("type-radio");
        expenseBtn.setSelected(true);

        HBox typeButtons = new HBox(15);
        typeButtons.getChildren().addAll(incomeBtn, expenseBtn);

        return createLabeledSection("Type*:", typeButtons);
    }

    private VBox buildCategorySection() {
        categoryCombo = new ComboBox<>();
        categoryCombo.setPromptText("Select Category (Optional)");
        categoryCombo.getItems().addAll(availableTags);
        categoryCombo.setConverter(tagConverter());
        categoryCombo.getStyleClass().add("dialog-field");
        return createLabeledSection("Category:", categoryCombo);
    }

    private VBox buildFrequencySection() {
        frequencyCombo = new ComboBox<>();
        frequencyCombo.getItems().addAll(RecurrenceFrequency.values());
        frequencyCombo.setValue(RecurrenceFrequency.MONTHLY);
        frequencyCombo.getStyleClass().add("dialog-field");
        return createLabeledSection("Frequency*:", frequencyCombo);
    }

    private VBox buildStartDateSection() {
        startDate = new DatePicker(LocalDate.now());
        startDate.getStyleClass().add("dialog-field");
        return createLabeledSection("Start Date*:", startDate);
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

    private VBox createEndDateSection() {
        VBox endDateSection = new VBox(8);

        Label endDateLabel = new Label("End Date:");
        endDateLabel.setStyle("-fx-font-weight: bold;");

        endDate = new DatePicker(LocalDate.now().plusYears(1));
        endDate.getStyleClass().add("dialog-field");

        noEndDateCheck = new CheckBox("No end date");
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
     * Validates the user input before creating the recurrence.
     * <p>
     * Ensures that description, amount, type, frequency, and start date are valid,
     * and checks that the start date is not after the end date.
     * </p>
     *
     * @return {@code true} if the input is valid, {@code false} otherwise
     */
    @Override
    protected boolean validateInput() {
        return  validateDescription()
                && validateAmount()
                && validateTypeSelected()
                && validateStartDatePresent()
                && validateDateOrder()
                && validateFrequencySelected();
    }

    private boolean validateDescription() {
        String description = descField.getText();
        if (description == null || description.trim().isEmpty()) {
            showError("Description is required.");
            return false;
        }
        return true;
    }

    private boolean validateAmount() {
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
        } catch (NumberFormatException ex) {
            showError("Please enter a valid amount.");
            return false;
        }
        return true;
    }

    private boolean validateTypeSelected() {
        if (typeGroup.getSelectedToggle() == null) {
            showError("Please select transaction type (Income or Expense).");
            return false;
        }
        return true;
    }

    private boolean validateStartDatePresent() {
        if (startDate.getValue() == null) {
            showError("Start date is required.");
            return false;
        }
        return true;
    }

    private boolean validateDateOrder() {
        LocalDate s = startDate.getValue();
        LocalDate e = getFinalEndDate();
        if (e != null && s.isAfter(e)) {
            showError("Start date must be before or equal to end date.");
            return false;
        }
        return true;
    }

    private boolean validateFrequencySelected() {
        if (frequencyCombo.getValue() == null) {
            showError("Frequency is required.");
            return false;
        }
        return true;
    }

    private LocalDate getFinalEndDate() {
        return noEndDateCheck.isSelected() ? null : endDate.getValue();
    }

    /**
     * Creates the new {@link RecurrenceDTO} based on user input
     * and generates transactions up to the current date.
     *
     * @return the created {@link RecurrenceDTO}
     * @throws RuntimeException if the recurrence or transaction creation fails
     */
    @Override
    protected RecurrenceDTO createResult() {
        try {
            TransactionDirection direction = determineDirection();
            Set<UUID> tagIds = collectSelectedTagIds();
            LocalDate finalEnd = computeFinalEndDate();

            OffsetDateTime startOdt = toStartOfDayUtc(startDate.getValue());
            OffsetDateTime endOdt   = toEndOfDayUtc(finalEnd);
            RecurrenceFrequency freq = frequencyCombo.getValue();

            int expected = computeExpectedTransactions(startOdt, endOdt, freq);

            return createRecurringTransactionWithTemplate(
                    currentAccount.id(), startOdt, endOdt, freq,
                    parseAmount(), direction, trimDescription(), tagIds
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to create recurring transaction: " + e.getMessage(), e);
        }
    }

    private TransactionDirection determineDirection() {
        return incomeBtn.isSelected()
                ? TransactionDirection.CREDIT
                : TransactionDirection.DEBIT;
    }

    private Set<UUID> collectSelectedTagIds() {
        Set<UUID> ids = new HashSet<>();
        if (categoryCombo.getValue() != null) {
            ids.add(categoryCombo.getValue().id());
        }
        return ids;
    }

    private LocalDate computeFinalEndDate() {
        return noEndDateCheck.isSelected() ? null : endDate.getValue();
    }

    private OffsetDateTime toStartOfDayUtc(LocalDate date) {
        return date.atStartOfDay().atOffset(ZoneOffset.UTC);
    }

    private OffsetDateTime toEndOfDayUtc(LocalDate date) {
        if (date == null) return null;
        return date.atTime(23, 59, 59).atOffset(ZoneOffset.UTC);
    }

    private BigDecimal parseAmount() {
        return new BigDecimal(amountField.getText().trim());
    }

    private String trimDescription() {
        return descField.getText().trim();
    }

    private int computeExpectedTransactions(OffsetDateTime start,
                                            OffsetDateTime end,
                                            RecurrenceFrequency freq) {
        return calculateExpectedTransactions(start, end, freq);
    }

    private int calculateExpectedTransactions(OffsetDateTime startDate, OffsetDateTime endDate,
                                              RecurrenceFrequency frequency) {
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime effectiveEndDate = (endDate != null && endDate.isBefore(now)) ? endDate : now;

        if (startDate.isAfter(effectiveEndDate))
            return 0;

        int count = 0;
        OffsetDateTime currentDate = startDate;
        final int MAX_COUNT = 500;

        while (!currentDate.isAfter(effectiveEndDate) && count < MAX_COUNT) {
            count++;
            currentDate = calculateNextOccurrence(currentDate, frequency);
        }

        return count;
    }

    private OffsetDateTime calculateNextOccurrence(OffsetDateTime currentDate, RecurrenceFrequency frequency) {
        return switch (frequency) {
            case DAILY -> currentDate.plusDays(1);
            case WEEKLY -> currentDate.plusWeeks(1);
            case MONTHLY -> currentDate.plusMonths(1);
            case YEARLY -> currentDate.plusYears(1);
        };
    }

    private RecurrenceDTO createRecurringTransactionWithTemplate(UUID accountId, OffsetDateTime startDate,
                                                                 OffsetDateTime endDate, RecurrenceFrequency frequency,
                                                                 BigDecimal amount, TransactionDirection direction,
                                                                 String description, Set<UUID> tagIds) {
        try {
            RecurrenceDTO recurrence = recurrenceService.createRecurrence(accountId, startDate, endDate, frequency);

            OffsetDateTime now = OffsetDateTime.now();
            OffsetDateTime currentDate = startDate;
            OffsetDateTime effectiveEndDate = (endDate != null && endDate.isBefore(now)) ? endDate : now;

            int transactionCount = 0;
            final int MAX_TRANSACTIONS = 2000;

            while (!currentDate.isAfter(effectiveEndDate) && transactionCount < MAX_TRANSACTIONS) {
                TransactionDTO createdTransaction = transactionController.createTransactionWithRecurrence(
                        accountId, currentDate, amount, direction, description, tagIds, recurrence.id()
                );

                eventBus.publish(new TransactionEvent(createdTransaction, TransactionEvent.TransactionAction.CREATED));
                eventBus.publish(new TransactionChangedEvent(
                        createdTransaction,
                        TransactionChangedEvent.TransactionAction.ADDED,
                        accountId
                ));

                transactionCount++;
                currentDate = calculateNextOccurrence(currentDate, frequency);
            }

            return recurrence;

        } catch (Exception e) {
            throw new RuntimeException("Failed to create recurring transaction: " + e.getMessage(), e);
        }
    }
}