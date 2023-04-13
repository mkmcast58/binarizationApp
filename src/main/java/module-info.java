module com.example.binarization {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.swing;


    opens com.example.binarization to javafx.fxml;
    exports com.example.binarization;
}