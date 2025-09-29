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

package it.unicam.cs.mpgc.jbudget126139.ui.navigation;

/**
 * Defines the available navigation targets within the application.
 * <p>
 * Each target corresponds to a section of the UI and provides
 * a human-readable display name and an icon for use in navigation menus.
 * </p>
 */
public enum NavigationTarget {

    /** Dashboard view showing summary statistics and charts. */
    DASHBOARD("Dashboard", "🏠"),

    /** Accounts view for managing user accounts. */
    ACCOUNTS("My Accounts", "💳"),

    /** Transactions view for listing and managing financial transactions. */
    TRANSACTIONS("Transactions", "📊"),

    /** Categories (tags) view for organizing transactions into groups. */
    TAGS("Categories", "🏷️"),

    /** Recurring transactions view for managing scheduled operations. */
    RECURRING("Recurring", "🔄");

    private final String displayName;
    private final String icon;

    /**
     * Constructs a navigation target with a display name and icon.
     *
     * @param displayName the human-readable name of the target
     * @param icon        the emoji or icon representing the target
     */
    NavigationTarget(String displayName, String icon) {
        this.displayName = displayName;
        this.icon = icon;
    }

    /**
     * Returns the display name associated with this navigation target.
     *
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns the icon associated with this navigation target.
     *
     * @return the icon string (e.g., emoji)
     */
    public String getIcon() {
        return icon;
    }
}