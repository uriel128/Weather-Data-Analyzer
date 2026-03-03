package com.example.weatherdataanalyzer;

/**
 * Weather category labels.
 */
enum WeatherCategory {
    HOT("Hot"),
    WARM("Warm"),
    COLD("Cold");

    private final String label;

    WeatherCategory(String label) {
        this.label = label;
    }

    String label() {
        return label;
    }
}
