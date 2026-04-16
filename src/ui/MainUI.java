package ui;

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainUI {

    public static Scene createMainScene(Stage stage) {
        BorderPane root = new BorderPane();

        // Center = Image Viewer
        root.setCenter(ImageViewer.createViewer(stage));

        return new Scene(root, 800, 600);
    }
}