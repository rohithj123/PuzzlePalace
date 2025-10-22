module com.puzzlepalace {
    requires javafx.controls;
    requires javafx.fxml;
    requires json.simple;
    requires freetts;


    opens com.puzzlepalace to javafx.fxml;
    opens com.example to javafx.fxml;

    exports com.example;
}
