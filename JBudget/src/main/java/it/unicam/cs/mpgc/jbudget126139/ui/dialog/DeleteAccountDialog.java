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
import it.unicam.cs.mpgc.jbudget126139.controller.RecurrenceController;
import it.unicam.cs.mpgc.jbudget126139.controller.TransactionController;
import it.unicam.cs.mpgc.jbudget126139.model.RecurrenceFrequency;
import it.unicam.cs.mpgc.jbudget126139.model.TransactionDirection;
import it.unicam.cs.mpgc.jbudget126139.service.dto.AccountDTO;
import it.unicam.cs.mpgc.jbudget126139.service.dto.RecurrenceDTO;
import it.unicam.cs.mpgc.jbudget126139.service.dto.TransactionDTO;
import it.unicam.cs.mpgc.jbudget126139.ui.util.FormatUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.beans.property.SimpleStringProperty;
import jakarta.persistence.EntityManagerFactory;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Dialog that guides the user through the deletion of an account.
 * <p>
 * This dialog:
 * <ul>
 *   <li>Analyzes the impact of deleting an account, including its transactions and recurrences.</li>
 *   <li>Displays warnings and optional detailed breakdowns before deletion.</li>
 *   <li>Requests final confirmation by requiring the account name to be typed.</li>
 *   <li>Delegates deletion of transactions, recurrences, and the account itself to the corresponding controllers.</li>
 * </ul>
 * </p>
 */
public class DeleteAccountDialog {

    private final AccountDTO account;
    private final List<AccountDTO> allAccounts;
    private final TransactionController transactionController;
    private final RecurrenceController recurrenceController;
    private final AccountController accountController;
    private final EntityManagerFactory emf;

    /**
     * Creates a new {@code DeleteAccountDialog}.
     *
     * @param account              the account to be deleted
     * @param allAccounts          the list of all existing accounts
     * @param transactionController the controller managing transactions
     * @param recurrenceController  the controller managing recurrences
     * @param accountController     the controller managing accounts
     * @param emf                   the entity manager factory (used internally if needed)
     */
    public DeleteAccountDialog(AccountDTO account,
                               List<AccountDTO> allAccounts,
                               TransactionController transactionController,
                               RecurrenceController recurrenceController,
                               AccountController accountController,
                               EntityManagerFactory emf) {
        this.account = account;
        this.allAccounts = allAccounts;
        this.transactionController = transactionController;
        this.recurrenceController = recurrenceController;
        this.accountController = accountController;
        this.emf = emf;
    }

    /**
     * Shows the deletion dialog and executes the account deletion process
     * if the user confirms through all steps.
     *
     * @return {@code true} if the account was successfully deleted,
     *         {@code false} if the process was canceled or failed
     */
    public boolean showAndWait() {
        try {
            List<TransactionDTO> accountTransactions = transactionController.listTransactions(account.id());
            List<RecurrenceDTO> accountRecurrences = getAccountRecurrences();

            if (allAccounts.size() <= 1) {
                showErrorAlert("Cannot delete the last account. You must have at least one account.");
                return false;
            }

            if (!showDetailAlert(accountTransactions, accountRecurrences)) {
                return false;
            }

            boolean shouldProceed = showAccountDeletionDetails(accountTransactions, accountRecurrences);
            if (!shouldProceed) {
                return false;
            }

            if (!showFinalDeletionConfirmation()) {
                return false;
            }

            executeAccountDeletion(accountTransactions, accountRecurrences);
            return true;

        } catch (Exception ex) {
            showErrorAlert("Error during account deletion analysis: " + ex.getMessage());
            return false;
        }
    }

    private List<RecurrenceDTO> getAccountRecurrences() {
        try {
            List<TransactionDTO> accountTransactions = transactionController.listTransactions(account.id());

            long recurrenceCount = accountTransactions.stream()
                    .map(TransactionDTO::recurrenceId)
                    .filter(Objects::nonNull)
                    .distinct()
                    .count();

            List<RecurrenceDTO> placeholders = new ArrayList<>();
            for (int i = 0; i < recurrenceCount; i++) {
                RecurrenceDTO placeholder = new RecurrenceDTO(
                        UUID.randomUUID(),
                        OffsetDateTime.now(),
                        account.id(),
                        OffsetDateTime.now(),
                        null,
                        RecurrenceFrequency.MONTHLY
                );
                placeholders.add(placeholder);
            }

            return placeholders;

        } catch (Exception e) {
            return List.of();
        }
    }

