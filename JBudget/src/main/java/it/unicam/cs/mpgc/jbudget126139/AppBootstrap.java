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

package it.unicam.cs.mpgc.jbudget126139;

import it.unicam.cs.mpgc.jbudget126139.controller.*;
import it.unicam.cs.mpgc.jbudget126139.controller.impl.AccountControllerImpl;
import it.unicam.cs.mpgc.jbudget126139.controller.impl.RecurrenceControllerImpl;
import it.unicam.cs.mpgc.jbudget126139.controller.impl.TagControllerImpl;
import it.unicam.cs.mpgc.jbudget126139.controller.impl.TransactionControllerImpl;
import it.unicam.cs.mpgc.jbudget126139.model.TransactionDirection;
import it.unicam.cs.mpgc.jbudget126139.service.*;
import it.unicam.cs.mpgc.jbudget126139.service.dto.AccountDTO;
import it.unicam.cs.mpgc.jbudget126139.service.dto.TagDTO;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Handles the application bootstrap process including:
 * <ul>
 *   <li>Database initialization</li>
 *   <li>Service and controller setup</li>
 *   <li>Creation of default data for first-time users</li>
 * </ul>
 */
public class AppBootstrap {

    private EntityManagerFactory emf;

    private AccountController accountController;
    private TransactionController transactionController;
    private TagController tagController;
    private RecurrenceController recurrenceController;

    private RecurrenceService recurrenceService;

    /**
     * Initializes the application backend by setting up:
     * <ul>
     *   <li>Database connection</li>
     *   <li>Services and controllers</li>
     *   <li>Default data population if necessary</li>
     * </ul>
     */
    public void initialize() {
        initializeDatabase();
        initializeServices();
        createDefaultDataIfNeeded();
    }

    private void initializeDatabase() {
        System.setProperty("prism.lcdtext", "false");
        System.setProperty("prism.text", "t2k");
        System.setProperty("prism.fontsmoothing", "true");

        emf = Persistence.createEntityManagerFactory("jbudget-unit");
    }

    private void initializeServices() {
        AccountService accountService = ServiceFactory.createAccountService(emf);
        TransactionService transactionService = ServiceFactory.createTransactionService(emf);
        TagService tagService = ServiceFactory.createTagService(emf);
        RecurrenceTransactionService recurrenceTransactionService = ServiceFactory.createRecurrenceTransactionService(emf);
        recurrenceService = ServiceFactory.createRecurrenceService(emf);

        accountController = new AccountControllerImpl(accountService);
        transactionController = new TransactionControllerImpl(transactionService);
        tagController = new TagControllerImpl(tagService);
        recurrenceController = new RecurrenceControllerImpl(recurrenceTransactionService);

        System.out.println("Services and controllers initialized");
    }

