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

package it.unicam.cs.mpgc.jbudget126139.ui.view;

import it.unicam.cs.mpgc.jbudget126139.service.dto.AccountDTO;
import javafx.scene.Parent;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * View interface for displaying and managing user accounts in the UI.
 * <p>
 * Provides methods for rendering account lists, updating balances,
 * and toggling between the account list and account creation form.
 * </p>
 * <p>
 * Implementations of this interface are responsible only for UI rendering
 * and should not contain business logic, which is handled by controllers.
 * </p>
 */
public interface AccountsView extends BaseView {

    /**
     * Returns the root node of this view, to be attached
     * to the main application layout.
     *
     * @return the root {@link Parent} node
     */
    Parent getRoot();

    /**
     * Displays the given list of accounts in the view.
     *
     * @param accounts list of accounts to display
     */
    void displayAccounts(List<AccountDTO> accounts);

    /**
     * Updates the displayed balance for a specific account.
     *
     * @param accountId the ID of the account
     * @param balance   the updated balance value
     */
    void updateAccountBalance(UUID accountId, BigDecimal balance);
}