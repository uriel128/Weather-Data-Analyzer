module com.example.weatherdataanalyzer {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.weatherdataanalyzer to javafx.fxml;
    exports com.example.weatherdataanalyzer;
}