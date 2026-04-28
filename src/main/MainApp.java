package main;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ui.MainUI;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        MainUI mainUI = new MainUI(primaryStage);

        Scene scene = new Scene(mainUI.getRoot(), 1100, 700);

        primaryStage.setTitle("Photo Repository System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}