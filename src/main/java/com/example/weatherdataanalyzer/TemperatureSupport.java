package com.example.weatherdataanalyzer;

/**
 * # Temperature Support
 *
 * Temperature conversion helpers.
 */
public interface TemperatureSupport {

    /**
     * Converts Celsius to Fahrenheit.
     *
     * @param c temperature in Celsius
     * @return temperature in Fahrenheit
     *
     * @snippet :
     * System.out.println(TemperatureSupport.cToF(0)); // 32.0
     */
    static double cToF(double c) {
        return (c * 9.0 / 5.0) + 32.0;
    }

    /**
     * Converts Fahrenheit to Celsius.
     *
     * @param f temperature in Fahrenheit
     * @return temperature in Celsius
     *
     * @snippet :
     * System.out.println(TemperatureSupport.fToC(50)); // 10.0
     */
    static double fToC(double f) {
        return (f - 32.0) * 5.0 / 9.0;
    }
}