    private void createDefaultDataIfNeeded() {
        try {
            List<AccountDTO> existingAccounts = accountController.listAccounts();
            if (existingAccounts.isEmpty()) {
                System.out.println("Creating default data for first-time users...");
                createDefaultAccounts();
                createDefaultTags();
                createDefaultTransactions();
                System.out.println("Default data created successfully");
            } else {
                System.out.println("Existing data found, skipping default data creation");
            }
        } catch (Exception e) {
            System.err.println("Error creating default data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createDefaultAccounts() {
        try {
            accountController.createAccount(
                    "Prof. Lorenzo Rossi",
                    "EUR",
                    "Account del Prof. Lorenzo Rossi - Università di Camerino"
            );

            accountController.createAccount(
                    "Prof. Michele Loreti",
                    "EUR",
                    "Account del Prof. Michele Loreti - Università di Camerino"
            );

            System.out.println("Default accounts created");
        } catch (Exception e) {
            System.err.println("Error creating default accounts: " + e.getMessage());
            throw e;
        }
    }

    private void createDefaultTags() {
        try {
            tagController.createTag("Ricerca", "Spese per attività di ricerca");
            tagController.createTag("Didattica", "Materiali e strumenti per la didattica");
            tagController.createTag("Conferenze", "Partecipazione a conferenze e seminari");
            tagController.createTag("Pubblicazioni", "Costi per pubblicazioni accademiche");
            tagController.createTag("Attrezzature", "Hardware e software per ricerca");

            tagController.createTag("Alimentari", "Spese per generi alimentari");
            tagController.createTag("Trasporti", "Mezzi di trasporto e carburante");
            tagController.createTag("Utenze", "Bollette e servizi pubblici");
            tagController.createTag("Tempo Libero", "Intrattenimento e hobby");
            tagController.createTag("Salute e sport", "Spese mediche e per sport");

            tagController.createTag("Entrate", "Retribuzioni");
            tagController.createTag("Consulenze", "Attività di consulenza");
            tagController.createTag("Borse di Studio", "Finanziamenti per ricerca");

            System.out.println("Default tags created");
        } catch (Exception e) {
            System.err.println("Error creating default tags: " + e.getMessage());
            throw e;
        }
    }

    private void createDefaultTransactions() {
        try {
            List<AccountDTO> accounts = accountController.listAccounts();
            List<TagDTO> allTags = tagController.listAllTags();
            OffsetDateTime now = OffsetDateTime.now();

            AccountDTO loreti = accounts.stream()
                    .filter(acc -> acc.name().contains("Michele Loreti"))
                    .findFirst()
                    .orElseThrow();

            createSampleTransactionsForAccount(loreti, allTags, now, "Michele Loreti");

            AccountDTO rossi = accounts.stream()
                    .filter(acc -> acc.name().contains("Lorenzo Rossi"))
                    .findFirst()
                    .orElseThrow();

            createSampleTransactionsForAccount(rossi, allTags, now, "Lorenzo Rossi");

            System.out.println("Default transactions created");
        } catch (Exception e) {
            System.err.println("Error creating default transactions: " + e.getMessage());
            throw e;
        }
    }

    private void createSampleTransactionsForAccount(AccountDTO account, List<TagDTO> allTags,
                                                    OffsetDateTime now, String professorName) {
        try {
            transactionController.createTransaction(
                    account.id(), now.minusDays(30), new BigDecimal("6000.00"),
                    TransactionDirection.CREDIT, "Vendita moto",
                    Set.of(getTagIdByName(allTags, "Entrate")));

            transactionController.createTransaction(
                    account.id(), now.minusDays(20), new BigDecimal("750.00"),
                    TransactionDirection.CREDIT, "Consulenza esterna",
                    Set.of(getTagIdByName(allTags, "Consulenze")));

            transactionController.createTransaction(
                    account.id(), now.minusDays(25), new BigDecimal("450.00"),
                    TransactionDirection.DEBIT, "Partecipazione conferenza internazionale",
                    Set.of(getTagIdByName(allTags, "Conferenze")));

            transactionController.createTransaction(
                    account.id(), now.minusDays(18), new BigDecimal("89.99"),
                    TransactionDirection.DEBIT, "Libri di testo per corso",
                    Set.of(getTagIdByName(allTags, "Didattica")));

            transactionController.createTransaction(
                    account.id(), now.minusDays(15), new BigDecimal("299.00"),
                    TransactionDirection.DEBIT, "Software per ricerca",
                    Set.of(getTagIdByName(allTags, "Attrezzature")));

            transactionController.createTransaction(
                    account.id(), now.minusDays(10), new BigDecimal("125.50"),
                    TransactionDirection.DEBIT, "Spesa settimanale",
                    Set.of(getTagIdByName(allTags, "Alimentari")));

            transactionController.createTransaction(
                    account.id(), now.minusDays(8), new BigDecimal("65.00"),
                    TransactionDirection.DEBIT, "Carburante auto",
                    Set.of(getTagIdByName(allTags, "Trasporti")));

            transactionController.createTransaction(
                    account.id(), now.minusDays(5), new BigDecimal("10.00"),
                    TransactionDirection.DEBIT, "Quota padel",
                    Set.of(getTagIdByName(allTags, "Salute e sport")));

            transactionController.createTransaction(
                    account.id(), now.minusDays(1), new BigDecimal("499.00"),
                    TransactionDirection.CREDIT, "Vendita vecchio iPhone",
                    Set.of(getTagIdByName(allTags, "Entrate")));

            System.out.println("Sample transactions created for " + professorName);

        } catch (Exception e) {
            System.err.println("Error creating sample transactions for " + professorName + ": " + e.getMessage());
            throw e;
        }
    }

    private UUID getTagIdByName(List<TagDTO> tags, String name) {
        return tags.stream()
                .filter(tag -> tag.name().equals(name))
                .findFirst()
                .map(TagDTO::id)
                .orElseThrow(() -> new RuntimeException("Tag not found: " + name));
    }

    /**
     * Returns an object holding all initialized controllers.
     *
     * @return a {@link Controllers} record containing all controllers
     */
    public Controllers getControllers() {
        return new Controllers(accountController, transactionController, tagController, recurrenceController);
    }

    /**
     * Returns an object holding all initialized services.
     *
     * @return a {@link Services} record containing all services
     */
    public Services getServices() {
        return new Services(recurrenceService);
    }

    /**
     * Cleans up application resources including closing the database connection.
     */
    public void cleanup() {
        if (emf != null) {
            emf.close();
        }
    }

    /**
     * Record class holding references to all controllers.
     *
     * @param accountController    the account controller
     * @param transactionController the transaction controller
     * @param tagController        the tag controller
     * @param recurrenceController the recurrence controller
     */
    public record Controllers(
            AccountController accountController,
            TransactionController transactionController,
            TagController tagController,
            RecurrenceController recurrenceController
    ) {}

    /**
     * Record class holding references to all services.
     *
     * @param recurrenceService the recurrence service
     */
    public record Services(
            RecurrenceService recurrenceService
    ) {}
}