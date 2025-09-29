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

package it.unicam.cs.mpgc.jbudget126139;

import it.unicam.cs.mpgc.jbudget126139.ui.UIManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

/**
 * Main application entry point for JBudget.
 * <p>
 * This class bootstraps the application by:
 * <ul>
 *   <li>Initializing the backend using {@link AppBootstrap}</li>
 *   <li>Delegating UI setup and navigation to {@link UIManager}</li>
 *   <li>Managing the JavaFX application lifecycle</li>
 * </ul>
 */
public class Main extends Application {

    private AppBootstrap appBootstrap;
    private UIManager uiManager;

    /**
     * Starts the JavaFX application.
     *
     * @param primaryStage the primary stage for this application
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            appBootstrap = new AppBootstrap();
            appBootstrap.initialize();

            uiManager = new UIManager(
                    appBootstrap.getControllers(),
                    appBootstrap.getServices(),
                    primaryStage
            );
            uiManager.initialize();

            primaryStage.show();

            Platform.runLater(() -> uiManager.loadInitialData());

        } catch (Exception e) {
            System.err.println("Failed to start application: " + e.getMessage());
            Platform.exit();
        }
    }

    /**
     * Stops the JavaFX application and performs cleanup.
     */
    @Override
    public void stop() {
        cleanup();
    }

    /**
     * Cleans up resources including UI components and backend services.
     */
    private void cleanup() {
        try {
            if (uiManager != null) {
                uiManager.cleanup();
            }
            if (appBootstrap != null) {
                appBootstrap.cleanup();
            }
        } catch (Exception e) {
            System.err.println("Error during application cleanup: " + e.getMessage());
        }
    }

    /**
     * Main method that launches the JavaFX application.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}