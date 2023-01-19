module com.example.filmaficionado2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.media;


    opens com.example.filmaficionado2 to javafx.fxml;
    exports com.example.filmaficionado2;
}