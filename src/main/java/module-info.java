module com.example.insertionsortvisualizer {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.ikonli.javafx;
    requires java.desktop;

    opens com.example.insertionsortvisualizer to javafx.fxml;
    exports com.example.insertionsortvisualizer;
}