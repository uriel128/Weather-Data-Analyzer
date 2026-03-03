package com.example.weatherdataanalyzer;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * # Weather UI Module
 *
 * Composes JavaFX UI and wires user actions to parsing and analysis modules.
 */
public interface WeatherDataApp {

    /**
     * Builds and displays the main scene.
     *
     * @param executor background executor for data tasks
     */
    static void createAndShowUi(ExecutorService executor) {
        var stage = new Stage();
        stage.setTitle("Weather Data Analyzer");

        var heading = new Label("Weather Data Analyzer");
        heading.getStyleClass().add("heading");

        var yearSelector = new ComboBox<Integer>();
        yearSelector.setPrefWidth(130);

        var monthSelector = new ComboBox<Month>(FXCollections.observableArrayList(Month.values()));
        monthSelector.setValue(Month.JANUARY);
        monthSelector.setPrefWidth(190);
        monthSelector.setCellFactory(event -> new ListCell<>() {
            @Override
            protected void updateItem(Month item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getDisplayName(TextStyle.FULL, Locale.US));
            }
        });
        monthSelector.setButtonCell(monthSelector.getCellFactory().call(null));

        var dayInput = new TextField(Integer.toString(AppConfig.DEFAULT_DAY));
        dayInput.setPromptText("Day (1-31)");
        dayInput.setPrefWidth(120);

        var updateButton = new Button("Update View");
        updateButton.getStyleClass().add("primary-button");

        var controls = new HBox(12, yearSelector, monthSelector, dayInput, updateButton);
        controls.setAlignment(Pos.CENTER_LEFT);

        var dateCardTitle = new Label("Selected Date");
        dateCardTitle.getStyleClass().add("card-title");

        var dateCard = new TextArea();
        dateCard.getStyleClass().addAll("output", "date-card-text");
        dateCard.setEditable(false);
        dateCard.setWrapText(true);
        dateCard.setPrefRowCount(9);

        var dateCardBox = new VBox(8, dateCardTitle, dateCard);
        dateCardBox.getStyleClass().add("date-card");

        var monthThresholdInput = new TextField("%.0f".formatted(AppConfig.DEFAULT_THRESHOLD_F));
        monthThresholdInput.setPromptText("Threshold (F)");
        monthThresholdInput.setPrefWidth(140);

        var thresholdRow = new HBox(10, new Label("Month threshold (F):"), monthThresholdInput);
        thresholdRow.setAlignment(Pos.CENTER_LEFT);

        var monthSummaryTitle = new Label("Monthly Summary");
        monthSummaryTitle.getStyleClass().add("card-title");

        var monthSummary = new TextArea();
        monthSummary.getStyleClass().addAll("output", "summary-text");
        monthSummary.setEditable(false);
        monthSummary.setWrapText(true);
        monthSummary.setPrefHeight(320);

        var monthSummaryCard = new VBox(8, monthSummaryTitle, monthSummary);
        monthSummaryCard.getStyleClass().add("month-panel");
        HBox.setHgrow(monthSummaryCard, Priority.ALWAYS);
        monthSummaryCard.setMaxWidth(Double.MAX_VALUE);

        var listTitle = new Label("Days in Month (Hot/Warm/Cold)");
        listTitle.getStyleClass().add("card-title");

        var monthList = new ListView<DayLine>();
        monthList.setPrefHeight(320);
        monthList.setCellFactory(event -> new ListCell<>() {
            @Override
            protected void updateItem(DayLine line, boolean empty) {
                super.updateItem(line, empty);
                if (empty || line == null) {
                    setText(null);
                    getStyleClass().removeAll("selected-day", "dimmed-day");
                    return;
                }

                setText(line.renderLabel());
                getStyleClass().removeAll("selected-day", "dimmed-day");
                if (line.selected()) {
                    getStyleClass().add("selected-day");
                }
                if (!line.matchesThreshold()) {
                    getStyleClass().add("dimmed-day");
                }
            }
        });

        var monthListCard = new VBox(8, listTitle, monthList);
        monthListCard.getStyleClass().add("month-panel");
        HBox.setHgrow(monthListCard, Priority.ALWAYS);
        monthListCard.setMaxWidth(Double.MAX_VALUE);

        var bottomCards = new HBox(14, monthSummaryCard, monthListCard);
        bottomCards.setAlignment(Pos.TOP_LEFT);

        var top = new VBox(6, heading, controls);
        VBox.setVgrow(monthSummary, Priority.ALWAYS);
        VBox.setVgrow(monthList, Priority.ALWAYS);

        var center = new VBox(14, dateCardBox, thresholdRow, bottomCards);

        var root = new BorderPane(center, top, null, null, null);
        root.getStyleClass().add("root-pane");
        BorderPane.setMargin(top, new Insets(16));
        BorderPane.setMargin(center, new Insets(0, 16, 16, 16));

