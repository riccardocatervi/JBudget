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

import it.unicam.cs.mpgc.jbudget126139.controller.AccountController;
import it.unicam.cs.mpgc.jbudget126139.service.dto.AccountDTO;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/**
 * Dialog for editing an existing account.
 * <p>
 * Provides a form pre-filled with the account's current details (name, currency, and description),
 * and allows the user to update them. On confirmation, the account is updated
 * through the {@link AccountController}.
 * </p>
 */
public class EditAccountDialog extends BaseDialog<AccountDTO> {

    private final AccountController accountController;
    private final AccountDTO originalAccount;

    private TextField nameField;
    private ComboBox<String> currencyCombo;
    private TextArea descArea;

    /**
     * Creates a new {@code EditAccountDialog}.
     *
     * @param account           the account to be edited
     * @param accountController the controller responsible for managing accounts
     */
    public EditAccountDialog(AccountDTO account, AccountController accountController) {
        super("Edit AbstractAccount", 600, 500);
        this.originalAccount = account;
        this.accountController = accountController;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getDialogTitle() {
        return "Edit AbstractAccount";
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
     * The form includes:
     * <ul>
     *   <li>AbstractAccount name (required)</li>
     *   <li>Currency selection (required)</li>
     *   <li>Description (optional)</li>
     * </ul>
     * Pre-fills the fields with the account’s current values.
     * </p>
     *
     * @return a {@link VBox} containing the form layout
     */
    @Override
    protected VBox createFormContent() {
        VBox form = new VBox(20);

        nameField = new TextField(originalAccount.name());
        nameField.getStyleClass().add("dialog-field");
        VBox nameSection = createLabeledSection("AbstractAccount Name*:", nameField);

        currencyCombo = new ComboBox<>();
        currencyCombo.getItems().addAll("USD", "EUR", "GBP", "JPY", "CAD", "AUD", "CHF", "KES");
        currencyCombo.setValue(originalAccount.currency().toString());
        currencyCombo.getStyleClass().add("dialog-field");
        VBox currencySection = createLabeledSection("Currency*:", currencyCombo);

        descArea = new TextArea();
        String accountDescription = originalAccount.description();
        if (accountDescription != null) {
            descArea.setText(accountDescription);
        } else {
            descArea.setText("");
        }
        descArea.setPrefRowCount(4);
        descArea.setWrapText(true);
        descArea.getStyleClass().add("dialog-field");
        VBox descSection = createLabeledSection("Description:", descArea);

        form.getChildren().addAll(nameSection, currencySection, descSection);
        return form;
    }

    /**
     * Validates the user input before updating the account.
     * <p>
     * Ensures that both name and currency are provided and not empty.
     * </p>
     *
     * @return {@code true} if the input is valid, {@code false} otherwise
     */
    @Override
    protected boolean validateInput() {
        String name = nameField.getText();
        String currency = currencyCombo.getValue();

        if (name == null || name.trim().isEmpty()) {
            showError("AbstractAccount name is required.");
            return false;
        }
        if (currency == null || currency.trim().isEmpty()) {
            showError("Currency is required.");
            return false;
        }
        return true;
    }

    /**
     * Creates the updated {@link AccountDTO} based on the user input
     * and applies the changes through the {@link AccountController}.
     *
     * @return the updated {@link AccountDTO}
     * @throws RuntimeException if the update operation fails
     */
    @Override
    protected AccountDTO createResult() {
        try {
            String description = descArea.getText();
            String finalDescription = (description == null || description.trim().isEmpty()) ? null : description.trim();

            return accountController.updateAccount(
                    originalAccount.id(),
                    nameField.getText().trim(),
                    currencyCombo.getValue(),
                    finalDescription
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to update account: " + e.getMessage(), e);
        }
    }
}