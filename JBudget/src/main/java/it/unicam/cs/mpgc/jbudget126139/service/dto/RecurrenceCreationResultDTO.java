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

package it.unicam.cs.mpgc.jbudget126139.service.dto;

import java.util.List;

/**
 * Data Transfer Object (DTO) representing the result of creating a recurrence.
 * <p>
 * Contains the created recurrence, the list of transactions generated from it,
 * the total number of generated transactions, and a summary message.
 * </p>
 *
 * @param recurrence           the created recurrence
 * @param generatedTransactions the list of generated transactions
 * @param transactionCount     the number of generated transactions
 * @param summary              a human-readable summary of the operation
 */
public record RecurrenceCreationResultDTO(
        RecurrenceDTO recurrence,
        List<TransactionDTO> generatedTransactions,
        int transactionCount,
        String summary
) {

    /**
     * Creates a {@code RecurrenceCreationResultDTO} from a recurrence and its generated transactions.
     * The {@code transactionCount} and {@code summary} are calculated automatically.
     *
     * @param recurrence  the created recurrence; must not be {@code null}
     * @param transactions the list of generated transactions; must not be {@code null}
     * @return a new {@link RecurrenceCreationResultDTO} instance
     */
    public static RecurrenceCreationResultDTO of(RecurrenceDTO recurrence, List<TransactionDTO> transactions) {
        String summary = createSummary(transactions.size());
        return new RecurrenceCreationResultDTO(recurrence, transactions, transactions.size(), summary);
    }

    // Private helper methods
    private static String createSummary(int count) {
        if (count == 0)
            return "Recurring transaction created successfully! No transactions were added since the start date is in the future.";
        else if (count == 1)
            return "Recurring transaction created successfully! 1 transaction has been added.";
        else
            return "Recurring transaction created successfully! " + count + " transactions have been added.";
    }
}