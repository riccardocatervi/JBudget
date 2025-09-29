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

import java.util.Map;

/**
 * Event signaling that a specific type of application data should be refreshed.
 * <p>
 * Published on the {@code UIEventBus} to notify controllers or views that they
 * need to reload their data (e.g., after a CRUD operation or synchronization).
 * </p>
 *
 * @param type the type of data to refresh
 * @param data an optional map of additional context data (may be empty or {@code null})
 */
public record DataRefreshEvent(DataType type, Map<String, Object> data) {

    /**
     * Types of data that can be refreshed in the application.
     */
    public enum DataType {
        /** Refresh account data. */
        ACCOUNTS,

        /** Refresh transaction data. */
        TRANSACTIONS,

        /** Refresh tag/category data. */
        TAGS,

        /** Refresh statistics/aggregated data. */
        STATS,

        /** Refresh all data types. */
        ALL
    }
}