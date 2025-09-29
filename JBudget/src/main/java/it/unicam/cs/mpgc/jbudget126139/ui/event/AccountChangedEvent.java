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

package it.unicam.cs.mpgc.jbudget126139.ui.event;

import it.unicam.cs.mpgc.jbudget126139.service.dto.AccountDTO;

/**
 * Event representing a change in an {@link AccountDTO}.
 * <p>
 * This event is published on the {@code UIEventBus} whenever an account is
 * selected, created, updated, or deleted in the application.
 * </p>
 *
 * @param account the account involved in the event
 * @param action  the type of action performed on the account
 */
public record AccountChangedEvent(AccountDTO account, AccountAction action) {

    /**
     * Enumeration of possible account actions that can trigger this event.
     */
    public enum AccountAction {
        /** An account has been selected in the UI. */
        SELECTED,

        /** A new account has been created. */
        CREATED,

        /** An existing account has been updated. */
        UPDATED,

        /** An account has been deleted. */
        DELETED
    }
}