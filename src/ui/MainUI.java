package ui;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainUI {

    public static Scene createMainScene(Stage stage) {

        BorderPane root = new BorderPane();

        // LEFT: Sidebar
        VBox sidebar = new VBox(10);
        sidebar.getChildren().addAll(
                new Label("📁 Navigation"),
                new Label("Load images soon..."),
                new Label("Thumbnails coming...")
        );
        sidebar.setPrefWidth(200);

        // CENTER: Image Viewer
        root.setCenter(ImageViewer.createViewer(stage));

        // LEFT attach
        root.setLeft(sidebar);

        return new Scene(root, 900, 600);
    }
}