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
 * Dialog for creating a new account.
 * <p>
 * Provides a form with fields for account name, currency, and optional description.
 * On confirmation, it uses the {@link AccountController} to persist the new account.
 * </p>
 */
public class AddAccountDialog extends BaseDialog<AccountDTO> {

    private final AccountController accountController;

    private TextField nameField;
    private ComboBox<String> currencyCombo;
    private TextArea descArea;

    /**
     * Creates a new {@code AddAccountDialog}.
     *
     * @param accountController the controller responsible for managing accounts
     */
    public AddAccountDialog(AccountController accountController) {
        super("Add New AbstractAccount", 600, 500);
        this.accountController = accountController;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getDialogTitle() {
        return "Create New AbstractAccount";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getSaveButtonText() {
        return "Create AbstractAccount";
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
     * </p>
     *
     * @return a {@link VBox} containing the form layout
     */
    @Override
    protected VBox createFormContent() {
        VBox form = new VBox(20);

        nameField = new TextField();
        nameField.setPromptText("Enter account name");
        nameField.getStyleClass().add("dialog-field");
        VBox nameSection = createLabeledSection("AbstractAccount Name*:", nameField);

        currencyCombo = new ComboBox<>();
        currencyCombo.getItems().addAll("USD", "EUR", "GBP", "JPY", "CAD", "AUD", "CHF", "KES");
        currencyCombo.setValue("USD");
        currencyCombo.getStyleClass().add("dialog-field");
        VBox currencySection = createLabeledSection("Currency*:", currencyCombo);

        descArea = new TextArea();
        descArea.setPromptText("AbstractAccount description (optional)");
        descArea.setPrefRowCount(4);
        descArea.setWrapText(true);
        descArea.getStyleClass().add("dialog-field");
        VBox descSection = createLabeledSection("Description:", descArea);

        form.getChildren().addAll(nameSection, currencySection, descSection);
        return form;
    }

    /**
     * Validates the input provided by the user.
     *
     * @return {@code true} if all required fields are valid, {@code false} otherwise
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
     * Creates a new {@link AccountDTO} from the dialog input.
     * <p>
     * If description is left empty, it will be set to {@code null}.
     * </p>
     *
     * @return the created {@link AccountDTO}
     * @throws RuntimeException if account creation fails
     */
    @Override
    protected AccountDTO createResult() {
        try {
            return accountController.createAccount(
                    nameField.getText().trim(),
                    currencyCombo.getValue(),
                    descArea.getText().trim().isEmpty() ? null : descArea.getText().trim()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to create account: " + e.getMessage(), e);
        }
    }
}