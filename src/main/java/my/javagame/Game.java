package my.javagame;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ResourceBundle;

public class Game extends Application {
    public static final ResourceBundle resourceBundle =  ResourceBundle.getBundle("my.javagame.i18n.i18n");

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Game.class.getResource("board-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 750, 900);
        scene.getStylesheets().add(Game.class.getResource("game.css").toExternalForm());
        stage.setTitle(resourceBundle.getString("gameTitle"));
        stage.setScene(scene);
        scene.getRoot().requestFocus();
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}