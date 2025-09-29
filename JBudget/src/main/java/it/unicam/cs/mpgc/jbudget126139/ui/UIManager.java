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

package it.unicam.cs.mpgc.jbudget126139.ui;

import it.unicam.cs.mpgc.jbudget126139.AppBootstrap;
import it.unicam.cs.mpgc.jbudget126139.service.dto.AccountDTO;
import it.unicam.cs.mpgc.jbudget126139.ui.controller.*;
import it.unicam.cs.mpgc.jbudget126139.ui.event.*;
import it.unicam.cs.mpgc.jbudget126139.ui.layout.MainLayoutManager;
import it.unicam.cs.mpgc.jbudget126139.ui.navigation.NavigationTarget;
import it.unicam.cs.mpgc.jbudget126139.ui.navigation.SidebarNavigator;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Central UI manager responsible for initializing the application UI, wiring navigation,
 * subscribing to and relaying UI events, and orchestrating the lifecycle of view controllers.
 * <p>
 * Responsibilities include:
 * <ul>
 *   <li>UI component initialization and window setup</li>
 *   <li>Lazy navigation between views</li>
 *   <li>Event handling and coordination</li>
 *   <li>Controller lifecycle management (initialize/cleanup)</li>
 * </ul>
 * </p>
 */
public class UIManager {

    private final AppBootstrap.Controllers controllers;
    private final AppBootstrap.Services services;
    private final Stage primaryStage;

    private UIEventBus eventBus;
    private MainLayoutManager layoutManager;
    private UIControllerFactory controllerFactory;
    private SidebarNavigator sidebar;

    private final Map<NavigationTarget, UIController> uiControllerCache = new HashMap<>();
    private NavigationTarget currentTarget;
    private AccountDTO currentAccount;

    /**
     * Creates a new {@code UIManager}.
     *
     * @param controllers  aggregated application controllers
     * @param services     aggregated application services
     * @param primaryStage the primary JavaFX stage to render into
     */
    public UIManager(AppBootstrap.Controllers controllers,
                     AppBootstrap.Services services,
                     Stage primaryStage) {
        this.controllers = controllers;
        this.services = services;
        this.primaryStage = primaryStage;
    }

    /**
     * Initializes the UI layer, including event bus subscriptions, UI structure,
     * and primary window configuration. This method should be called once during application startup.
     */
    public void initialize() {
        initializeEventSystem();
        initializeUIComponents();
        setupMainWindow();
        setupEventHandlers();
    }

    private void initializeEventSystem() {
        eventBus = new UIEventBus();
    }

    private void initializeUIComponents() {
        controllerFactory = new UIControllerFactory(
                controllers.accountController(),
                controllers.transactionController(),
                controllers.tagController(),
                controllers.recurrenceController(),
                services.recurrenceService(),
                eventBus
        );

        layoutManager = new MainLayoutManager();
        sidebar = new SidebarNavigator(this::navigateToLazy);
        layoutManager.setSidebar(sidebar);

        layoutManager.setupTopBar(this::handleAccountChange, this::handleExit);
    }

    private void setupMainWindow() {
        primaryStage.setTitle("JBudget - Personal Finance Manager");
        primaryStage.setWidth(1600);
        primaryStage.setHeight(1000);
        primaryStage.setMinWidth(1200);
        primaryStage.setMinHeight(800);

        Scene scene = new Scene(layoutManager.getRoot());
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(e -> {
            cleanup();
            Platform.exit();
        });
    }

    private void setupEventHandlers() {
        eventBus.subscribe(AccountChangedEvent.class, this::handleAccountChangedEvent);

        eventBus.subscribe(NavigationEvent.class, event -> {
            Platform.runLater(() -> navigateToLazy(event.target()));
        });

        eventBus.subscribe(DataRefreshEvent.class, event -> {
            if (event.type() == DataRefreshEvent.DataType.STATS) {
                handleStatisticsRefresh(event);
            }
        });
        eventBus.subscribe(TransactionChangedEvent.class, this::handleTransactionChanged);
    }

    private void handleStatisticsRefresh(DataRefreshEvent event) {
        Map<String, Object> data = event.data();
        if (data != null && data.containsKey("balance")) {
            BigDecimal balance = (BigDecimal) data.get("balance");
            BigDecimal income = (BigDecimal) data.get("income");
            BigDecimal expenses = (BigDecimal) data.get("expenses");
            AccountDTO account = (AccountDTO) data.get("account");

            Platform.runLater(() -> {
                if (layoutManager != null) {
                    layoutManager.updateSidebarStats(balance, income, expenses, account);
                }
            });
        }
    }

    private void navigateToLazy(NavigationTarget target) {
        try {
            UIController uiController = uiControllerCache.computeIfAbsent(target, this::createUIControllerForTarget);

            if (uiController == null) {
                System.err.println("Failed to create controller for target: " + target);
                return;
            }

            if (currentTarget != null && currentTarget != target) {
                UIController currentController = uiControllerCache.get(currentTarget);
                if (currentController != null)
                    currentController.cleanup();
            }

            layoutManager.setContent(getViewForController(uiController));
            currentTarget = target;

            if (sidebar != null)
                sidebar.setActiveTarget(target);

        } catch (Exception e) {
            System.err.println("Error navigating to " + target + ": " + e.getMessage());
        }
    }