        var scene = new Scene(root, 1220, 860);
        scene.getStylesheets().add(WeatherDataApp.class.getResource("weather-theme.css").toExternalForm());

        var components = new UiComponents(
                yearSelector,
                monthSelector,
                dayInput,
                monthThresholdInput,
                dateCardTitle,
                dateCard,
                monthSummary,
                monthList
        );

        var recordsRef = new AtomicReference<List<WeatherRecord>>(List.of());

        monthSummary.setText("Loading embedded dataset: " + AppConfig.DEFAULT_CSV_RESOURCE);
        dateCard.setText("Loading selected date details...");

        executor.submit(() -> loadDataset(recordsRef, components));

        var refreshAction = (Runnable) () -> executor.submit(() -> {
            var loaded = recordsRef.get();
            if (loaded.isEmpty()) {
                Platform.runLater(() -> monthSummary.setText("Embedded dataset is still loading. Please wait and try again."));
                return;
            }
            Platform.runLater(() -> refreshViews(loaded, components));
        });

        updateButton.setOnAction(event -> refreshAction.run());
        monthSelector.setOnAction(event -> refreshAction.run());
        yearSelector.setOnAction(event -> refreshAction.run());
        dayInput.textProperty().addListener((obs, oldValue, newValue) -> refreshAction.run());
        monthThresholdInput.textProperty().addListener((obs, oldValue, newValue) -> refreshAction.run());

        stage.setOnCloseRequest(event -> {
            executor.shutdownNow();
            Platform.exit();
        });

        stage.setScene(scene);
        stage.show();
    }

    /**
     * Loads dataset and initializes year selector.
     */
    private static void loadDataset(AtomicReference<List<WeatherRecord>> recordsRef, UiComponents components) {
        try {
            var parsed = WeatherCsvDataSource.parseCsvResource(AppConfig.DEFAULT_CSV_RESOURCE);
            var years = parsed.stream()
                    .map(row -> row.date().getYear())
                    .collect(Collectors.toCollection(java.util.TreeSet::new));

            Platform.runLater(() -> {
                recordsRef.set(parsed);
                components.yearSelector().setItems(FXCollections.observableArrayList(years));
                components.yearSelector().setValue(years.isEmpty() ? LocalDate.now().getYear() : years.getLast());
                refreshViews(parsed, components);
            });
        } catch (IOException ex) {
            Platform.runLater(() -> {
                components.monthSummary().setText("Unable to read embedded CSV: " + ex.getMessage());
                components.dateCard().setText("Unable to load selected date details.");
            });
        }
    }

    /**
     * Refreshes selected-date card and monthly panels.
     */
    private static void refreshViews(List<WeatherRecord> allRecords, UiComponents components) {
        var month = Optional.ofNullable(components.monthSelector().getValue()).orElse(Month.JANUARY);
        var year = Optional.ofNullable(components.yearSelector().getValue()).orElse(allRecords.getFirst().date().getYear());
        var day = InputParser.parseDay(components.dayInput().getText()).orElse(AppConfig.DEFAULT_DAY);
        var thresholdF = InputParser.parseThreshold(components.thresholdInputF().getText()).orElse(AppConfig.DEFAULT_THRESHOLD_F);
        var thresholdC = TemperatureSupport.fToC(thresholdF);

        var analyzer = new WeatherAnalyzer(allRecords);
        var monthRows = analyzer.recordsForMonth(year, month);
        var chosenDate = LocalDate.of(year, month, Math.min(day, month.length(false)));
        var chosenRecord = monthRows.stream().filter(row -> row.date().equals(chosenDate)).findFirst();

        components.dateCardTitle().setText(
                chosenDate.getMonth().getDisplayName(TextStyle.FULL, Locale.US)
                        + " " + chosenDate.getDayOfMonth() + ", " + chosenDate.getYear()
        );

        components.dateCard().setText(chosenRecord
                .map(analyzer::dateCardDetails)
                .orElse("No record available for %s.".formatted(chosenDate)));

        components.monthSummary().setText(analyzer.monthPanelSummary(year, month, thresholdF));
        components.monthList().setItems(FXCollections.observableArrayList(analyzer.monthLines(year, month, thresholdC, chosenDate)));
    }

    /**
     * Typed references to interactive UI controls.
     *
     * @param yearSelector selected year control
     * @param monthSelector selected month control
     * @param dayInput selected day input
     * @param thresholdInputF monthly threshold input in Fahrenheit
     * @param dateCardTitle selected-date title label
     * @param dateCard selected-date details area
     * @param monthSummary monthly summary area
     * @param monthList monthly day list view
     */
    record UiComponents(
            ComboBox<Integer> yearSelector,
            ComboBox<Month> monthSelector,
            TextField dayInput,
            TextField thresholdInputF,
            Label dateCardTitle,
            TextArea dateCard,
            TextArea monthSummary,
            ListView<DayLine> monthList
    ) {
    }
}
