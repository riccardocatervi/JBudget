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

package it.unicam.cs.mpgc.jbudget126139.ui.viewimpl;

import it.unicam.cs.mpgc.jbudget126139.service.dto.AccountDTO;
import it.unicam.cs.mpgc.jbudget126139.service.dto.TransactionDTO;
import it.unicam.cs.mpgc.jbudget126139.ui.component.TransactionItem;
import it.unicam.cs.mpgc.jbudget126139.ui.view.DashboardView;
import it.unicam.cs.mpgc.jbudget126139.ui.util.FormatUtils;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Concrete implementation of the {@link DashboardView} interface.
 * <p>
 * Provides the UI for displaying account balance, quick statistics,
 * income vs. expenses charts, recent transactions, and spending by category.
 * Supports dynamic updates when the user selects a new account, adds
 * transactions, or changes the chart period.
 * </p>
 */
public class DashboardViewImpl extends BaseViewImpl implements DashboardView {

    private Currency currency = Currency.getInstance("EUR");

    private VBox root;
    private VBox balanceCard;
    private VBox quickStatsCard;
    private VBox quickActionCard;
    private VBox recentTransactionsContainer;
    private VBox chartContainer;
    private VBox categoriesChartContainer;
    private ComboBox<String> periodSelector;

    private Runnable onAddTransaction;
    private Runnable onViewAllTransactions;
    private Consumer<String> onPeriodChange;
    private Consumer<TransactionDTO> onDeleteTransaction;

    /**
     * Creates a new {@code DashboardViewImpl} instance and initializes its layout.
     */
    public DashboardViewImpl() {
        initialize();
    }

    /**
     * Sets the currency used for displaying balances and transaction amounts.
     *
     * @param currency the {@link Currency} to use
     */
    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    private void initialize() {
        initializeBaseComponents();

        root = new VBox(30);
        root.getStyleClass().add("dashboard-container");

        createHeader();
        createTopCards();
        createMiddleSection();
        createBottomSection();

        StackPane containerWithOverlays = new StackPane();
        containerWithOverlays.getChildren().addAll(root, getLoadingOverlay(), getErrorContainer());
    }

    private void createHeader() {
        Label pageTitle = new Label("Dashboard");
        pageTitle.getStyleClass().add("dashboard-title");
        root.getChildren().add(pageTitle);
    }

    private void createTopCards() {
        HBox topAccounts = new HBox(25);

        balanceCard = createBalanceCard();
        quickStatsCard = createQuickStatsCard();
        quickActionCard = createQuickActionCard();

        HBox.setHgrow(balanceCard, Priority.ALWAYS);
        HBox.setHgrow(quickStatsCard, Priority.ALWAYS);
        HBox.setHgrow(quickActionCard, Priority.ALWAYS);

        topAccounts.getChildren().addAll(balanceCard, quickStatsCard, quickActionCard);
        root.getChildren().add(topAccounts);
    }

    private VBox createBalanceCard() {
        VBox card = new VBox(20);
        card.getStyleClass().add("balance-card");

        Label title = new Label("Total Balance");
        title.getStyleClass().add("card-title");

        Label amount = new Label("$0.00");
        amount.getStyleClass().add("balance-amount-large");

        HBox accountInfo = new HBox(10);
        accountInfo.setAlignment(Pos.CENTER_LEFT);

        Label accountName = new Label("No AbstractAccount");
        accountName.getStyleClass().add("account-name");

        Label accountNumber = new Label("");
        accountNumber.getStyleClass().add("account-number");

        accountInfo.getChildren().addAll(accountName, accountNumber);

        card.getChildren().addAll(title, amount, accountInfo);
        return card;
    }

