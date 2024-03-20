module my.javagame {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens my.javagame to javafx.fxml;
    exports my.javagame;
}