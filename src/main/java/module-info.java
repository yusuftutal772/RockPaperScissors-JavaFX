module com.example.rockpaperscciorsgame {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.rockpaperscciorsgame to javafx.fxml;
    exports com.example.rockpaperscciorsgame;
}