    private VBox createQuickStatsCard() {
        VBox card = new VBox(20);
        card.getStyleClass().add("stats-card");

        Label title = new Label("This Month");
        title.getStyleClass().add("card-title");

        VBox incomeRow = new VBox(5);
        HBox incomeHeader = new HBox();
        HBox.setHgrow(incomeHeader, Priority.ALWAYS);

        Label incomeTitle = new Label("Income");
        incomeTitle.getStyleClass().add("stat-label");

        Label incomeIcon = new Label("↗");
        incomeIcon.getStyleClass().add("income-icon");

        incomeHeader.getChildren().addAll(incomeTitle, incomeIcon);

        Label incomeAmount = new Label("$0.00");
        incomeAmount.getStyleClass().add("stat-amount-positive");

        incomeRow.getChildren().addAll(incomeHeader, incomeAmount);

        VBox expenseRow = new VBox(5);
        HBox expenseHeader = new HBox();
        HBox.setHgrow(expenseHeader, Priority.ALWAYS);

        Label expenseTitle = new Label("Expenses");
        expenseTitle.getStyleClass().add("stat-label");

        Label expenseIcon = new Label("↘");
        expenseIcon.getStyleClass().add("expense-icon");

        expenseHeader.getChildren().addAll(expenseTitle, expenseIcon);

        Label expenseAmount = new Label("$0.00");
        expenseAmount.getStyleClass().add("stat-amount-negative");

        expenseRow.getChildren().addAll(expenseHeader, expenseAmount);

        card.getChildren().addAll(title, incomeRow, expenseRow);
        return card;
    }

    private VBox createQuickActionCard() {
        VBox card = new VBox(20);
        card.getStyleClass().add("action-card");

        Label title = new Label("Quick Actions");
        title.getStyleClass().add("card-title");

        VBox actions = new VBox(15);

        Button addTransactionBtn = new Button("➕ Add Transaction");
        addTransactionBtn.getStyleClass().add("quick-action-btn");
        addTransactionBtn.setOnAction(e -> {
            if (onAddTransaction != null) onAddTransaction.run();
        });

        Button viewTransactionsBtn = new Button("📊 View All Transactions");
        viewTransactionsBtn.getStyleClass().add("quick-action-btn");
        viewTransactionsBtn.setOnAction(e -> {
            if (onViewAllTransactions != null) onViewAllTransactions.run();
        });

        actions.getChildren().addAll(addTransactionBtn, viewTransactionsBtn);
        card.getChildren().addAll(title, actions);
        return card;
    }

    private void createMiddleSection() {
        HBox middleContainer = new HBox(25);

        VBox chartCard = createExpenseChart();
        VBox recentCard = createRecentTransactionsCard();

        HBox.setHgrow(chartCard, Priority.ALWAYS);
        HBox.setHgrow(recentCard, Priority.ALWAYS);

        middleContainer.getChildren().addAll(chartCard, recentCard);
        root.getChildren().add(middleContainer);
    }

