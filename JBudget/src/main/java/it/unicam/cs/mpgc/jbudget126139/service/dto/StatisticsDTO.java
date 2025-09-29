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

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Data Transfer Object (DTO) representing statistical information
 * about an account's financial activity.
 *
 * @param totalBalance          the current balance of the account
 * @param totalIncome           the total income over a given period
 * @param totalExpenses         the total expenses over a given period
 * @param netAmount             the net amount (total income minus total expenses)
 * @param spendingByCategory    a mapping of category names to the total amount spent in each
 * @param recentTransactions    a list of the most recent transactions
 * @param totalTransactionsCount the total number of transactions considered in the statistics
 */
public record StatisticsDTO(
        BigDecimal totalBalance,
        BigDecimal totalIncome,
        BigDecimal totalExpenses,
        BigDecimal netAmount,
        Map<String, BigDecimal> spendingByCategory,
        List<TransactionDTO> recentTransactions,
        int totalTransactionsCount
) {}