    private boolean showDetailAlert(List<TransactionDTO> accountTransactions, List<RecurrenceDTO> accountRecurrences) {
        Alert detailAlert = new Alert(Alert.AlertType.WARNING);
        detailAlert.setTitle("Delete AbstractAccount - Impact Analysis");
        detailAlert.setHeaderText("AbstractAccount Deletion Impact");

        StringBuilder contentBuilder = new StringBuilder();
        contentBuilder.append(String.format("AbstractAccount: \"%s\"\n\n", account.name()));
        contentBuilder.append("This operation will permanently delete:\n");
        contentBuilder.append(String.format("• %d transaction(s)\n", accountTransactions.size()));
        contentBuilder.append(String.format("• %d recurring transaction pattern(s)\n", accountRecurrences.size()));

        if (!accountTransactions.isEmpty()) {
            BigDecimal totalAmount = accountTransactions.stream()
                    .map(t -> t.direction() == TransactionDirection.CREDIT ? t.amount() : t.amount().negate())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            contentBuilder.append(String.format("• Current balance: %s\n", FormatUtils.formatCurrency(totalAmount, account.currency())));
        }

        contentBuilder.append("\nThis action cannot be undone.\n");
        contentBuilder.append("Do you want to see the detailed breakdown before proceeding?");

        detailAlert.setContentText(contentBuilder.toString());

        ButtonType showDetailsBtn = new ButtonType("Show Details");
        ButtonType proceedBtn = new ButtonType("Proceed Anyway");
        ButtonType cancelBtn = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        detailAlert.getButtonTypes().setAll(showDetailsBtn, proceedBtn, cancelBtn);

        Optional<ButtonType> detailResult = detailAlert.showAndWait();

        if (detailResult.isEmpty() || detailResult.get() == cancelBtn) {
            return false;
        }

        return detailResult.get() == proceedBtn || detailResult.get() == showDetailsBtn;
    }

