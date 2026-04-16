package main;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ui.ImageLoader;
import ui.MainUI;


public class MainApp extends Application {
    @Override
public void start(Stage stage) {
    stage.setScene(MainUI.createMainScene(stage));
    stage.setTitle("Photo App");
    stage.show();
}
    public static void main(String[] args) {
        launch();
    }
}