    private UIController createUIControllerForTarget(NavigationTarget target) {
        System.out.println("Lazy creating controller for: " + target);

        UIController controller = switch (target) {
            case DASHBOARD -> {
                DashboardControllerUI dashController = controllerFactory.createDashboardController();
                if (currentAccount != null) {
                    dashController.setCurrentAccount(currentAccount);
                }
                dashController.initialize();
                yield dashController;
            }
            case ACCOUNTS -> {
                AccountControllerUI accController = controllerFactory.createAccountsController();
                accController.initialize();
                yield accController;
            }
            case TRANSACTIONS -> {
                TransactionControllerUI transController = controllerFactory.createTransactionsController();
                if (currentAccount != null) {
                    transController.setCurrentAccount(currentAccount);
                }
                transController.initialize();
                yield transController;
            }
            case TAGS -> {
                TagControllerUI tagControllerUI = controllerFactory.createTagsController();
                tagControllerUI.initialize();
                yield tagControllerUI;
            }
            case RECURRING -> {
                RecurringControllerUI recurController = controllerFactory.createRecurringController();
                if (currentAccount != null) {
                    recurController.setCurrentAccount(currentAccount);
                }
                recurController.initialize();
                yield recurController;
            }
        };

        return controller;
    }

    private Parent getViewForController(UIController controller) {
        return switch (controller) {
            case DashboardControllerUI dc -> dc.getView().getRoot();
            case AccountControllerUI ac -> ac.getView().getRoot();
            case TransactionControllerUI tc -> tc.getView().getRoot();
            case TagControllerUI tgc -> tgc.getView().getRoot();
            case RecurringControllerUI rc -> rc.getView().getRoot();
            default -> throw new IllegalArgumentException("Unknown controller type: " + controller.getClass());
        };
    }

    private void handleAccountChangedEvent(AccountChangedEvent event) {
        if (event.action() == AccountChangedEvent.AccountAction.SELECTED) {
            this.currentAccount = event.account();
            updateControllersWithAccount(currentAccount);
            layoutManager.updateCurrentAccount(event.account());
        }
    }

    private void updateControllersWithAccount(AccountDTO account) {
        for (Map.Entry<NavigationTarget, UIController> entry : uiControllerCache.entrySet()) {
            UIController controller = entry.getValue();

            switch (controller) {
                case DashboardControllerUI dc -> dc.setCurrentAccount(account);
                case TransactionControllerUI tc -> tc.setCurrentAccount(account);
                case RecurringControllerUI rc -> rc.setCurrentAccount(account);
                default -> { }
            }
        }
    }

    /**
     * Loads initial data required by the UI and selects a default view.
     * If accounts exist, the first account is selected and the dashboard is shown.
     * Otherwise, the dashboard is shown in its empty state.
     */
    public void loadInitialData() {
        try {
            List<AccountDTO> accounts = controllers.accountController().listAccounts();
            layoutManager.updateAccountsList(accounts);

            if (!accounts.isEmpty()) {
                AccountDTO firstAccount = accounts.get(0);
                this.currentAccount = firstAccount;
                eventBus.publish(new AccountChangedEvent(firstAccount, AccountChangedEvent.AccountAction.SELECTED));
                navigateToLazy(NavigationTarget.DASHBOARD);
            } else {
                navigateToLazy(NavigationTarget.DASHBOARD);
            }
        } catch (Exception e) {
            System.err.println("Error loading initial data: " + e.getMessage());
            navigateToLazy(NavigationTarget.DASHBOARD);
        }
    }

    private void handleAccountChange(AccountDTO account) {
        eventBus.publish(new AccountChangedEvent(account, AccountChangedEvent.AccountAction.SELECTED));
    }

    private void handleExit() {
        cleanup();
        Platform.exit();
    }

    private void handleTransactionChanged(TransactionChangedEvent event) {
        Platform.runLater(() -> {
            try {
                System.out.println("🔄 Updating account selector...");
                List<AccountDTO> accounts = controllers.accountController().listAccounts();
                layoutManager.updateAccountsList(accounts);

                UIController accountsController = uiControllerCache.get(NavigationTarget.ACCOUNTS);
                if (accountsController instanceof AccountControllerUI) {
                    ((AccountControllerUI) accountsController).refreshAccounts();
                }

                if (currentAccount != null && currentAccount.id().equals(event.accountId())) {
                    UIController dashboardController = uiControllerCache.get(NavigationTarget.DASHBOARD);
                    if (dashboardController instanceof DashboardControllerUI) {
                        ((DashboardControllerUI) dashboardController).loadDashboardData();
                        ((DashboardControllerUI) dashboardController).updateSidebarStats();
                    }
                }

            } catch (Exception e) {
                System.err.println("Error handling transaction change: " + e.getMessage());
            }
        });
    }

    /**
     * Cleans up all UI resources and unsubscribes controllers and event handlers.
     * This should be invoked during application shutdown to ensure a graceful exit.
     */
    public void cleanup() {
        uiControllerCache.values().forEach(controller -> {
            try {
                controller.cleanup();
            } catch (Exception e) {
                System.err.println("Error cleaning up controller: " + e.getMessage());
            }
        });

        if (eventBus != null)
            eventBus.clear();
    }
}