    private boolean showAccountDeletionDetails(List<TransactionDTO> transactions, List<RecurrenceDTO> recurrences) {
        Stage detailStage = new Stage();
        detailStage.initModality(Modality.APPLICATION_MODAL);
        detailStage.setTitle("AbstractAccount Deletion Details");
        detailStage.setWidth(700);
        detailStage.setHeight(600);

        VBox content = new VBox(20);
        content.setPadding(new Insets(30));

        Label title = new Label("Detailed Impact Analysis");
        title.getStyleClass().add("dialog-title");

        TabPane tabPane = new TabPane();

        Tab transactionsTab = new Tab("Transactions (" + transactions.size() + ")");
        transactionsTab.setClosable(false);

        if (transactions.isEmpty()) {
            Label noTransactions = new Label("No transactions to delete");
            noTransactions.getStyleClass().add("empty-message");
            transactionsTab.setContent(noTransactions);
        } else {
            TableView<TransactionDTO> transactionTable = createTransactionTable(transactions);
            transactionsTab.setContent(transactionTable);
        }

        Tab recurrencesTab = new Tab("Recurring Patterns (" + recurrences.size() + ")");
        recurrencesTab.setClosable(false);

        if (recurrences.isEmpty()) {
            Label noRecurrences = new Label("No recurring patterns to delete");
            noRecurrences.getStyleClass().add("empty-message");
            recurrencesTab.setContent(noRecurrences);
        } else {
            VBox recurrencesList = new VBox(10);
            for (RecurrenceDTO recurrence : recurrences) {
                Label recurrenceLabel = new Label(String.format("%s - %s",
                        recurrence.frequency().toString(),
                        recurrence.startDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))));
                recurrencesList.getChildren().add(recurrenceLabel);
            }
            ScrollPane recurrencesScroll = new ScrollPane(recurrencesList);
            recurrencesTab.setContent(recurrencesScroll);
        }

        tabPane.getTabs().addAll(transactionsTab, recurrencesTab);

        HBox buttons = new HBox(20);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        Button cancelBtn = new Button("Cancel Deletion");
        cancelBtn.getStyleClass().add("dialog-cancel-btn");

        Button proceedBtn = new Button("Proceed with Deletion");
        proceedBtn.getStyleClass().add("dialog-delete-btn");

        final boolean[] shouldProceed = {false};

        cancelBtn.setOnAction(e -> {
            shouldProceed[0] = false;
            detailStage.close();
        });

        proceedBtn.setOnAction(e -> {
            shouldProceed[0] = true;
            detailStage.close();
        });

        buttons.getChildren().addAll(cancelBtn, proceedBtn);
        content.getChildren().addAll(title, tabPane, buttons);

        Scene scene = new Scene(content);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        detailStage.setScene(scene);
        detailStage.showAndWait();

        return shouldProceed[0];
    }

    private TableView<TransactionDTO> createTransactionTable(List<TransactionDTO> transactions) {
        TableView<TransactionDTO> table = new TableView<>();
        table.getItems().addAll(transactions);

        TableColumn<TransactionDTO, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().valueDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))));

        TableColumn<TransactionDTO, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<TransactionDTO, String> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(data -> new SimpleStringProperty(
                (data.getValue().direction() == TransactionDirection.CREDIT ? "+" : "-") +
                        FormatUtils.formatCurrency(data.getValue().amount(), account.currency())));

        table.getColumns().addAll(dateCol, descCol, amountCol);
        return table;
    }

    private boolean showFinalDeletionConfirmation() {
        Alert finalAlert = new Alert(Alert.AlertType.CONFIRMATION);
        finalAlert.setTitle("Final Confirmation Required");
        finalAlert.setHeaderText("Type AbstractAccount Name to Confirm");

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        Label warningLabel = new Label("This is your last chance to cancel this irreversible action.");
        warningLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #d32f2f;");

        Label instructionLabel = new Label(String.format(
                "Type the exact account name \"%s\" to confirm deletion:", account.name()));

        TextField confirmationField = new TextField();
        confirmationField.setPromptText("Enter account name here");

        content.getChildren().addAll(warningLabel, instructionLabel, confirmationField);
        finalAlert.getDialogPane().setContent(content);

        ButtonType confirmBtn = new ButtonType("DELETE ACCOUNT", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelBtn = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        finalAlert.getButtonTypes().setAll(confirmBtn, cancelBtn);

        Button deleteButton = (Button) finalAlert.getDialogPane().lookupButton(confirmBtn);
        deleteButton.setDisable(true);

        confirmationField.textProperty().addListener((obs, oldText, newText) -> {
            deleteButton.setDisable(!account.name().equals(newText));
        });

        Optional<ButtonType> result = finalAlert.showAndWait();
        return result.isPresent() && result.get() == confirmBtn &&
                account.name().equals(confirmationField.getText());
    }

    private void executeAccountDeletion(List<TransactionDTO> accountTransactions, List<RecurrenceDTO> accountRecurrences) {
        try {
            for (RecurrenceDTO recurrence : accountRecurrences) {
                try {
                    recurrenceController.deleteRecurrence(recurrence.id());
                } catch (Exception e) {
                    System.err.println("Warning: Could not delete recurrence " + recurrence.id() + ": " + e.getMessage());
                }
            }

            for (TransactionDTO transaction : accountTransactions) {
                try {
                    transactionController.deleteTransaction(account.id(), transaction.id());
                } catch (Exception e) {
                    System.err.println("Warning: Could not delete transaction " + transaction.id() + ": " + e.getMessage());
                }
            }

            accountController.deleteAccount(account.id());

        } catch (Exception ex) {
            throw new RuntimeException("Failed to delete account: " + ex.getMessage(), ex);
        }
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}