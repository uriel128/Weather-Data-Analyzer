package com.example.weatherdataanalyzer;

import javafx.application.Platform;

import java.util.concurrent.Executors;

/**
 * # Launcher
 *
 * Thin entry point that starts JavaFX and delegates UI assembly.
 *
 * @snippet :
 * Launcher.main(new String[0]);
 */
public interface Launcher {

    /**
     * Starts the application runtime.
     *
     * @param args ignored command-line args
     */
    static void main(String[] args) {
        var executor = Executors.newVirtualThreadPerTaskExecutor();
        Platform.startup(() -> WeatherDataApp.createAndShowUi(executor));
    }
}