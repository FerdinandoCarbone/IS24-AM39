module com.example.codexnaturalis {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.fasterxml.jackson.databind;
    requires java.rmi;
    requires java.management;
    //requires jdk.unsupported.desktop;

    opens com.example.codexnaturalis to javafx.fxml,com.fasterxml.jackson.databind;
    exports com.example.codexnaturalis;

}