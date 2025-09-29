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

import it.unicam.cs.mpgc.jbudget126139.ui.navigation.NavigationTarget;

/**
 * Event representing a navigation request in the UI.
 * <p>
 * Published on the {@code UIEventBus} to instruct the application
 * to navigate to a specific {@link NavigationTarget}. Optionally,
 * additional data can be passed along to the target view.
 * </p>
 *
 * @param target the navigation target (destination view/screen)
 * @param data   optional data to pass to the target (may be {@code null})
 */
public record NavigationEvent(NavigationTarget target, Object data) {

    /**
     * Creates a {@code NavigationEvent} without extra data.
     *
     * @param target the navigation target
     */
    public NavigationEvent(NavigationTarget target) {
        this(target, null);
    }

    /**
     * Returns the lowercase string representation of the target name.
     * <p>
     * Useful for routing logic or CSS styling based on the navigation target.
     * </p>
     *
     * @return the target name in lowercase
     */
    public String getTarget() {
        return target.name().toLowerCase();
    }
}