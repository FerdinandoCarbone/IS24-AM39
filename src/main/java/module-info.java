module com.example.codexnaturalis {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens com.example.codexnaturalis to javafx.fxml;
    exports com.example.codexnaturalis;
}