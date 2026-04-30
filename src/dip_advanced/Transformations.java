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
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;

public class Transformations extends Application {

    private BufferedImage originalImage;
    private ImageView imageView = new ImageView();

    private Slider resizeSlider;
    private Slider rotateSlider;
    private Slider translateXSlider;
    private Slider translateYSlider;

    @Override
    public void start(Stage stage) {
        Button openButton = new Button("Open Image");
        Button applyButton = new Button("Apply Transform");
        Button resetButton = new Button("Reset");

        resizeSlider = new Slider(50, 200, 100);
        rotateSlider = new Slider(-180, 180, 0);
        translateXSlider = new Slider(-300, 300, 0);
        translateYSlider = new Slider(-300, 300, 0);

        ComboBox<Integer> rotateBox = new ComboBox<>();
        rotateBox.getItems().addAll(0, 90, 180, 270);
        rotateBox.setValue(0);

        setupSlider(resizeSlider);
        setupSlider(rotateSlider);
        setupSlider(translateXSlider);
        setupSlider(translateYSlider);

        imageView.setFitWidth(700);
        imageView.setFitHeight(500);
        imageView.setPreserveRatio(true);

        openButton.setOnAction(e -> openImage(stage));

        rotateBox.setOnAction(e -> rotateSlider.setValue(rotateBox.getValue()));

        applyButton.setOnAction(e -> applyTransform());

        resetButton.setOnAction(e -> {
            if (originalImage != null) {
                resizeSlider.setValue(100);
                rotateSlider.setValue(0);
                translateXSlider.setValue(0);
                translateYSlider.setValue(0);
                imageView.setImage(SwingFXUtils.toFXImage(originalImage, null));
            }
        });

        VBox controls = new VBox(15,
                openButton,
                new Label("Resize (%)"), resizeSlider,
                new Label("Rotate Slider"), rotateSlider,
                new Label("Rotate Dropdown"), rotateBox,
                new Label("Translate X"), translateXSlider,
                new Label("Translate Y"), translateYSlider,
                applyButton,
                resetButton
        );

        controls.setPadding(new Insets(20));
        controls.setPrefWidth(250);

        BorderPane root = new BorderPane();
        root.setLeft(controls);
        root.setCenter(imageView);

        Scene scene = new Scene(root, 1000, 650);
        stage.setTitle("Advanced DIP - Transformations");
        stage.setScene(scene);
        stage.show();
    }

    private void setupSlider(Slider slider) {
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(50);
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

    private void applyTransform() {
        if (originalImage == null) {
            return;
        }

        double scale = resizeSlider.getValue() / 100.0;
        double angle = rotateSlider.getValue();
        int moveX = (int) translateXSlider.getValue();
        int moveY = (int) translateYSlider.getValue();

        BufferedImage result = resize(originalImage, scale);
        result = rotate(result, angle);
        result = translate(result, moveX, moveY);

        imageView.setImage(SwingFXUtils.toFXImage(result, null));
    }

    public static BufferedImage resize(BufferedImage image, double scale) {
        int newWidth = Math.max(1, (int) (image.getWidth() * scale));
        int newHeight = Math.max(1, (int) (image.getHeight() * scale));

        BufferedImage resized = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g = resized.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(image, 0, 0, newWidth, newHeight, null);
        g.dispose();

        return resized;
    }

    public static BufferedImage rotate(BufferedImage image, double angle) {
        double radians = Math.toRadians(angle);

        int width = image.getWidth();
        int height = image.getHeight();

        BufferedImage rotated = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g = rotated.createGraphics();
        AffineTransform transform = new AffineTransform();
        transform.rotate(radians, width / 2.0, height / 2.0);

        g.drawImage(image, transform, null);
        g.dispose();

        return rotated;
    }

    public static BufferedImage translate(BufferedImage image, int x, int y) {
        BufferedImage translated = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);

        Graphics2D g = translated.createGraphics();
        g.drawImage(image, x, y, null);
        g.dispose();

        return translated;
    }

    public static void main(String[] args) {
        launch(args);
    }
}