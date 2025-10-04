module com.puzzlepalace {
    requires javafx.controls;
    requires javafx.fxml;
    requires json.simple;

    opens com.puzzlepalace to javafx.fxml;
    exports com.example;
}
