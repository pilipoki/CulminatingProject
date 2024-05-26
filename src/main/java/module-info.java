module culminating {
    requires javafx.controls;
    requires javafx.fxml;

    opens culminating to javafx.fxml;
    exports culminating;
}
