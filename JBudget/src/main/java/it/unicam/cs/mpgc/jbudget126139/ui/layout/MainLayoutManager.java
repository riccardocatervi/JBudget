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

package it.unicam.cs.mpgc.jbudget126139.ui.layout;

import it.unicam.cs.mpgc.jbudget126139.service.dto.AccountDTO;
import it.unicam.cs.mpgc.jbudget126139.ui.navigation.SidebarNavigator;
import it.unicam.cs.mpgc.jbudget126139.ui.component.AccountSelector;
import it.unicam.cs.mpgc.jbudget126139.ui.component.SidebarStats;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Layout manager responsible for constructing and managing the main UI structure.
 * <p>
 * Provides the application shell with:
 * <ul>
 *   <li>a sidebar containing navigation and statistics</li>
 *   <li>a top bar with account selection and exit button</li>
 *   <li>a central scrollable content area</li>
 * </ul>
 * Coordinates {@link AccountSelector} and {@link SidebarStats} components.
 * </p>
 */
public class MainLayoutManager {

    private BorderPane mainLayout;
    private ScrollPane contentScrollPane;
    private VBox contentContainer;
    private AccountSelector accountSelector;
    private SidebarStats sidebarStats;

    /**
     * Creates and initializes the main layout manager.
     */
    public MainLayoutManager() {
        initialize();
    }

    private void initialize() {
        mainLayout = new BorderPane();
        mainLayout.getStyleClass().add("main-layout");

        contentScrollPane = new ScrollPane();
        contentScrollPane.getStyleClass().add("content-scroll");
        contentScrollPane.setFitToWidth(true);
        contentScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        contentContainer = new VBox(30);
        contentContainer.getStyleClass().add("content-area");
        contentContainer.setPadding(new Insets(30));

        contentScrollPane.setContent(contentContainer);
        mainLayout.setCenter(contentScrollPane);
    }

    /**
     * Configures the sidebar with navigation buttons, app branding,
     * and the {@link SidebarStats} panel.
     *
     * @param sidebar the sidebar navigator container
     */
    public void setSidebar(SidebarNavigator sidebar) {
        List<Node> navigationButtons = new ArrayList<>();
        for (Node node : sidebar.getChildren()) {
            if (node instanceof Button) {
                navigationButtons.add(node);
            }
        }
        sidebar.getChildren().clear();

        sidebar.getStyleClass().add("modern-sidebar");
        sidebar.setPrefWidth(280);
        sidebar.setPadding(new Insets(30, 20, 30, 20));
        sidebar.setSpacing(10);

        HBox logoSection = new HBox(15);
        logoSection.setAlignment(Pos.CENTER_LEFT);

        Label logoIcon = new Label("💰");
        logoIcon.getStyleClass().add("logo-icon");

        Label appName = new Label("JBudget");
        appName.getStyleClass().add("app-name");

        logoSection.getChildren().addAll(logoIcon, appName);

        VBox navButtons = new VBox(8);
        navButtons.setPadding(new Insets(40, 0, 0, 0));

        navButtons.getChildren().addAll(navigationButtons);

        Region bottomSpacer = new Region();
        VBox.setVgrow(bottomSpacer, Priority.ALWAYS);

        sidebarStats = new SidebarStats();

        sidebar.getChildren().addAll(logoSection, navButtons, bottomSpacer, sidebarStats);

        mainLayout.setLeft(sidebar);
    }

    /**
     * Sets up the top bar containing the welcome message,
     * account selector, and exit button.
     *
     * @param onAccountChange callback invoked when account selection changes
     * @param onExit          callback invoked when the user clicks the exit button
     */
    public void setupTopBar(Consumer<AccountDTO> onAccountChange, Runnable onExit) {
        VBox topBar = new VBox();
        topBar.getStyleClass().add("top-bar");

        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(20, 30, 20, 30));

        Label welcomeLabel = new Label("Welcome to JBudget");
        welcomeLabel.getStyleClass().add("welcome-text");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        accountSelector = new AccountSelector(onAccountChange);

        Button exitBtn = new Button("Exit App");
        exitBtn.getStyleClass().add("logout-btn");
        exitBtn.setOnAction(e -> onExit.run());

        header.getChildren().addAll(welcomeLabel, spacer, accountSelector, exitBtn);
        topBar.getChildren().add(header);

        mainLayout.setTop(topBar);
    }

    /**
     * Returns the root layout node for embedding in the application scene.
     *
     * @return the root {@link BorderPane}
     */
    public BorderPane getRoot() {
        return mainLayout;
    }

    /**
     * Replaces the current content with the given view.
     *
     * @param content the view to display
     */
    public void setContent(javafx.scene.Parent content) {
        contentContainer.getChildren().clear();
        contentContainer.getChildren().add(content);
    }

    /**
     * Updates the list of accounts available in the {@link AccountSelector}.
     *
     * @param accounts list of available accounts
     */
    public void updateAccountsList(List<AccountDTO> accounts) {
        if (accountSelector != null) {
            accountSelector.updateAccounts(accounts);
        }
    }

    /**
     * Updates the UI to reflect the currently selected account.
     * <p>
     * Also refreshes the {@link SidebarStats} panel if available.
     * </p>
     *
     * @param account the current account
     */
    public void updateCurrentAccount(AccountDTO account) {
        if (accountSelector != null)
            accountSelector.setCurrentAccount(account);
        if (sidebarStats != null)
            sidebarStats.updateForAccount(account);
    }

    /**
     * Updates sidebar statistics (balance, income, expenses) for the given account.
     *
     * @param balance  current balance
     * @param income   total income
     * @param expenses total expenses
     * @param account  the account for which stats are displayed
     */
    public void updateSidebarStats(BigDecimal balance, BigDecimal income, BigDecimal expenses, AccountDTO account) {
        if (sidebarStats != null && account != null) {
            sidebarStats.updateStats(balance, income, expenses, account);
        }
    }
}