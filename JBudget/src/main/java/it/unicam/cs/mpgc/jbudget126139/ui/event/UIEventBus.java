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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.function.Consumer;

/**
 * A simple event bus for the UI layer.
 * <p>
 * Provides a publish/subscribe mechanism that allows controllers, dialogs,
 * and views to communicate decoupled via events. Subscribers can register
 * to listen for specific event types, and publishers can broadcast events
 * to all matching subscribers.
 * </p>
 *
 * <p><b>Key features:</b></p>
 * <ul>
 *   <li>Supports multiple subscribers per event type</li>
 *   <li>Prevents re-processing of the same event in nested publishing</li>
 *   <li>Provides error isolation (exceptions in handlers are caught and logged)</li>
 * </ul>
 */
public class UIEventBus {

    private final Map<Class<?>, List<Consumer<?>>> subscribers = new HashMap<>();
    private final Set<Object> processingEvents = new HashSet<>();

    /**
     * Subscribes a handler to a specific type of event.
     *
     * @param eventType the class of the event to subscribe to
     * @param handler   the handler that will be invoked when the event is published
     * @param <T>       the type of the event
     */
    @SuppressWarnings("unchecked")
    public <T> void subscribe(Class<T> eventType, Consumer<T> handler) {
        subscribers.computeIfAbsent(eventType, k -> new ArrayList<>()).add((Consumer<Object>) handler);
    }

    /**
     * Publishes an event to all registered subscribers for its type.
     * <p>
     * If the same event is already being processed (to avoid recursion),
     * it will be ignored.
     * </p>
     *
     * @param event the event to publish (ignored if {@code null})
     */
    public void publish(Object event) {
        if (event == null) return;
        if (processingEvents.contains(event))
            return;
        try {
            processingEvents.add(event);
            deliver(event);
        } finally {
            processingEvents.remove(event);
        }
    }

    /**
     * Delivers an event to all subscribed handlers.
     *
     * @param event the event to deliver
     */
    private void deliver(Object event) {
        List<Consumer<?>> handlers = subscribers.get(event.getClass());
        if (handlers != null) {
            List<Consumer<?>> snapshot = new ArrayList<>(handlers);
            for (Consumer<?> handler : snapshot) {
                @SuppressWarnings("unchecked")
                Consumer<Object> h = (Consumer<Object>) handler;
                try {
                    h.accept(event);
                } catch (Exception e) {
                    System.err.println("Error in event handler: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Unsubscribes a handler from a specific event type.
     *
     * @param eventType the class of the event
     * @param handler   the handler to remove
     * @param <T>       the type of the event
     */
    public <T> void unsubscribe(Class<T> eventType, Consumer<T> handler) {
        List<Consumer<?>> handlers = subscribers.get(eventType);
        if (handlers != null) handlers.remove(handler);
    }

    /**
     * Removes all subscribers and clears the processing state.
     */
    public void clear() {
        subscribers.clear();
        processingEvents.clear();
    }
}