    private VBox createExpenseChart() {
        VBox card = new VBox(20);
        card.getStyleClass().add("chart-card");
        card.setMinHeight(400);
        card.setPrefHeight(400);

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(header, Priority.ALWAYS);

        Label title = new Label("Income vs Expenses Distribution");
        title.getStyleClass().add("card-title");

        periodSelector = new ComboBox<>();
        periodSelector.getItems().addAll("This Month", "Last 3 Months", "Last 6 Months");
        periodSelector.setValue("This Month");
        periodSelector.getStyleClass().add("period-selector");

        periodSelector.setOnAction(e -> {
            if (onPeriodChange != null) onPeriodChange.accept(periodSelector.getValue());
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(title, spacer, periodSelector);

        chartContainer = new VBox();
        chartContainer.setAlignment(Pos.CENTER);
        VBox initialChart = createIncomeExpensePieChartForPeriod("This Month");
        chartContainer.getChildren().add(initialChart);

        card.getChildren().addAll(header, chartContainer);
        return card;
    }

    private VBox createIncomeExpensePieChartForPeriod(String period) {
        VBox container = new VBox(15);
        container.setAlignment(Pos.CENTER);

        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpenses = BigDecimal.ZERO;

        if (totalIncome.compareTo(BigDecimal.ZERO) == 0 && totalExpenses.compareTo(BigDecimal.ZERO) == 0) {
            Label emptyMessage = new Label("No transactions for " + period.toLowerCase());
            emptyMessage.getStyleClass().add("empty-chart-message");
            emptyMessage.setStyle("-fx-text-fill: #666; -fx-font-size: 16px; -fx-font-style: italic;");
            container.getChildren().add(emptyMessage);
            return container;
        }

        PieChart pieChart = new PieChart();
        pieChart.setPrefSize(300, 300);
        pieChart.setLegendVisible(false);
        pieChart.setLabelsVisible(true);

        if (totalIncome.compareTo(BigDecimal.ZERO) > 0) {
            PieChart.Data incomeData = new PieChart.Data("Income: " + FormatUtils.formatCurrency(totalIncome, currency), totalIncome.doubleValue());
            pieChart.getData().add(incomeData);
        }

        if (totalExpenses.compareTo(BigDecimal.ZERO) > 0) {
            PieChart.Data expenseData = new PieChart.Data("Expenses: " + FormatUtils.formatCurrency(totalExpenses, currency), totalExpenses.doubleValue());
            pieChart.getData().add(expenseData);
        }

        Platform.runLater(() -> applyPieChartColors(pieChart, totalIncome, totalExpenses));

        HBox customLegend = createCustomLegend(totalIncome, totalExpenses);
        container.getChildren().addAll(pieChart, customLegend);

        return container;
    }

    private VBox createRecentTransactionsCard() {
        VBox card = new VBox(20);
        card.getStyleClass().add("recent-card");

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Recent Transactions");
        title.getStyleClass().add("card-title");

        Button viewAllBtn = new Button("View All");
        viewAllBtn.getStyleClass().add("view-all-btn");
        viewAllBtn.setOnAction(e -> {
            if (onViewAllTransactions != null) onViewAllTransactions.run();
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(title, spacer, viewAllBtn);

        recentTransactionsContainer = new VBox(10);

        card.getChildren().addAll(header, recentTransactionsContainer);
        return card;
    }

    private void createBottomSection() {
        VBox categoriesCard = new VBox(20);
        categoriesCard.getStyleClass().add("categories-card");

        Label title = new Label("Spending by Category");
        title.getStyleClass().add("card-title");

        categoriesChartContainer = new VBox(10);
        categoriesChartContainer.setAlignment(Pos.CENTER);

        categoriesCard.getChildren().addAll(title, categoriesChartContainer);
        root.getChildren().add(categoriesCard);
    }

    @Override
    public Parent getRoot() {
        return root;
    }

    /**
     * Updates the displayed account balance.
     *
     * @param balance the new balance value
     */
    @Override
    public void updateBalance(BigDecimal balance) {
        Label amountLabel = findLabelInCard(balanceCard, "balance-amount-large");
        if (amountLabel != null) {
            String formattedBalance = FormatUtils.formatCurrency(balance, currency);
            amountLabel.setText(formattedBalance);
        }
    }

    /**
     * Updates the account information displayed in the balance card.
     *
     * @param account the currently selected {@link AccountDTO}
     */
    public void updateAccountInfo(AccountDTO account) {
        if (account == null) return;

        Label accountNameLabel = findLabelInCard(balanceCard, "account-name");
        if (accountNameLabel != null)
            accountNameLabel.setText(account.name());

        Label accountNumberLabel = findLabelInCard(balanceCard, "account-number");
        if (accountNumberLabel != null) {
            String partialId = "••••" + account.id().toString().substring(0, 4);
            accountNumberLabel.setText(partialId);
        }
    }

    /**
     * Updates the monthly income and expenses summary.
     *
     * @param income   total income for the current period
     * @param expenses total expenses for the current period
     */
    @Override
    public void updateIncomeExpenses(BigDecimal income, BigDecimal expenses) {
        Label incomeLabel = findLabelInCard(quickStatsCard, "stat-amount-positive");
        if (incomeLabel != null)
            incomeLabel.setText(FormatUtils.formatCurrency(income, currency));

        Label expenseLabel = findLabelInCard(quickStatsCard, "stat-amount-negative");
        if (expenseLabel != null) {
            expenseLabel.setText(FormatUtils.formatCurrency(expenses, currency));
        }
    }

    /**
     * Updates the recent transactions list shown on the dashboard.
     *
     * @param transactions a list of {@link TransactionDTO} objects
     */
    @Override
    public void updateRecentTransactions(List<TransactionDTO> transactions) {
        recentTransactionsContainer.getChildren().clear();

        if (transactions.isEmpty()) {
            Label emptyLabel = new Label("No recent transactions");
            emptyLabel.getStyleClass().add("empty-message");
            recentTransactionsContainer.getChildren().add(emptyLabel);
        } else {
            int limit = Math.min(5, transactions.size());
            for (int i = 0; i < limit; i++) {
                TransactionDTO transaction = transactions.get(i);
                TransactionItem item = new TransactionItem(transaction, currency);
                recentTransactionsContainer.getChildren().add(item);
            }
        }
    }

    /**
     * Updates the income vs. expenses chart for the given period.
     *
     * @param income   total income
     * @param expenses total expenses
     * @param period   textual label for the period (e.g., "This Month")
     */
    @Override
    public void updateExpenseChart(BigDecimal income, BigDecimal expenses, String period) {
        chartContainer.getChildren().clear();
        VBox updatedChart = createIncomeExpensePieChartForPeriodWithData(period, income, expenses);
        chartContainer.getChildren().add(updatedChart);
    }

    private VBox createIncomeExpensePieChartForPeriodWithData(String period, BigDecimal totalIncome, BigDecimal totalExpenses) {
        VBox container = new VBox(15);
        container.setAlignment(Pos.CENTER);

        if (totalIncome.compareTo(BigDecimal.ZERO) == 0 && totalExpenses.compareTo(BigDecimal.ZERO) == 0) {
            Label emptyMessage = new Label("No transactions for " + period.toLowerCase());
            emptyMessage.getStyleClass().add("empty-chart-message");
            emptyMessage.setStyle("-fx-text-fill: #666; -fx-font-size: 16px; -fx-font-style: italic;");
            container.getChildren().add(emptyMessage);
            return container;
        }

        PieChart pieChart = new PieChart();
        pieChart.setPrefSize(300, 300);
        pieChart.setLegendVisible(false);
        pieChart.setLabelsVisible(true);

        if (totalIncome.compareTo(BigDecimal.ZERO) > 0) {
            PieChart.Data incomeData = new PieChart.Data("Income: " + FormatUtils.formatCurrency(totalIncome, currency), totalIncome.doubleValue());
            pieChart.getData().add(incomeData);
        }

        if (totalExpenses.compareTo(BigDecimal.ZERO) > 0) {
            PieChart.Data expenseData = new PieChart.Data("Expenses: " + FormatUtils.formatCurrency(totalExpenses, currency), totalExpenses.doubleValue());
            pieChart.getData().add(expenseData);
        }

        Platform.runLater(() -> applyPieChartColors(pieChart, totalIncome, totalExpenses));

        HBox customLegend = createCustomLegend(totalIncome, totalExpenses);
        container.getChildren().addAll(pieChart, customLegend);

        return container;
    }

    private void applyPieChartColors(PieChart chart, BigDecimal totalIncome, BigDecimal totalExpenses) {
        if (chart.getData().isEmpty()) return;

        if (totalIncome.compareTo(BigDecimal.ZERO) > 0 && totalExpenses.compareTo(BigDecimal.ZERO) > 0) {
            chart.getData().get(0).getNode().setStyle("-fx-pie-color: #4CAF50;");
            chart.getData().get(1).getNode().setStyle("-fx-pie-color: #f44336;");
        } else if (totalIncome.compareTo(BigDecimal.ZERO) > 0 && totalExpenses.compareTo(BigDecimal.ZERO) == 0) {
            chart.getData().get(0).getNode().setStyle("-fx-pie-color: #4CAF50;");
        } else if (totalIncome.compareTo(BigDecimal.ZERO) == 0 && totalExpenses.compareTo(BigDecimal.ZERO) > 0) {
            chart.getData().get(0).getNode().setStyle("-fx-pie-color: #f44336;");
        }
    }

    /**
     * Updates the "Spending by Category" chart.
     *
     * @param categoryData a map of category names to amounts spent
     */
    public void updateCategoryChart(Map<String, BigDecimal> categoryData) {
        if (categoriesChartContainer == null) {
            System.err.println("DEBUG: categoriesChartContainer is null");
            return;
        }

        categoriesChartContainer.getChildren().clear();

        if (categoryData == null || categoryData.isEmpty()) {
            Label emptyLabel = new Label("No spending data available");
            emptyLabel.setStyle("-fx-text-fill: #999; -fx-font-style: italic;");
            categoriesChartContainer.setAlignment(Pos.CENTER);
            categoriesChartContainer.setPrefHeight(300);
            categoriesChartContainer.getChildren().add(emptyLabel);
            return;
        }

        VBox chartWithLabels = new VBox(10);

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("");
        xAxis.setTickLabelsVisible(false);
        xAxis.setTickMarkVisible(false);
        yAxis.setLabel("Amount Spent");

        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle("");
        chart.setLegendVisible(false);
        chart.setAnimated(false);
        chart.setPrefHeight(250);
        chart.setMinHeight(250);
        chart.setCategoryGap(10);
        chart.setBarGap(3);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Spending");

        List<Map.Entry<String, BigDecimal>> topCategories = categoryData.entrySet().stream()
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                .limit(5)
                .toList();

        for (int i = 0; i < topCategories.size(); i++) {
            Map.Entry<String, BigDecimal> entry = topCategories.get(i);
            series.getData().add(new XYChart.Data<>(String.valueOf(i), entry.getValue().doubleValue()));
        }

        chart.getData().add(series);

        Platform.runLater(() -> {
            chart.lookupAll(".chart-bar").forEach(node -> {
                node.setStyle("-fx-bar-fill: #2196F3;");
            });
        });

        HBox labelsContainer = createCategoryLabels(topCategories);
        chartWithLabels.getChildren().addAll(chart, labelsContainer);
        categoriesChartContainer.getChildren().add(chartWithLabels);
    }

    private HBox createCategoryLabels(List<Map.Entry<String, BigDecimal>> categories) {
        HBox labelsContainer = new HBox();
        labelsContainer.setAlignment(Pos.CENTER);
        labelsContainer.setPadding(new Insets(0, 30, 0, 30));
        labelsContainer.setSpacing(categories.size() > 1 ? 20 : 0);

        for (Map.Entry<String, BigDecimal> entry : categories) {
            VBox labelBox = new VBox(5);
            labelBox.setAlignment(Pos.CENTER);
            labelBox.setPrefWidth(70);
            labelBox.setMinWidth(70);
            labelBox.setMaxWidth(70);

            String categoryName = entry.getKey();
            String displayName = truncateCategoryName(categoryName, 10);

            Label categoryNameLabel = new Label(displayName);
            categoryNameLabel.setStyle("-fx-text-fill: #333; -fx-font-size: 11px;");
            categoryNameLabel.setTextAlignment(TextAlignment.CENTER);
            categoryNameLabel.setWrapText(false);
            categoryNameLabel.setAlignment(Pos.CENTER);

            if (!displayName.equals(categoryName)) {
                Tooltip tooltip = new Tooltip(categoryName);
                Tooltip.install(categoryNameLabel, tooltip);
            }

            Label amount = new Label("$" + String.format("%,.2f", entry.getValue()));
            amount.setStyle("-fx-text-fill: #2196F3; -fx-font-size: 10px;");
            amount.setTextAlignment(TextAlignment.CENTER);
            amount.setAlignment(Pos.CENTER);

            labelBox.getChildren().addAll(categoryNameLabel, amount);
            labelsContainer.getChildren().add(labelBox);
        }

        return labelsContainer;
    }

    private String truncateCategoryName(String name, int maxLength) {
        return FormatUtils.truncateText(name, maxLength);
    }


    private HBox createCustomLegend(BigDecimal income, BigDecimal expenses) {
        HBox legend = new HBox(30);
        legend.setAlignment(Pos.CENTER);

        if (income.compareTo(BigDecimal.ZERO) > 0) {
            HBox incomeItem = new HBox(8);
            incomeItem.setAlignment(Pos.CENTER_LEFT);

            Label incomeSquare = new Label("  ");
            incomeSquare.setStyle("-fx-background-color: #4CAF50; -fx-border-color: #388E3C; -fx-border-width: 1px;");
            incomeSquare.setPrefSize(20, 20);

            Label incomeLabel = new Label("Income: " + FormatUtils.formatCurrency(income, currency));
            incomeLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #4CAF50;");

            incomeItem.getChildren().addAll(incomeSquare, incomeLabel);
            legend.getChildren().add(incomeItem);
        }

        if (expenses.compareTo(BigDecimal.ZERO) > 0) {
            HBox expenseItem = new HBox(8);
            expenseItem.setAlignment(Pos.CENTER_LEFT);

            Label expenseSquare = new Label("  ");
            expenseSquare.setStyle("-fx-background-color: #f44336; -fx-border-color: #d32f2f; -fx-border-width: 1px;");
            expenseSquare.setPrefSize(20, 20);

            Label expenseLabel = new Label("Expenses: " + FormatUtils.formatCurrency(expenses, currency));
            expenseLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #f44336;");

            expenseItem.getChildren().addAll(expenseSquare, expenseLabel);
            legend.getChildren().add(expenseItem);
        }

        return legend;
    }

    @Override
    public void showEmptyState() {
        root.getChildren().clear();

        VBox emptyState = new VBox(20);
        emptyState.setAlignment(Pos.CENTER);
        emptyState.getStyleClass().add("empty-state-container");

        Label emptyIcon = new Label("📊");
        emptyIcon.getStyleClass().add("empty-state-icon");

        Label emptyText = new Label("No account selected");
        emptyText.getStyleClass().add("empty-state-text");

        Label emptySubtext = new Label("Please select an account to view dashboard");
        emptySubtext.getStyleClass().add("empty-state-subtext");

        emptyState.getChildren().addAll(emptyIcon, emptyText, emptySubtext);
        root.getChildren().add(emptyState);
    }

    @Override
    public void hideEmptyState() {
        if (!root.getChildren().isEmpty() &&
                root.getChildren().size() == 1 &&
                root.getChildren().get(0) instanceof VBox vbox &&
                vbox.getStyleClass().contains("empty-state-container")) {

            root.getChildren().clear();
            createHeader();
            createTopCards();
            createMiddleSection();
            createBottomSection();
        }
    }

    private Label findLabelInCard(VBox card, String styleClass) {
        return findLabelRecursive(card, styleClass);
    }

    private Label findLabelRecursive(javafx.scene.Node node, String styleClass) {
        if (node instanceof Label label && label.getStyleClass().contains(styleClass)) {
            return label;
        }

        if (node instanceof javafx.scene.Parent parent) {
            for (javafx.scene.Node child : parent.getChildrenUnmodifiable()) {
                Label found = findLabelRecursive(child, styleClass);
                if (found != null) return found;
            }
        }

        return null;
    }

    /**
     * Registers a handler invoked when the user requests to add a new transaction.
     *
     * @param handler runnable executed on add transaction
     */
    public void setOnAddTransaction(Runnable handler) {
        this.onAddTransaction = handler;
    }

    /**
     * Registers a handler invoked when the user requests to view all transactions.
     *
     * @param handler runnable executed on view all transactions
     */
    public void setOnViewAllTransactions(Runnable handler) {
        this.onViewAllTransactions = handler;
    }

    /**
     * Registers a handler invoked when the user changes the period for the chart.
     *
     * @param handler consumer receiving the selected period label
     */
    public void setOnPeriodChange(Consumer<String> handler) {
        this.onPeriodChange = handler;
    }
}