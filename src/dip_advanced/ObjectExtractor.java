package dip_advanced;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;

public class ObjectExtractor extends Application {

    private BufferedImage originalImage;
    private BufferedImage extractedImage;
    private Color selectedColor;

    private ImageView imageView = new ImageView();
    private Label selectedColorLabel = new Label("Selected Color: None");
    private Slider toleranceSlider;

    @Override
    public void start(Stage stage) {
        Button openButton = new Button("Open Image");
        Button extractButton = new Button("Extract Object");
        Button saveButton = new Button("Save Extracted Image");
        Button resetButton = new Button("Reset");

        toleranceSlider = new Slider(10, 150, 60);
        toleranceSlider.setShowTickLabels(true);
        toleranceSlider.setShowTickMarks(true);

        imageView.setFitWidth(700);
        imageView.setFitHeight(500);
        imageView.setPreserveRatio(true);

        openButton.setOnAction(e -> openImage(stage));

        imageView.setOnMouseClicked(e -> pickColor(e.getX(), e.getY()));

        extractButton.setOnAction(e -> extractObject());

        saveButton.setOnAction(e -> saveImage(stage));

        resetButton.setOnAction(e -> {
            if (originalImage != null) {
                imageView.setImage(SwingFXUtils.toFXImage(originalImage, null));
                extractedImage = null;
                selectedColor = null;
                selectedColorLabel.setText("Selected Color: None");
            }
        });

        VBox controls = new VBox(15,
                openButton,
                new Label("Click image to pick object color"),
                selectedColorLabel,
                new Label("Tolerance"),
                toleranceSlider,
                extractButton,
                saveButton,
                resetButton
        );

        controls.setPadding(new Insets(20));
        controls.setPrefWidth(260);

        BorderPane root = new BorderPane();
        root.setLeft(controls);
        root.setCenter(imageView);

        Scene scene = new Scene(root, 1000, 650);
        stage.setTitle("Advanced DIP - Object Extraction");
        stage.setScene(scene);
        stage.show();
    }

    private void openImage(Stage stage) {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Image");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
            );

            File file = fileChooser.showOpenDialog(stage);

            if (file != null) {
                originalImage = ImageIO.read(file);
                imageView.setImage(SwingFXUtils.toFXImage(originalImage, null));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void pickColor(double mouseX, double mouseY) {
        if (originalImage == null) {
            return;
        }

        double displayedWidth = imageView.getBoundsInLocal().getWidth();
        double displayedHeight = imageView.getBoundsInLocal().getHeight();

        int imageX = (int) (mouseX * originalImage.getWidth() / displayedWidth);
        int imageY = (int) (mouseY * originalImage.getHeight() / displayedHeight);

        if (imageX >= 0 && imageY >= 0 && imageX < originalImage.getWidth() && imageY < originalImage.getHeight()) {
            selectedColor = new Color(originalImage.getRGB(imageX, imageY), true);
            selectedColorLabel.setText("Selected RGB: " +
                    selectedColor.getRed() + ", " +
                    selectedColor.getGreen() + ", " +
                    selectedColor.getBlue());
        }
    }

    private void extractObject() {
        if (originalImage == null || selectedColor == null) {
            return;
        }

        int tolerance = (int) toleranceSlider.getValue();
        extractedImage = extractByColor(originalImage, selectedColor, tolerance);
        imageView.setImage(SwingFXUtils.toFXImage(extractedImage, null));
    }

    public static BufferedImage extractByColor(BufferedImage image, Color targetColor, int tolerance) {
        int width = image.getWidth();
        int height = image.getHeight();

        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                Color current = new Color(image.getRGB(x, y), true);

                int rDiff = Math.abs(current.getRed() - targetColor.getRed());
                int gDiff = Math.abs(current.getGreen() - targetColor.getGreen());
                int bDiff = Math.abs(current.getBlue() - targetColor.getBlue());

                if (rDiff <= tolerance && gDiff <= tolerance && bDiff <= tolerance) {
                    result.setRGB(x, y, current.getRGB());
                } else {
                    result.setRGB(x, y, 0x00000000);
                }
            }
        }

        return result;
    }

    private void saveImage(Stage stage) {
        if (extractedImage == null) {
            return;
        }

        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Extracted Object");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("PNG Image", "*.png")
            );

            File file = fileChooser.showSaveDialog(stage);

            if (file != null) {
                ImageIO.write(extractedImage, "png", file);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}