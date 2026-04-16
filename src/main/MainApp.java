package main;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ui.ImageLoader;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) {
        Scene scene = new Scene(ImageLoader.createImageLoader(stage), 500, 400);

        stage.setTitle("Photo App");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}