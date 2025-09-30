module com.puzzlepalace {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.puzzlepalace to javafx.fxml;
    exports com.puzzlepalace;
}
