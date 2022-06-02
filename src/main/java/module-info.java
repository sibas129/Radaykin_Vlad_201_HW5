module com.game.radaykin_vlad_201_hw5 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.game.radaykin_vlad_201_hw5 to javafx.fxml;
    exports com.game.radaykin_vlad_201_hw5;
}