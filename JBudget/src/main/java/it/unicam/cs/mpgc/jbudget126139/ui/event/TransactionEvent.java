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

import it.unicam.cs.mpgc.jbudget126139.service.dto.TransactionDTO;

/**
 * Event representing a change in a {@link TransactionDTO}.
 * <p>
 * Published on the {@code UIEventBus} when a transaction is created,
 * updated, or deleted. Controllers and views can listen for this event
 * to update their state accordingly.
 * </p>
 *
 * @param transaction the transaction involved in the event
 * @param action      the action performed on the transaction
 */
public record TransactionEvent(TransactionDTO transaction, TransactionAction action) {

    /**
     * Enumeration of possible actions performed on a transaction.
     */
    public enum TransactionAction {
        /** A new transaction was created. */
        CREATED,

        /** An existing transaction was updated. */
        UPDATED,

        /** A transaction was deleted. */
        DELETED
    }
}