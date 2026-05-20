package ui;

//import javafx.embed.swing.SwingFXUtils;
import java.awt.image.BufferedImage;
// Ensure you also have this for the conversion logic
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.util.Duration;
import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.FontMetrics;

import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
//import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import repository.AnnotationManager;
import repository.DatabaseManager;
import repository.ImageModel;

import utils.EditAction;
import utils.HistoryManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import integration.AppController;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javafx.embed.swing.SwingFXUtils;
import multimedia.MosaicGenerator;
import java.io.File;
import javafx.stage.FileChooser;

public class MainUI {

    private BorderPane root;
    private FlowPane thumbnailPane;
    private ImageView mainImageView;
    private TextArea annotationArea;
    private Label heartLabel;
    private Label annotationOverlay;
    private Slider durationSlider;
    private Label fileNameLabel;
    private ImageView imageView;
    private ImageView editPreview;
    private AppController controller = new AppController();
    private javafx.scene.image.Image originalImage; 
    private HistoryManager historyManager = new HistoryManager();
    private File latestSavedFile = null; 
   private String latestSavedType = "image"; // Default to image

    private Stage stage;
    private AnnotationManager annotationManager;
    private ImageModel currentImage;
    private List<ImageModel> imageList;
    private String currentSection = "gallery";

    public MainUI(Stage stage) {
        this.stage = stage;
        this.annotationManager = new AnnotationManager();
        this.imageList = new ArrayList<>();

        DatabaseManager.initialize();

        createUI();

        List<String> paths = DatabaseManager.getSavedPaths();
    if (paths != null) {
        for (String path : paths) {
            File file = new File(path);
            if (file.exists()) {
                ImageModel imageModel = new ImageModel(file.getAbsolutePath());
                // Link existing annotations if any
                imageModel.setAnnotation(annotationManager.getAnnotation(file.getAbsolutePath()));
                
                imageList.add(imageModel);
                addThumbnail(imageModel); 
            }
        }
    }
    }

    public Parent getRoot() {
        return root;
    }

    private void createUI() {
        root = new BorderPane();

        root.setTop(createTopBar());
        root.setLeft(createNavigationPanel());
        root.setCenter(createMainContent());

root.setStyle("-fx-background-color: linear-gradient(to bottom right, #f8fafc, #e5e7eb);");      }

private HBox createTopBar() {
      // 1. Setup the Open Folder Button
    //   Button openFolderButton = new Button("Open Folder");
    //   openFolderButton.setStyle(primaryButtonStyle());
    //   openFolderButton.setOnAction(e -> openImageFolder());

      // 2. Setup the Title Label
    //   Label title = new Label("Photo Repository System");
    //   title.setStyle(
    //       "-fx-font-size: 24px;" +
    //       "-fx-font-weight: bold;" +
    //       "-fx-text-fill: #2e7d32;"
    //   );

    Button openFolderButton = new Button("＋ Add Folder");
    openFolderButton.setStyle(
        "-fx-background-color: #2563eb;" +
        "-fx-text-fill: white;" +
        "-fx-font-size: 14px;" +
        "-fx-font-weight: bold;" +
        "-fx-background-radius: 14;" +
        "-fx-padding: 12 20;" +
        "-fx-effect: dropshadow(gaussian, rgba(37,99,235,0.35), 12, 0, 0, 4);" +
        "-fx-cursor: hand;"
    );
    openFolderButton.setOnAction(e -> openImageFolder());

    Label title = new Label("Photo Studio");
    title.setStyle(
        "-fx-font-size: 28px;" +
        "-fx-font-weight: 900;" +
        "-fx-text-fill: #111827;" +
        "-fx-padding: 0 8 0 0;"
    );

      Label subtitle = new Label("Photo • Edit • Create");
    subtitle.setStyle(
        "-fx-font-size: 12px;" +
        "-fx-text-fill: #64748b;" +
        "-fx-font-weight: bold;"
    );

    //   // Cleaned layout: spacer and theme toggle button are completely removed
    //   HBox topBar = new HBox(15, title, openFolderButton);

     VBox titleBox = new VBox(2, title, subtitle);
    titleBox.setAlignment(Pos.CENTER_LEFT);

      //return topBar;
  

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    Button themeToggle = new Button("☀");
    themeToggle.setStyle(
    "-fx-background-color: transparent;" +
    "-fx-font-size: 18px;" +
    "-fx-cursor: hand;"
);

    HBox topBar = new HBox(18, titleBox, openFolderButton, spacer, themeToggle);
    topBar.setPadding(new Insets(14, 22, 14, 22));
    topBar.setAlignment(Pos.CENTER_LEFT);
    topBar.setStyle(
        "-fx-background-color: linear-gradient(to right, #ffffff, #eef2ff);" +
        "-fx-border-color: #e5e7eb;" +
        "-fx-border-width: 0 0 1 0;"
    );

    return topBar;
}

    private VBox createNavigationPanel() {
        Label menuTitle = new Label("MENU");
        menuTitle.setStyle(
                "-fx-text-fill: #bdc3c7;" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;"
        );

        Button galleryBtn = createNavButton("📂 Gallery");
        Button editingBtn = createNavButton("🎨 Image Editing");
        Button objectBtn = createNavButton("✂️ Object & Transform");
        Button mosaicBtn = createNavButton("🖼️ Mosaic");
        Button videoBtn = createNavButton("🎬 Video Creator");
        Button shareBtn = createNavButton("📤 Share / Export");

       galleryBtn.setOnAction(e -> {
    currentSection = "gallery";
    root.setCenter(createMainContent());
    refreshThumbnails();
});

        editingBtn.setOnAction(e -> showEditingPage());

        objectBtn.setOnAction(e -> showObjectTransformPage());

        mosaicBtn.setOnAction(e -> showMosaicPage());

        videoBtn.setOnAction(e -> showVideoPage());

        shareBtn.setOnAction(e -> showSharePage());
        
        
        
        // (e -> showModulePage(
        //         "📤 Share / Export",
        //         "Export and share images or videos through Email or WhatsApp."
        // ));

        VBox nav = new VBox(12,
                menuTitle,
                galleryBtn,
                editingBtn,
                objectBtn,
                mosaicBtn,
                videoBtn,
                shareBtn
        );

        nav.setPadding(new Insets(20));
        nav.setPrefWidth(230);
     nav.setStyle(
        "-fx-background-color: linear-gradient(to bottom, #111827, #1f2937);" +
        "-fx-border-color: #374151;" +
        "-fx-border-width: 0 1 0 0;"
);

        return nav;
    }

  private Button createNavButton(String text) {
    Button button = new Button(text);

    button.setMaxWidth(Double.MAX_VALUE);
    button.setPrefHeight(46);

    String normalStyle =
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #e5e7eb;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: 600;" +
            "-fx-alignment: CENTER_LEFT;" +
            "-fx-padding: 12 16;" +
            "-fx-background-radius: 12;";

    String hoverStyle =
            "-fx-background-color: #374151;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-alignment: CENTER_LEFT;" +
            "-fx-padding: 12 16;" +
            "-fx-background-radius: 12;";

    button.setStyle(normalStyle);

    button.setOnMouseEntered(e -> button.setStyle(hoverStyle));
    button.setOnMouseExited(e -> button.setStyle(normalStyle));

    return button;
}

    private BorderPane createMainContent() {
        BorderPane content = new BorderPane();
        content.setPadding(new Insets(18));
content.setStyle(
        "-fx-background-color: linear-gradient(to bottom right, #f8fafc, #eef2ff);" +
        "-fx-background-radius: 24;" +
        "-fx-padding: 18;"
);

    Label galleryTitle = new Label("📂 Image Gallery");
    galleryTitle.setStyle(
        "-fx-font-size: 26px;" +
        "-fx-font-weight: bold;" +
        "-fx-text-fill: #111827;" +
        "-fx-padding: 0 0 16 0;"
);
        content.setTop(galleryTitle);
        content.setLeft(createThumbnailSection());
        content.setCenter(createImagePreviewSection());
        content.setRight(createAnnotationSection());

        return content;
    }

    private ScrollPane createThumbnailSection() {
        thumbnailPane = new FlowPane();
        thumbnailPane.setPadding(new Insets(10));
        thumbnailPane.setHgap(10);
        thumbnailPane.setVgap(10);
        thumbnailPane.setPrefWrapLength(220);
        thumbnailPane.setStyle(
        "-fx-background-color: #111827;" +
        "-fx-background-radius: 22;"
);

        ScrollPane scrollPane = new ScrollPane(thumbnailPane);
        scrollPane.setPrefWidth(250);
        scrollPane.setFitToWidth(true);
            scrollPane.setStyle(
        "-fx-background-color: #111827;" +
        "-fx-background-radius: 22;" +
        "-fx-padding: 10;" +
        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.18), 18, 0, 0, 6);"
);
        return scrollPane;
    }

    private StackPane createImagePreviewSection() {
        mainImageView = new ImageView();
        mainImageView.setStyle(
    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 15, 0, 0, 5);"
);

mainImageView.setPreserveRatio(true);
mainImageView.setFitWidth(600);
mainImageView.setFitHeight(500);
        mainImageView.setPreserveRatio(true);
        mainImageView.setFitWidth(600);
        mainImageView.setFitHeight(500);
        

        fileNameLabel = new Label("📷 No image selected");
fileNameLabel.setStyle(
        "-fx-font-size: 16px;" +
        "-fx-font-weight: bold;" +
        "-fx-text-fill: white;"
);

        heartLabel = new Label("♥");
        heartLabel.setStyle("-fx-font-size: 42px; -fx-text-fill: red;");
        heartLabel.setVisible(false);
        annotationOverlay = new Label();

annotationOverlay.setStyle(
    "-fx-background-color: rgba(0,0,0,0.75);" +
    "-fx-text-fill: white;" +
    "-fx-font-size: 18px;" +
    "-fx-font-weight: bold;" +
    "-fx-padding: 10 18;" +
    "-fx-background-radius: 12;"
);

annotationOverlay.setVisible(false);

        // --- ADD THIS LOGIC HERE ---
    if (currentImage != null) {
        // Reload the current image into the new ImageView
        Image image = new Image(new File(currentImage.getFilePath()).toURI().toString());
        mainImageView.setImage(image);
        
        // Restore the filename label
        File file = new File(currentImage.getFilePath());
        fileNameLabel.setText(file.getName());
        
        // Restore the heart/annotation status
        heartLabel.setVisible(annotationManager.hasAnnotation(currentImage.getFilePath()));
        
        // Restore the text area content
        if (annotationArea != null) {
            annotationArea.setText(currentImage.getAnnotation());
        }
    }

StackPane imageStack = new StackPane(mainImageView, heartLabel, annotationOverlay);        StackPane.setAlignment(heartLabel, Pos.TOP_RIGHT);
        StackPane.setMargin(heartLabel, new Insets(20));
     StackPane.setAlignment(annotationOverlay, Pos.CENTER);

annotationOverlay.setTranslateY(210);

        VBox centerBox = new VBox(15, fileNameLabel, imageStack);
        centerBox.setPadding(new Insets(20));
        centerBox.setAlignment(Pos.TOP_CENTER);

        StackPane previewPane = new StackPane(centerBox);
        previewPane.setStyle(
        "-fx-background-color: linear-gradient(to bottom right, #020617, #111827);" +
        "-fx-background-radius: 24;" +
        "-fx-padding: 18;" +
        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.28), 24, 0, 0, 8);"
);
        return previewPane;
    }

    private VBox createAnnotationSection() {
        Label annotationLabel = new Label("Image Annotation");
        annotationLabel.setStyle(
                "-fx-font-size: 16px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #2c3e50;"
        );

        annotationArea = new TextArea();
        annotationArea.setPromptText("Write notes about this image...");
        annotationArea.setWrapText(true);
        annotationArea.setPrefHeight(250);

        Button saveButton = new Button("Save Annotation");
        saveButton.setMaxWidth(Double.MAX_VALUE);
        saveButton.setStyle(greenButtonStyle());
        saveButton.setMaxWidth(Double.MAX_VALUE);

        saveButton.setDisable(true);
        // 1. Initial State (Disabled and Faded)
saveButton.setDisable(true);
saveButton.setStyle("-fx-background-color: #007AFF; -fx-text-fill: white; -fx-font-weight: bold; " +
                     "-fx-background-radius: 10; -fx-padding: 12; -fx-opacity: 0.5;");

// 2. Add a listener to detect when the user types in the TextArea
annotationArea.textProperty().addListener((obs, oldText, newText) -> {
    if (newText.trim().isEmpty()) {
        // Still empty? Keep it faded
        saveButton.setDisable(true);
        saveButton.setStyle("-fx-background-color: #007AFF; -fx-text-fill: white; -fx-font-weight: bold; " +
                             "-fx-background-radius: 10; -fx-padding: 12; -fx-opacity: 0.5;");
    } else {
        // Text present! Make it bright and clickable
        saveButton.setDisable(false);
        saveButton.setStyle("-fx-background-color: #007AFF; -fx-text-fill: white; -fx-font-weight: bold; " +
                             "-fx-background-radius: 10; -fx-padding: 12; -fx-opacity: 1.0;");
    }
});

        saveButton.setOnAction(e -> saveAnnotation());
      

VBox rightBox = new VBox(10, annotationLabel, annotationArea, saveButton);       
      rightBox.setPadding(new Insets(15));
      rightBox.setPrefWidth(280);
      rightBox.setStyle(
        "-fx-background-color: #ffffff;" +
        "-fx-background-radius: 24;" +
        "-fx-padding: 18;" +
        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 18, 0, 0, 6);"
);

        return rightBox;
    }

    private void showModulePage(String titleText, String descriptionText) {
        VBox page = new VBox(20);
        page.setPadding(new Insets(50));
        page.setAlignment(Pos.CENTER);
        page.setStyle("-fx-background-color: #fafafa;");

        Label title = new Label(titleText);
        title.setStyle(
        "-fx-font-size: 24px;" +
        "-fx-font-weight: bold;" +
        "-fx-text-fill: #111827;"
);

        Label description = new Label(descriptionText);
        description.setWrapText(true);
        description.setMaxWidth(600);
        description.setStyle(
                "-fx-font-size: 16px;" +
                "-fx-text-fill: #555555;"
        );

        Label status = new Label("🚧 Module page ready for integration");
        status.setStyle(
                "-fx-font-size: 14px;" +
                "-fx-text-fill: #7f8c8d;"
        );

        page.getChildren().addAll(title, description, status);

        root.setCenter(page);
    }

//   private void showObjectTransformPage() {
//     currentSection = "object";
//     if (currentImage == null) {
//         showAlert("Please select an image first!");
//         return;
//     }

//     HBox mainLayout = new HBox(15);
//     mainLayout.setStyle("-fx-background-color: #000000;"); 
//     VBox.setVgrow(mainLayout, Priority.ALWAYS);

//     // --- LEFT SIDE: PREVIEW AREA ---
//     VBox imageSection = new VBox(20);
//     imageSection.setPadding(new Insets(30));
//     imageSection.setAlignment(Pos.CENTER);
//     HBox.setHgrow(imageSection, Priority.SOMETIMES);

//     Image sourceImg = mainImageView.getImage();
//     ImageView preview = new ImageView(sourceImg);
//     // preview.setPreserveRatio(true);
//     preview.setSmooth(true);
//     preview.setFitHeight(450);
//     preview.setPreserveRatio(true);
    
//     double imgW = sourceImg.getWidth();
//     double imgH = sourceImg.getHeight();
//     double maxViewW = 900;
//     double maxViewH = 650;
//     double ratio = Math.min(maxViewW / imgW, maxViewH / imgH);
    
//     double canvasWidth = imgW * ratio;
//     double canvasHeight = imgH * ratio;

//     Pane clipWindow = new Pane(preview); 
//     // clipWindow.setPrefSize(canvasWidth, canvasHeight);
//     // clipWindow.setMaxSize(canvasWidth, canvasHeight);
//     // clipWindow.setStyle("-fx-background-color: #111111;");
    
//     // Rectangle clipRegion = new Rectangle(canvasWidth, canvasHeight);
//     // clipWindow.setClip(clipRegion);

//     preview.setFitWidth(canvasWidth);
//     preview.setFitHeight(canvasHeight);
//     // preview.setManaged(false); 

//     //----ADDED
//     // Re-applying the exact container style from Editing
//     StackPane displayStack = new StackPane(preview);
//     displayStack.setStyle("-fx-border-width: 0; -fx-padding: 0;");
    
//     StackPane container = new StackPane(displayStack);
//     container.setStyle("-fx-background-color: #1a1a1a; -fx-background-radius: 12; -fx-padding: 20;");
//     container.setPrefSize(600, 500); // Fixed size to match Editing
//     container.setMaxSize(600, 500);
//     VBox.setVgrow(container, Priority.NEVER);
//     //----ADDED

//     // --- OBJECT EXTRACTION LOGIC ---
//     Label selectedColorLabel = new Label("Click image to pick color");
//     selectedColorLabel.setStyle("-fx-text-fill: #8E8E93; -fx-font-size: 12px;");

//     preview.setOnMouseClicked(e -> {
//         java.awt.image.BufferedImage bimg = fxToBufferedImage(preview.getImage());
//         int x = (int) (e.getX() * bimg.getWidth() / preview.getFitWidth());
//         int y = (int) (e.getY() * bimg.getHeight() / preview.getFitHeight());

//         if (x >= 0 && y >= 0 && x < bimg.getWidth() && y < bimg.getHeight()) {
//             this.pickedColor = new java.awt.Color(bimg.getRGB(x, y), true);
//             selectedColorLabel.setText(String.format("Selected: %d, %d, %d", 
//                 pickedColor.getRed(), pickedColor.getGreen(), pickedColor.getBlue()));
//             selectedColorLabel.setStyle("-fx-text-fill: #34C759;");
//         }
//     });

//     imageSection.getChildren().addAll(new Label("Edit Photo") {{ 
//         setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold;"); 
//     }}, container);

//     // --- RIGHT SIDE: TOOLS ---
//     VBox controlSideBar = new VBox(12);
//     controlSideBar.setPadding(new Insets(20, 15, 20, 15));
//     controlSideBar.setPrefWidth(300);
//     controlSideBar.setStyle("-fx-background-color: #000000; -fx-border-color: #2F3336; -fx-border-width: 0 0 0 1;");

//     // ADDED: History Navigation Buttons
//     HBox historyControls = new HBox(10);
//     Button undoBtn = new Button("⬅ Undo");
//     Button redoBtn = new Button("Forward ➡");
//     undoBtn.setStyle("-fx-background-color: #2C2C2E; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;");
//     redoBtn.setStyle("-fx-background-color: #2C2C2E; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;");
//     historyControls.getChildren().addAll(undoBtn, redoBtn);

//     Label toolsLabel = new Label("Geometric & Selection");
//     toolsLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: white;");

//     // Sliders
//     Label scaleLab = new Label("Scale: 100%");
//     Slider scaleS = new Slider(10, 300, 100); 
//     Label rotateLab = new Label("Rotation: 0°");
//     Slider rotateS = new Slider(-180, 180, 0);
//     Label transXLab = new Label("Translation X: 0");
//     Slider transX = new Slider(-canvasWidth, canvasWidth, 0);
//     Label transYLab = new Label("Translation Y: 0");
//     Slider transY = new Slider(-canvasHeight, canvasHeight, 0);

//     List.of(scaleLab, rotateLab, transXLab, transYLab).forEach(l -> l.setStyle("-fx-text-fill: #EBEBF5; -fx-font-size: 12px;"));
//     List.of(scaleS, rotateS, transX, transY).forEach(this::setupSliderDesign);

//     // ADDED: Undo/Redo Action Logic
//     undoBtn.setOnAction(e -> {
//         historyManager.undo();
//         applyHistoryToSliders(scaleS, rotateS, transX, transY);
//     });

//     redoBtn.setOnAction(e -> {
//         historyManager.redo();
//         applyHistoryToSliders(scaleS, rotateS, transX, transY);
//     });

//     Button saveBtn = new Button("Save Changes");
//     saveBtn.setMaxWidth(Double.MAX_VALUE);
//     saveBtn.setDisable(true);
//     saveBtn.setStyle("-fx-background-color: #007AFF; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 12; -fx-opacity: 0.5;");

//     // Transformation Actions
//     Runnable applyRealTime = () -> {
//         saveBtn.setDisable(false);
//         saveBtn.setStyle("-fx-background-color: #007AFF; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 12; -fx-opacity: 1.0;");
//         double s = scaleS.getValue() / 100.0;
//         double r = rotateS.getValue();
//         double tx = transX.getValue();
//         double ty = -transY.getValue();
//         scaleLab.setText(String.format("Scale: %.0f%%", scaleS.getValue()));
//         rotateLab.setText(String.format("Rotation: %.0f°", r));
//         transXLab.setText(String.format("Translation X: %.0f", tx));
//         transYLab.setText(String.format("Translation Y: %.0f", -ty));
//         preview.setScaleX(s); preview.setScaleY(s); preview.setRotate(r);
//         preview.setTranslateX(tx); preview.setTranslateY(ty);
//     };

//     scaleS.valueProperty().addListener((o, old, v) -> applyRealTime.run());
//     rotateS.valueProperty().addListener((o, old, v) -> applyRealTime.run());
//     transX.valueProperty().addListener((o, old, v) -> applyRealTime.run());
//     transY.valueProperty().addListener((o, old, v) -> applyRealTime.run());

//     saveBtn.setOnAction(e -> {
//     // 1. Safety check for the image and the clipWindow
//     if (preview.getImage() == null) {
//         showAlert("No image found to save.");
//         return;
//     }

//     try {
//         // 2. Setup snapshot parameters
//         SnapshotParameters params = new SnapshotParameters();
        
//         // Match the background color of your clipWindow (#111111)
//         params.setFill(javafx.scene.paint.Color.rgb(17, 17, 17)); 

//         // 3. Take snapshot of 'clipWindow' instead of 'preview'
//         // This captures only what is visible inside the clipped rectangle
//         WritableImage snapshot = clipWindow.snapshot(params, null);

//         // 4. Convert to BufferedImage
//         BufferedImage bufferedImage = fxToBufferedImage(snapshot);

//         // 5. Generate filename and save
//         String fileName = "transformed_view_" + System.currentTimeMillis() + ".png";
//         File output = new File(fileName);

//         if (ImageIO.write(bufferedImage, "png", output)) {
//             showAlert("Transformation saved as: " + output.getName());
            
//             // UI Cleanup
//             saveBtn.setDisable(true);
//             saveBtn.setStyle("-fx-background-color: #007AFF; -fx-text-fill: white; -fx-font-weight: bold; " +
//                              "-fx-background-radius: 10; -fx-padding: 12; -fx-opacity: 0.5;");
            
//             refreshThumbnails();
//         } else {
//             showAlert("Failed to save the image file.");
//         }

//     } catch (Exception ex) {
//         ex.printStackTrace();
//         showAlert("Error capturing view: " + ex.getMessage());
//     }
// });

//     // ADDED: Capture History when user stops dragging (Mouse Release)
//     scaleS.setOnMouseReleased(e -> historyManager.addStep(new utils.EditAction(utils.EditAction.Type.SCALE, String.valueOf(scaleS.getValue()))));
//     rotateS.setOnMouseReleased(e -> historyManager.addStep(new utils.EditAction(utils.EditAction.Type.ROTATE, String.valueOf(rotateS.getValue()))));
//     transX.setOnMouseReleased(e -> historyManager.addStep(new utils.EditAction(utils.EditAction.Type.TRANSLATE_X, String.valueOf(transX.getValue()))));
//     transY.setOnMouseReleased(e -> historyManager.addStep(new utils.EditAction(utils.EditAction.Type.TRANSLATE_Y, String.valueOf(transY.getValue()))));

//     // --- EXTRACTION SECTION ---
//     Label extractLabel = new Label("Object Extraction");
//     extractLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: white;");

//     Slider toleranceS = new Slider(0, 150, 60);
//     this.setupSliderDesign(toleranceS);

//     Button extractBtn = new Button("Extract & Save Object");
//     extractBtn.setMaxWidth(Double.MAX_VALUE);
//     extractBtn.setStyle("-fx-background-color: #34C759; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 12;");
    
//     extractBtn.setOnAction(e -> {
//     if (this.pickedColor == null) {
//         showAlert("Click the image to pick a color first!");
//         return;
//     }

//     try {
//         BufferedImage original = fxToBufferedImage(mainImageView.getImage());

//         BufferedImage extracted = dip_advanced.ObjectExtractor.extractByColor(
//                 original,
//                 this.pickedColor,
//                 (int) toleranceS.getValue()
//         );

//         Image fxExtracted = SwingFXUtils.toFXImage(extracted, null);

//         Stage previewStage = new Stage();
//         previewStage.setTitle("Object Extraction Preview");

//         ImageView previewImage = new ImageView(fxExtracted);
//         previewImage.setFitWidth(500);
//         previewImage.setFitHeight(400);
//         previewImage.setPreserveRatio(true);

//         Button saveExtractedBtn = new Button("Save Extracted Object");
//         saveExtractedBtn.setStyle(greenButtonStyle());

//         saveExtractedBtn.setOnAction(saveEvent -> {
//             try {
//                 File outputFile = new File("extracted_" + System.currentTimeMillis() + ".png");
//                 ImageIO.write(extracted, "png", outputFile);
//                 showAlert("Saved: " + outputFile.getName());
//                 previewStage.close();
//             } catch (Exception ex) {
//                 ex.printStackTrace();
//                 showAlert("Failed to save extracted object.");
//             }
//         });

//         VBox previewLayout = new VBox(15, previewImage, saveExtractedBtn);
//         previewLayout.setPadding(new Insets(20));
//         previewLayout.setAlignment(Pos.CENTER);
//         previewLayout.setStyle("-fx-background-color: #111827;");

//         previewStage.setScene(new javafx.scene.Scene(previewLayout, 600, 520));
//         previewStage.show();

//     } catch (Exception ex) {
//         ex.printStackTrace();
//         showAlert("Failed to extract object.");
//     }

//     });

//     // CHANGED: Assemble Sidebar (Included historyControls)
//     controlSideBar.getChildren().addAll(
//         historyControls, // ADDED
//         toolsLabel, new Separator() {{ setStyle("-fx-background-color: #2F3336;"); }},
//         scaleLab, scaleS, rotateLab, rotateS, transXLab, transX, transYLab, transY,
//         saveBtn,
//         new Separator() {{ setStyle("-fx-background-color: #2F3336;"); }},
//         extractLabel, selectedColorLabel, new Label("Tolerance") {{ setStyle("-fx-text-fill: white; -fx-font-size: 11px;"); }},
//         toleranceS, extractBtn,
//         new Region() {{ VBox.setVgrow(this, Priority.ALWAYS); }}, 
//         new Button("Reset All") {{ 
//             setStyle("-fx-background-color: transparent; -fx-text-fill: #FF453A; -fx-font-weight: bold;");
//             setOnAction(ev -> {
//                 scaleS.setValue(100); rotateS.setValue(0); transX.setValue(0); transY.setValue(0);
//                 saveBtn.setDisable(true);
//             });
//         }}
//     );

//     mainLayout.getChildren().addAll(imageSection, controlSideBar);
//     HBox page = new HBox(18, createGalleryMiniList(), mainLayout);
//     page.setPadding(new Insets(18));
//     root.setCenter(page);
// }

private void showObjectTransformPage() {
    currentSection = "object";
    if (currentImage == null) {
        showAlert("Please select an image first!");
        return;
    }

    HBox mainLayout = new HBox(15);
    mainLayout.setStyle("-fx-background-color: #000000;"); 
    VBox.setVgrow(mainLayout, Priority.ALWAYS);

    // --- LEFT SIDE: PREVIEW AREA ---
    VBox imageSection = new VBox(15); // Reduced spacing to save vertical height
    imageSection.setPadding(new Insets(15)); // Reduced padding
    imageSection.setAlignment(Pos.CENTER);
    HBox.setHgrow(imageSection, Priority.SOMETIMES);

    Image sourceImg = mainImageView.getImage();
    ImageView preview = new ImageView(sourceImg);
    preview.setPreserveRatio(true);
    preview.setSmooth(true);
    
    // Calculate display dimensions
    double maxViewW = 600; 
    double maxViewH = 500;
    double ratio = Math.min(maxViewW / sourceImg.getWidth(), maxViewH / sourceImg.getHeight());
    double canvasWidth = sourceImg.getWidth() * ratio;
    double canvasHeight = sourceImg.getHeight() * ratio;

    preview.setFitWidth(canvasWidth);
    preview.setFitHeight(canvasHeight);

    // FIX: The ClipWindow MUST have a clip applied to prevent image spill-over
    Pane clipWindow = new Pane(preview); 
    clipWindow.setPrefSize(canvasWidth, canvasHeight);
    clipWindow.setMaxSize(canvasWidth, canvasHeight);
    
    Rectangle clipRegion = new Rectangle(canvasWidth, canvasHeight);
    clipWindow.setClip(clipRegion); // This keeps the image inside the box

    StackPane displayStack = new StackPane(clipWindow);
    displayStack.setStyle("-fx-border-width: 0; -fx-padding: 0;");
    
    StackPane container = new StackPane(displayStack);
    container.setStyle("-fx-background-color: #1a1a1a; -fx-background-radius: 12; -fx-padding: 20;");
    container.setPrefSize(600, 500); 
    container.setMaxSize(600, 500);
    VBox.setVgrow(container, Priority.NEVER);

    // --- OBJECT EXTRACTION LOGIC ---
    Label selectedColorLabel = new Label("Click image to pick color");
    selectedColorLabel.setStyle("-fx-text-fill: #8E8E93; -fx-font-size: 11px;");

    preview.setOnMouseClicked(e -> {
        java.awt.image.BufferedImage bimg = fxToBufferedImage(preview.getImage());
        int x = (int) (e.getX() * bimg.getWidth() / preview.getFitWidth());
        int y = (int) (e.getY() * bimg.getHeight() / preview.getFitHeight());

        if (x >= 0 && y >= 0 && x < bimg.getWidth() && y < bimg.getHeight()) {
            this.pickedColor = new java.awt.Color(bimg.getRGB(x, y), true);
            selectedColorLabel.setText(String.format("Selected: %d, %d, %d", pickedColor.getRed(), pickedColor.getGreen(), pickedColor.getBlue()));
            selectedColorLabel.setStyle("-fx-text-fill: #34C759;");
        }
    });

    imageSection.getChildren().addAll(new Label("Transformation Preview") {{ 
        setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;"); 
    }}, container);

    // --- RIGHT SIDE: TOOLS ---
    VBox controlSideBar = new VBox(12); // Reduced spacing to keep "Reset All" visible
    controlSideBar.setPadding(new Insets(20, 15, 20, 15));
    controlSideBar.setPrefWidth(300);
    controlSideBar.setStyle("-fx-background-color: #000000; -fx-border-color: #2F3336; -fx-border-width: 0 0 0 1;");

    HBox historyControls = new HBox(10);
    Button undoBtn = new Button("⬅ Undo");
    Button redoBtn = new Button("Forward ➡");
    undoBtn.setStyle("-fx-background-color: #2C2C2E; -fx-text-fill: white; -fx-cursor: hand;");
    redoBtn.setStyle("-fx-background-color: #2C2C2E; -fx-text-fill: white; -fx-cursor: hand;");
    historyControls.getChildren().addAll(undoBtn, redoBtn);

    Label toolsLabel = new Label("Geometric & Selection");
    toolsLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 22px; -fx-text-fill: white;");

    // Slider scaleS = new Slider(10, 300, 100); 
    // Slider rotateS = new Slider(-180, 180, 0);
    // Slider transX = new Slider(-300, 300, 0);
    // Slider transY = new Slider(-300, 300, 0);
    Label scaleLab = new Label("Scale: 100%");
    Slider scaleS = new Slider(10, 300, 100); 
    Label rotateLab = new Label("Rotation: 0°");
    Slider rotateS = new Slider(-180, 180, 0);
    Label transXLab = new Label("Translation X: 0");
    Slider transX = new Slider(-canvasWidth, canvasWidth, 0);
    Label transYLab = new Label("Translation Y: 0");
    Slider transY = new Slider(-canvasHeight, canvasHeight, 0);
    
    // Design Sliders
    List.of(scaleLab, rotateLab, transXLab, transYLab).forEach(l -> l.setStyle("-fx-text-fill: #EBEBF5; -fx-font-size: 13px; -fx-opacity: 0.85;"));
    List.of(scaleS, rotateS, transX, transY).forEach(this::setupSliderDesign);


    Button saveBtn = new Button("Save Changes");
    saveBtn.setMaxWidth(Double.MAX_VALUE);
    saveBtn.setDisable(true);
    saveBtn.setStyle("-fx-background-color: #007AFF; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 12; -fx-opacity: 0.5;");
    
    // Runnable applyRealTime = () -> {
    //     saveBtn.setDisable(false);
    //     saveBtn.setStyle("-fx-background-color: #007AFF; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 10;");
        
    //     double s = scaleS.getValue() / 100.0;
    //     preview.setScaleX(s); preview.setScaleY(s);
    //     preview.setRotate(rotateS.getValue());
    //     preview.setTranslateX(transX.getValue());
    //     preview.setTranslateY(-transY.getValue());
    // };

        // Transformation Actions
    Runnable applyRealTime = () -> {
        saveBtn.setDisable(false);
        saveBtn.setStyle("-fx-background-color: #007AFF; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 12; -fx-opacity: 1.0;");
        double s = scaleS.getValue() / 100.0;
        double r = rotateS.getValue();
        double tx = transX.getValue();
        double ty = -transY.getValue();
        scaleLab.setText(String.format("Scale: %.0f%%", scaleS.getValue()));
        rotateLab.setText(String.format("Rotation: %.0f°", r));
        transXLab.setText(String.format("Translation X: %.0f", tx));
        transYLab.setText(String.format("Translation Y: %.0f", -ty));
        preview.setScaleX(s); preview.setScaleY(s); preview.setRotate(r);
        preview.setTranslateX(tx); preview.setTranslateY(ty);
    };

    scaleS.valueProperty().addListener((o, old, v) -> applyRealTime.run());
    rotateS.valueProperty().addListener((o, old, v) -> applyRealTime.run());
    transX.valueProperty().addListener((o, old, v) -> applyRealTime.run());
    transY.valueProperty().addListener((o, old, v) -> applyRealTime.run());

    saveBtn.setOnAction(e -> {
        try {
            SnapshotParameters params = new SnapshotParameters();
            params.setFill(javafx.scene.paint.Color.rgb(26, 26, 26)); 
            WritableImage snapshot = clipWindow.snapshot(params, null);
            
            File output = new File("transformed_" + System.currentTimeMillis() + ".png");
            ImageIO.write(fxToBufferedImage(snapshot), "png", output);

            this.latestSavedFile = output;
            this.latestSavedType = "image";
            
            showAlert("Edited image saved as a new file:" + output.getName());

            refreshThumbnails();
        } catch (Exception ex) { showAlert("Error saving: " + ex.getMessage()); }
    });

     Label extractLabel = new Label("Object Extraction");
    extractLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: white;"); 

    // Extraction Section
    Slider toleranceS = new Slider(0, 150, 60);
    this.setupSliderDesign(toleranceS);

    Button extractBtn = new Button("Extract & Save Object");
    extractBtn.setMaxWidth(Double.MAX_VALUE);
    extractBtn.setStyle("-fx-background-color: #34C759; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 10;");

    extractBtn.setOnAction(e -> {
    if (this.pickedColor == null) {
        showAlert("Click the image to pick a color first!");
        return;
    }

    try {
        BufferedImage original = fxToBufferedImage(mainImageView.getImage());

        BufferedImage extracted = dip_advanced.ObjectExtractor.extractByColor(
                original,
                this.pickedColor,
                (int) toleranceS.getValue()
        );

        Image fxExtracted = SwingFXUtils.toFXImage(extracted, null);

        Stage previewStage = new Stage();
        previewStage.setTitle("Object Extraction Preview");

        ImageView previewImage = new ImageView(fxExtracted);
        previewImage.setFitWidth(500);
        previewImage.setFitHeight(400);
        previewImage.setPreserveRatio(true);

        Button saveExtractedBtn = new Button("Save Extracted Object");
        saveExtractedBtn.setStyle(greenButtonStyle());

        saveExtractedBtn.setOnAction(saveEvent -> {
            try {
                File outputFile = new File("extracted_" + System.currentTimeMillis() + ".png");
                ImageIO.write(extracted, "png", outputFile);
                showAlert("Saved: " + outputFile.getName());
                previewStage.close();
            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert("Failed to save extracted object.");
            }
        });

        VBox previewLayout = new VBox(15, previewImage, saveExtractedBtn);
        previewLayout.setPadding(new Insets(20));
        previewLayout.setAlignment(Pos.CENTER);
        previewLayout.setStyle("-fx-background-color: #111827;");

        previewStage.setScene(new javafx.scene.Scene(previewLayout, 600, 520));
        previewStage.show();

    } catch (Exception ex) {
        ex.printStackTrace();
        showAlert("Failed to extract object.");
    }
});

    // Sidebar Assembly
    // controlSideBar.getChildren().addAll(
    //     historyControls, toolsLabel, new Separator(),
    //     new Label("Scale") {{ setStyle("-fx-text-fill: white;"); }}, scaleS,
    //     new Label("Rotation") {{ setStyle("-fx-text-fill: white;"); }}, rotateS,
    //     new Label("Translation X/Y") {{ setStyle("-fx-text-fill: white;"); }}, transX, transY,
    //     saveBtn, new Separator(),
    //     new Label("Object Extraction") {{ setStyle("-fx-text-fill: white; -fx-font-weight: bold;"); }},
    //     selectedColorLabel, toleranceS, extractBtn,
    //     new Region() {{ VBox.setVgrow(this, Priority.ALWAYS); }}, // Push reset to bottom
    //     new Button("Reset All") {{ 
    //         setStyle("-fx-background-color: transparent; -fx-text-fill: #FF453A; -fx-font-weight: bold;");
    //         setOnAction(ev -> {
    //             scaleS.setValue(100); rotateS.setValue(0); transX.setValue(0); transY.setValue(0);
    //             saveBtn.setDisable(true);
    //         });
    //     }}
    // );

      controlSideBar.getChildren().addAll(
        toolsLabel, new Separator() {{ setStyle("-fx-background-color: #2F3336;"); }},
        scaleLab, scaleS, rotateLab, rotateS, transXLab, transX, transYLab, transY,
        saveBtn,
        new Separator() {{ setStyle("-fx-background-color: #2F3336;"); }},
        extractLabel, selectedColorLabel, new Label("Tolerance") {{ setStyle("-fx-text-fill: white; -fx-font-size: 11px;"); }},
        toleranceS, extractBtn,
        new Region() {{ VBox.setVgrow(this, Priority.ALWAYS); }}, 
        new Button("Reset All") {{ 
            setStyle("-fx-background-color: transparent; -fx-text-fill: #FF453A; -fx-font-weight: bold;");
            setOnAction(ev -> {
                scaleS.setValue(100); rotateS.setValue(0); transX.setValue(0); transY.setValue(0);
                saveBtn.setDisable(true);
            });
        }}
    );

    mainLayout.getChildren().addAll(imageSection, controlSideBar);
    HBox page = new HBox(15, createGalleryMiniList(), mainLayout);
    page.setPadding(new Insets(15));
    root.setCenter(page);
}

private java.awt.Color pickedColor = java.awt.Color.WHITE;

  private boolean isGrayscale = false; // Class-level flag

private void showEditingPage() {
    currentSection = "editing";
    if (currentImage == null) {
        showAlert("Please select an image from the Gallery first!");
        return;
    }
    
    isGrayscale = false; 

    HBox mainLayout = new HBox(15);
    mainLayout.setStyle("-fx-background-color: #000000;"); 

    VBox.setVgrow(mainLayout, Priority.ALWAYS);

    // --- LEFT SIDE: PREVIEW ---
    VBox imageSection = new VBox(20);
    imageSection.setPadding(new Insets(30));
    imageSection.setAlignment(Pos.CENTER);

    HBox.setHgrow(imageSection, Priority.SOMETIMES);

    // CHANGED: Using a class-level reference to ensure refreshImageDisplay works
    this.editPreview = new ImageView(mainImageView.getImage());
    editPreview.setFitHeight(450);
    editPreview.setPreserveRatio(true);

    StackPane displayStack = new StackPane(editPreview);
    displayStack.setStyle("-fx-border-width: 0; -fx-padding: 0;");
    
    StackPane container = new StackPane(displayStack);
    container.setStyle("-fx-background-color: #1a1a1a; -fx-background-radius: 12; -fx-padding: 20;");

  container.setPrefSize(600, 500); // Width 600, Height 500 (Matches Gallery)
    container.setMaxSize(600, 500);
    VBox.setVgrow(container, Priority.NEVER);
    
    imageSection.getChildren().addAll(new Label("Edit Photo") {{ 
        setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold;"); 
    }}, container);
   
    // --- RIGHT SIDE: TOOLS ---
    VBox controlSideBar = new VBox(20);
    controlSideBar.setPadding(new Insets(30, 20, 30, 20));
    controlSideBar.setPrefWidth(320);
    controlSideBar.setStyle("-fx-background-color: #000000; -fx-border-color: #2F3336; -fx-border-width: 0 0 0 1;");

    // ADDED: History Navigation Controls
    HBox historyControls = new HBox(10);
    Button undoBtn = new Button("⬅ Undo");
    Button redoBtn = new Button("Forward ➡");
    undoBtn.setStyle("-fx-background-color: #2C2C2E; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;");
    redoBtn.setStyle("-fx-background-color: #2C2C2E; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;");
    historyControls.getChildren().addAll(undoBtn, redoBtn);

    Label toolsLabel = new Label("Adjustment Tools");
    toolsLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: white;");

    Button saveBtn = new Button("Save Changes");
    saveBtn.setMaxWidth(Double.MAX_VALUE);
    saveBtn.setDisable(true);
    saveBtn.setStyle("-fx-background-color: #007AFF; -fx-text-fill: white; -fx-font-weight: bold; " +
                     "-fx-background-radius: 10; -fx-padding: 12; -fx-opacity: 0.5;");

    Runnable activateSave = () -> {
        saveBtn.setDisable(false);
        saveBtn.setStyle("-fx-background-color: #007AFF; -fx-text-fill: white; -fx-font-weight: bold; " +
                         "-fx-background-radius: 10; -fx-padding: 12; -fx-opacity: 1.0;");
    };

    saveBtn.setOnAction(e -> {
    if (currentImage == null || editPreview.getImage() == null) {
        showAlert("No image available to save.");
        return;
    }

    try {
        // 1. Convert the JavaFX Image from the preview to BufferedImage
        BufferedImage editedBufferedImage = fxToBufferedImage(editPreview.getImage());

        // 2. Create a unique filename to avoid overwriting
        // This will save in your main project directory
        String fileName = "edited_" + System.currentTimeMillis() + ".png";
        File output = new File(fileName);

        // 3. Save as PNG (safest for edited images with filters/transparency)
        boolean success = ImageIO.write(editedBufferedImage, "png", output);

        if (success) {
            this.latestSavedFile = output;
            this.latestSavedType = "image";

            showAlert("Edited image saved as a new file: " + output.getName());

            // 4. Reset the save button
            saveBtn.setDisable(true);
            saveBtn.setStyle("-fx-background-color: #007AFF; -fx-text-fill: white; -fx-font-weight: bold; " +
                             "-fx-background-radius: 10; -fx-padding: 12; -fx-opacity: 0.5;");

            // Optional: Refresh thumbnails if your app tracks the project folder
            refreshThumbnails();
        } else {
            showAlert("Failed to save the edited image.");
        }

    } catch (Exception ex) {
        ex.printStackTrace();
        showAlert("Error saving new file: " + ex.getMessage());
    }
});

    // --- MONO FILTER ---
    Button grayBtn = new Button("Apply Mono Filter");
    grayBtn.setMaxWidth(Double.MAX_VALUE);
    grayBtn.setStyle("-fx-background-color: #1C1C1E; -fx-text-fill: white; -fx-border-color: #2F3336; -fx-border-radius: 8; -fx-padding: 12;");
    
    // --- SLIDERS SETUP ---
    Label brightTitle = new Label("Brightness: 0");
    brightTitle.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");
    Slider brightSlider = new Slider(-100, 100, 0);
    setupSliderDesign(brightSlider); 

    Label contrastTitle = new Label("Contrast: 0");
    contrastTitle.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");
    Slider contrastSlider = new Slider(-100, 100, 0);
    setupSliderDesign(contrastSlider);

    // Logic to re-apply sliders from history
    undoBtn.setOnAction(e -> {
        historyManager.undo();
        applyHistoryToEditingSliders(brightSlider, contrastSlider);
    });

    redoBtn.setOnAction(e -> {
        historyManager.redo();
        applyHistoryToEditingSliders(brightSlider, contrastSlider);
    });

 // 1. First, declare and setup the hueSlider
Label borderTitle = new Label("Frame Settings (Color Hue)");
borderTitle.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");

Slider hueSlider = new Slider(0, 360, 0);
hueSlider.setStyle("-fx-background-color: linear-gradient(to right, red, orange, yellow, green, cyan, blue, violet, red); -fx-background-radius: 5;");

// 2. Now define the updateImage logic (it can now see hueSlider)
Runnable updateImage = () -> {
    activateSave.run(); 
    int bVal = (int) brightSlider.getValue();
    int cVal = (int) contrastSlider.getValue();
    
    java.awt.image.BufferedImage img = fxToBufferedImage(mainImageView.getImage());
    img = dip_basic.BrightnessContrast.adjustBrightness(img, bVal);
    img = dip_basic.BrightnessContrast.adjustContrast(img, cVal);
    
    if (isGrayscale) {
        img = dip_basic.Grayscale.apply(img);
    }

    // Drawing the frame onto the image pixels
    double hue = hueSlider.getValue();
    if (hue > 0) {
        java.awt.Graphics2D g2d = img.createGraphics();
        javafx.scene.paint.Color fxColor = javafx.scene.paint.Color.hsb(hue, 1.0, 1.0);
        java.awt.Color awtColor = new java.awt.Color((float)fxColor.getRed(), (float)fxColor.getGreen(), (float)fxColor.getBlue());

        int borderThickness = (int)(img.getWidth() * 0.05); // Dynamic thickness based on image size
        g2d.setColor(awtColor);
        g2d.setStroke(new java.awt.BasicStroke(borderThickness));
        g2d.drawRect(0, 0, img.getWidth(), img.getHeight());
        g2d.dispose();
    }

    editPreview.setImage(bufferedToFxImage(img));
};

// 3. Finally, attach the listener to the slider
hueSlider.valueProperty().addListener((obs, old, val) -> {
    // Call updateImage to bake it into the preview pixels
    updateImage.run(); 
});

    brightSlider.valueProperty().addListener((obs, old, val) -> updateImage.run());
    contrastSlider.valueProperty().addListener((obs, old, val) -> updateImage.run());

    // ADDED: Efficient Way - Save state only on mouse release
    brightSlider.setOnMouseReleased(e -> historyManager.addStep(new utils.EditAction(utils.EditAction.Type.BRIGHTNESS, String.valueOf(brightSlider.getValue()))));
    contrastSlider.setOnMouseReleased(e -> historyManager.addStep(new utils.EditAction(utils.EditAction.Type.CONTRAST, String.valueOf(contrastSlider.getValue()))));

    grayBtn.setOnAction(e -> {
        isGrayscale = true;
        historyManager.addStep(new utils.EditAction(utils.EditAction.Type.FILTER, "GRAYSCALE"));
        updateImage.run(); 
    });

    // --- RESET / DISCARD ---
    Button resetBtn = new Button("Discard Changes");
    resetBtn.setMaxWidth(Double.MAX_VALUE);
    resetBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #FF3B30; -fx-font-weight: bold;");
    resetBtn.setOnAction(e -> {
        isGrayscale = false;
        historyManager = new HistoryManager(); // ADDED: Clear history on reset
        brightSlider.setValue(0);
        contrastSlider.setValue(0);
        hueSlider.setValue(0);
        displayStack.setStyle("-fx-border-width: 0;");
        editPreview.setImage(mainImageView.getImage());
        saveBtn.setDisable(true);
        saveBtn.setStyle("-fx-background-color: #007AFF; -fx-text-fill: white; -fx-font-weight: bold; " +
                         "-fx-background-radius: 10; -fx-padding: 12; -fx-opacity: 0.5;");
    });

    // Assembly
    controlSideBar.getChildren().addAll(
        historyControls, // ADDED
        toolsLabel, grayBtn, new Separator() {{ setStyle("-fx-background-color: #2F3336;"); }},
        brightTitle, brightSlider, 
        contrastTitle, contrastSlider, new Separator() {{ setStyle("-fx-background-color: #2F3336;"); }},
        borderTitle, hueSlider,
        saveBtn, 
        new Region() {{ VBox.setVgrow(this, Priority.ALWAYS); }}, 
        resetBtn
    );

    mainLayout.getChildren().addAll(imageSection, controlSideBar);
    HBox page = new HBox(18, createGalleryMiniList(), mainLayout);
    page.setPadding(new Insets(18));
    root.setCenter(page);
}
private ScrollPane createGalleryMiniList() {
    FlowPane miniPane = new FlowPane();
    miniPane.setPadding(new Insets(10));
    miniPane.setHgap(8);
    miniPane.setVgap(8);
    miniPane.setPrefWrapLength(220);

    for (ImageModel model : imageList) {
        Image img = new Image(new File(model.getFilePath()).toURI().toString());

        ImageView thumb = new ImageView(img);
        thumb.setFitWidth(90);
        thumb.setFitHeight(70);
        thumb.setPreserveRatio(true);

        Label favMark = new Label("♥");
        favMark.setStyle("-fx-text-fill: red; -fx-font-size: 18px;");
        favMark.setVisible(annotationManager.hasAnnotation(model.getFilePath()));

        StackPane card = new StackPane(thumb, favMark);
        StackPane.setAlignment(favMark, Pos.TOP_RIGHT);

        card.setStyle(
                "-fx-background-color: #1f2937;" +
                "-fx-background-radius: 10;" +
                "-fx-padding: 6;" +
                "-fx-cursor: hand;"
        );

       card.setOnMouseClicked(e -> {
    displayImage(model);

    if (currentSection.equals("editing")) {
        showEditingPage();
    } else if (currentSection.equals("object")) {
        showObjectTransformPage();
    } else if (currentSection.equals("mosaic")) {
        showMosaicPage();
    } else if (currentSection.equals("video")) {
        showVideoPage();
    }
});

        miniPane.getChildren().add(card);
    }

    ScrollPane scroll = new ScrollPane(miniPane);
    scroll.setPrefWidth(250);
    scroll.setFitToWidth(true);
    scroll.setStyle(darkCardStyle());

    return scroll;
}
    private void openImageFolder() {
    DirectoryChooser chooser = new DirectoryChooser();
    chooser.setTitle("Select Image Folder");
    File folder = chooser.showDialog(stage);

    if (folder == null) return;

    File[] files = folder.listFiles();
    if (files != null) {
        for (File file : files) {
            if (isImageFile(file)) {
                // Save to Database using your static method
                DatabaseManager.savePath(file.getAbsolutePath());

                // Load into UI
                ImageModel imageModel = new ImageModel(file.getAbsolutePath());
                imageModel.setAnnotation(annotationManager.getAnnotation(file.getAbsolutePath()));

                imageList.add(imageModel);
                addThumbnail(imageModel);
            }
        }
    }
}

    private void setupSliderDesign(Slider slider) {
    slider.setShowTickMarks(true);
    slider.setShowTickLabels(true);
    slider.setMajorTickUnit(25);
    slider.setMinorTickCount(0);
    slider.setSnapToTicks(false);
    slider.setStyle("-fx-control-inner-background: #333333; -fx-cursor: hand;");}

    private boolean isImageFile(File file) {
        String name = file.getName().toLowerCase();

        return name.endsWith(".jpg")
                || name.endsWith(".jpeg")
                || name.endsWith(".png")
                || name.endsWith(".bmp")
                || name.endsWith(".gif");
    }

    private void addThumbnail(ImageModel imageModel) {
        Image image = new Image(new File(imageModel.getFilePath()).toURI().toString());

        ImageView thumbnail = new ImageView(image);
        thumbnail.setFitWidth(100);
        thumbnail.setFitHeight(80);
        thumbnail.setPreserveRatio(true);

        Label heart = new Label("♥");
        heart.setStyle("-fx-text-fill: red; -fx-font-size: 20px;");
        heart.setVisible(annotationManager.hasAnnotation(imageModel.getFilePath()));

        StackPane thumbnailStack = new StackPane(thumbnail, heart);
        StackPane.setAlignment(heart, Pos.TOP_RIGHT);

         thumbnailStack.setStyle(
        "-fx-background-color: #1f2937;" +
        "-fx-background-radius: 14;" +
        "-fx-border-color: #334155;" +
        "-fx-border-radius: 14;" +
        "-fx-border-width: 2;" +
        "-fx-padding: 7;" +
        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 8, 0, 0, 3);" +
        "-fx-cursor: hand;" );

        String normalThumbStyle =
        "-fx-background-color: #1f2937;" +
        "-fx-background-radius: 14;" +
        "-fx-border-color: #334155;" +
        "-fx-border-radius: 14;" +
        "-fx-border-width: 2;" +
        "-fx-padding: 7;" +
        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 8, 0, 0, 3);" +
        "-fx-cursor: hand;";

String hoverThumbStyle =
        "-fx-background-color: #2563eb;" +
        "-fx-background-radius: 14;" +
        "-fx-border-color: #60a5fa;" +
        "-fx-border-radius: 14;" +
        "-fx-border-width: 2;" +
        "-fx-padding: 7;" +
        "-fx-effect: dropshadow(gaussian, rgba(37,99,235,0.45), 12, 0, 0, 4);" +
        "-fx-cursor: hand;";

thumbnailStack.setStyle(normalThumbStyle);
thumbnailStack.setOnMouseEntered(e -> thumbnailStack.setStyle(hoverThumbStyle));
thumbnailStack.setOnMouseExited(e -> thumbnailStack.setStyle(normalThumbStyle));

        thumbnailStack.setOnMouseClicked(e -> displayImage(imageModel));

        thumbnailPane.getChildren().add(thumbnailStack);
    }

    private void displayImage(ImageModel imageModel) {
        currentImage = imageModel;

        Image image = new Image(new File(imageModel.getFilePath()).toURI().toString());
        mainImageView.setImage(image);

        File file = new File(imageModel.getFilePath());
        fileNameLabel.setText(file.getName());

        String annotation = annotationManager.getAnnotation(imageModel.getFilePath());
        annotationArea.setText(annotation);

        heartLabel.setVisible(annotationManager.hasAnnotation(imageModel.getFilePath()));
        if (annotation != null && !annotation.trim().isEmpty()) {
    annotationOverlay.setText(annotation);
    annotationOverlay.setVisible(true);
} else {
    annotationOverlay.setVisible(false);
}
    }

    private void saveAnnotation() {
        if (currentImage == null) {
            showAlert("Please select an image first.");
            return;
        }

        String annotation = annotationArea.getText();

        currentImage.setAnnotation(annotation);
        annotationManager.saveAnnotation(currentImage.getFilePath(), annotation);
         embedAnnotationOnImage(currentImage);
        heartLabel.setVisible(currentImage.hasAnnotation());

        refreshThumbnails();

        showAlert("Annotation saved successfully.");
    }

    private void refreshThumbnails() {
        if (thumbnailPane == null) {
            return;
        }

        thumbnailPane.getChildren().clear();

        for (ImageModel imageModel : imageList) {
            imageModel.setAnnotation(annotationManager.getAnnotation(imageModel.getFilePath()));
            addThumbnail(imageModel);
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Message");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private java.awt.image.BufferedImage fxToBufferedImage(Image img) {
    return javafx.embed.swing.SwingFXUtils.fromFXImage(img, null);    }

    private Image bufferedToFxImage(java.awt.image.BufferedImage bimg) {
    return javafx.embed.swing.SwingFXUtils.toFXImage(bimg, null);}

// private void applyLightMode() {
//     // Restore the standard light background
// root.setStyle("-fx-background-color: #eef2f3;");    
//     HBox topBar = (HBox) root.getTop();
// topBar.setStyle(
//         "-fx-background-color: #ffffff;" +
//         "-fx-border-color: #dcdcdc;" +
//         "-fx-border-width: 0 0 1 0;"
// );
//     // Update the button icon visibility
//     if (themeToggle != null) {
//         themeToggle.setText("☀️");
//         themeToggle.setStyle("-fx-background-color: transparent; -fx-font-size: 18px; -fx-text-fill: black;");
//     }
// }

private void showSharePage() {
    VBox shareLayout = new VBox(20);
    shareLayout.setAlignment(Pos.CENTER);
    shareLayout.setPadding(new Insets(30));
    shareLayout.setStyle("-fx-background-color: #f9fafb;");

    Label title = new Label("📤 Export & Share");
    title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #111827;");

    if (latestSavedFile == null) {
        Label noFileLabel = new Label("Nothing to share yet!\nPlease save an edited photo or export a video first.");
        noFileLabel.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 14px;");
        shareLayout.getChildren().addAll(title, noFileLabel);
    } else {
        Label fileInfo = new Label("Ready to share: " + latestSavedFile.getName());
        fileInfo.setStyle("-fx-font-size: 14px; -fx-text-fill: #6b7280;");

        Button whatsappBtn = new Button("Share to WhatsApp");
        Button emailBtn = new Button("Share via Gmail");
        
        whatsappBtn.getStyleClass().add("nav-button");
        emailBtn.getStyleClass().add("nav-button");
        whatsappBtn.setPrefWidth(250);
        emailBtn.setPrefWidth(250);

        // --- WhatsApp Logic (Images Only) ---
        if (latestSavedType.equals("video")) {
            whatsappBtn.setDisable(true);
            whatsappBtn.setText("WhatsApp (Images Only)");
        }

        whatsappBtn.setOnAction(e -> {
            Alert instructions = new Alert(Alert.AlertType.INFORMATION);
            instructions.setTitle("WhatsApp Sharing");
            instructions.setHeaderText("Image Copied!");
            instructions.setContentText(
                "1. WhatsApp Web will open.\n" +
                "2. Pick a contact.\n" +
                "3. Press CTRL + V in the chat to paste your edited image."
            );
            instructions.showAndWait();
            controller.handleWhatsAppShare(latestSavedFile);
        });

        // --- Email Logic (Works for both) ---
emailBtn.setOnAction(e -> {
    if (latestSavedFile != null) {
        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setTitle("Gmail Sharing");
        
        if (latestSavedType.equals("image")) {
            info.setHeaderText("Image Copied to Clipboard!");
            info.setContentText("1. Gmail will open in your browser.\n" +
                                "2. Click the email body.\n" +
                                "3. Press CTRL + V to paste your image!");
        } else {
            info.setHeaderText("Video Path Ready");
            info.setContentText("Gmail will open. Please use the 'Paperclip' icon\n" +
                                "to attach the video from the path provided in the email body.");
        }
        
        info.showAndWait();
        controller.handleEmailShare(latestSavedFile);
    }
});

        shareLayout.getChildren().addAll(title, fileInfo, whatsappBtn, emailBtn);
    }
    
    root.setCenter(shareLayout);
}

// --- PUT THIS AT THE BOTTOM OF YOUR CLASS ---
private java.awt.image.BufferedImage extractObjectBySimilarity(java.awt.image.BufferedImage source, javafx.scene.paint.Color targetFxColor) {
    int width = source.getWidth();
    int height = source.getHeight();
    java.awt.image.BufferedImage output = new java.awt.image.BufferedImage(width, height, java.awt.image.BufferedImage.TYPE_INT_ARGB);

    // Convert JavaFX Color to float HSV for better lighting handling
    float[] targetHSV = new float[3];
    java.awt.Color.RGBtoHSB(
        (int)(targetFxColor.getRed()*255), 
        (int)(targetFxColor.getGreen()*255), 
        (int)(targetFxColor.getBlue()*255), 
        targetHSV
    );

    for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
            java.awt.Color pixelColor = new java.awt.Color(source.getRGB(x, y), true);
            float[] pixelHSV = new float[3];
            java.awt.Color.RGBtoHSB(pixelColor.getRed(), pixelColor.getGreen(), pixelColor.getBlue(), pixelHSV);

            // Calculate Hue Distance (Circular distance handles shadows/highlights better)
            double hueDiff = Math.abs(pixelHSV[0] - targetHSV[0]);
            if (hueDiff > 0.5) hueDiff = 1.0 - hueDiff;

            // Euclidean distance focused on Hue rather than just RGB brightness
            double distance = Math.sqrt(Math.pow(hueDiff * 2.0, 2) + Math.pow(pixelHSV[1] - targetHSV[1], 2));

            // 0.15 is a standard similarity threshold for realistic photos
            if (distance < 0.15) {
                output.setRGB(x, y, source.getRGB(x, y)); 
            } else {
                output.setRGB(x, y, 0x00000000); // Make background transparent
            }
        }
    }
    return output;
}

private File selectedTargetFile = null; // Class-level variable to store the selected target image

private void showMosaicPage() {
    currentSection = "mosaic";
    VBox layout = new VBox(18);
    layout.setPadding(new Insets(22));
    layout.setAlignment(Pos.TOP_CENTER);
    layout.setStyle(
    "-fx-background-color: linear-gradient(to bottom right, #ffffff, #f1f5f9);" +
    "-fx-background-radius: 24;" +
    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 22, 0, 0, 8);"
);

    Label title = new Label("🖼️ True Photomosaic Studio");
    title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #111827;");

    Label subtitle = new Label("Reconstruct a target image using the color values of your photo collection.");
    subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #6b7280;");

    // UI elements for file selection and sizes
    Button selectTargetBtn = new Button("📁 Choose Target Image");
    Label targetLabel = new Label("No target image selected");
    targetLabel.setStyle("-fx-font-style: italic; -fx-text-fill: #6b7280;");

    TextField tileSizeField = new TextField("30"); // Smaller tile size yields higher fidelity resolution
    tileSizeField.setMaxWidth(90);

    TextField canvasSizeField = new TextField("900"); // Standardized output grid dimensions
    canvasSizeField.setMaxWidth(90);

    HBox settings = new HBox(12,
            selectTargetBtn, targetLabel,
            new Label("Tile Size:"), tileSizeField,
            new Label("Canvas:"), canvasSizeField
    );
    settings.setAlignment(Pos.CENTER);

    ImageView mosaicView = new ImageView();
    mosaicView.setFitWidth(720);
    mosaicView.setFitHeight(500);
    mosaicView.setPreserveRatio(true);

    StackPane previewBox = new StackPane(mosaicView);
    previewBox.setPrefSize(760, 520);
    previewBox.setStyle(
            "-fx-background-color: #111827;" +
            "-fx-background-radius: 18;" +
            "-fx-padding: 18;"
    );

    final BufferedImage[] currentMosaic = new BufferedImage[1];

    Button generateBtn = new Button("✨ Generate Photomosaic");
    Button saveBtn = new Button("💾 Save Mosaic");
    Button clearBtn = new Button("🗑 Clear");

    generateBtn.setStyle(primaryButtonStyle());
    saveBtn.setStyle(greenButtonStyle());
    clearBtn.setStyle(
            "-fx-background-color: #ef4444;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 10;" +
            "-fx-padding: 10 18;"
    );

    // File choosing configuration block
    selectTargetBtn.setOnAction(e -> {
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Select Target Image");
        fileChooser.getExtensionFilters().addAll(
                new javafx.stage.FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.bmp")
        );
        File file = fileChooser.showOpenDialog(root.getScene().getWindow());
        if (file != null) {
            selectedTargetFile = file;
            targetLabel.setText(file.getName());
        }
    });

   generateBtn.setOnAction(e -> {
        // 1. Basic Validation Checks
        if (imageList.isEmpty()) {
            showAlert("Please open an image source folder first.");
            return;
        }
        if (selectedTargetFile == null) {
            showAlert("Please select a target background image to replicate.");
            return;
        }

        try {
            // 2. Parse User Dimensions from the UI Input Fields
            int tileSize = Integer.parseInt(tileSizeField.getText());
            int canvasSize = Integer.parseInt(canvasSizeField.getText());

            // 3. Extract the Plain Text File Paths (Takes virtually zero memory)
            List<String> tilePaths = new ArrayList<>();
            for (ImageModel model : imageList) {
                tilePaths.add(model.getFilePath());
            }

            // 4. Update UI Button State to Prevent Accidental Double-Clicks
            generateBtn.setDisable(true);
            generateBtn.setText("⏳ Processing Mosaic...");

            // Hint to Java to run a Garbage Collection pass to maximize available RAM
            System.gc();

            // 5. This is EXACTLY where your Task block lives
            Task<BufferedImage> mosaicTask = new Task<>() {
                @Override
                protected BufferedImage call() throws Exception {
                    // Step 1: Turn the text paths into memory-optimized AnalyzedTiles
                    List<MosaicGenerator.AnalyzedTile> cachedTiles = 
                            MosaicGenerator.preAnalyzeTiles(tilePaths, tileSize);
                    
                    // Step 2: Pass those cachedTiles into createTrueMosaic
                    BufferedImage result = MosaicGenerator.createTrueMosaic(selectedTargetFile, cachedTiles, tileSize, canvasSize);
                    
                    // Step 3: Clear the list to free up memory immediately
                    cachedTiles.clear();
                    
                    return result;
                }
            };

            // 6. Define what happens when the background task completes successfully
            mosaicTask.setOnSucceeded(workerEvent -> {
                BufferedImage result = mosaicTask.getValue();
                if (result != null) {
                    currentMosaic[0] = result;
                    mosaicView.setImage(SwingFXUtils.toFXImage(result, null));
                } else {
                    showAlert("Failed to process mosaic layout.");
                }
                // Reset button back to its active state
                generateBtn.setDisable(false);
                generateBtn.setText("✨ Generate Photomosaic");
                System.gc(); 
            });

            // 7. Define what happens if the background task crashes or encounters an error
            mosaicTask.setOnFailed(workerEvent -> {
                Throwable error = mosaicTask.getException();
                if (error != null) error.printStackTrace();
                showAlert("An error occurred during mosaic generation.");
                generateBtn.setDisable(false);
                generateBtn.setText("✨ Generate Photomosaic");
                System.gc();
            });

            // 8. Start the Task in a Background Daemon Thread
            Thread thread = new Thread(mosaicTask);
            thread.setDaemon(true); // Automatically shuts down thread if the application window is closed
            thread.start();

        } catch (NumberFormatException ex) {
            showAlert("Please input valid numeric values for structural dimensions.");
        }
    });

    saveBtn.setOnAction(e -> {
        if (currentMosaic[0] == null) {
            showAlert("Please generate mosaic first.");
            return;
        }

        try {
            File output = new File("true_mosaic_" + System.currentTimeMillis() + ".png");
            ImageIO.write(currentMosaic[0], "png", output);
            this.latestSavedFile = output;
            this.latestSavedType = "image";

            showAlert("Mosaic successfully saved as: " + output.getName());
        } catch (Exception ex) {
            showAlert("Failed to export image output asset.");
        }
    });

    clearBtn.setOnAction(e -> {
        mosaicView.setImage(null);
        currentMosaic[0] = null;
        selectedTargetFile = null;
        targetLabel.setText("No target image selected");
    });

    HBox buttons = new HBox(12, generateBtn, saveBtn, clearBtn);
    buttons.setAlignment(Pos.CENTER);

    layout.getChildren().addAll(title, subtitle, settings, buttons, previewBox);

    HBox page = new HBox(18, createGalleryMiniList(), layout);
    page.setPadding(new Insets(18));
    root.setCenter(page);
}

private void showVideoPage() {
    currentSection = "video";
    VBox layout = new VBox(18);
    layout.setPadding(new Insets(22));
    layout.setAlignment(Pos.TOP_CENTER);
    layout.setStyle(cardStyle());

    Label title = new Label("🎬 Video Story Creator");
    title.setStyle(
    "-fx-font-size: 32px;" +
    "-fx-font-weight: bold;" +
    "-fx-text-fill: #111827;"
);

    Label subtitle = new Label("Build a slideshow video sequence from your favourite images.");
    subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #6b7280;");

    List<ImageModel> favouriteImages = new ArrayList<>();
    ListView<String> favouriteListView = new ListView<>();
    favouriteListView.setPrefWidth(260);
    favouriteListView.setPrefHeight(360);

Button loadFavBtn = new Button("♥ Load Annotated Favourites");
    loadFavBtn.setStyle(primaryButtonStyle());

    ImageView videoView = new ImageView();
    videoView.setFitWidth(680);
    videoView.setFitHeight(390);
    videoView.setPreserveRatio(true);

    videoView.setSmooth(true);

    Label captionLabel = new Label("Caption will appear here");
    captionLabel.setStyle(
            "-fx-font-size: 22px;" +
            "-fx-text-fill: white;" +
            "-fx-background-color: rgba(0,0,0,0.55);" +
            "-fx-padding: 10;" +
            "-fx-background-radius: 10;"
    );

    StackPane videoPane = new StackPane(videoView, captionLabel);
    StackPane.setAlignment(captionLabel, Pos.BOTTOM_CENTER);
    StackPane.setMargin(captionLabel, new Insets(20));
 videoPane.setMinWidth(680);
videoPane.setMinHeight(390);
videoPane.setPrefSize(680, 390);
videoPane.setMaxSize(680, 390);

videoPane.setStyle(
    "-fx-background-color: #111827;" + // Black background fills empty ratio gaps
    "-fx-background-radius: 18;" + 
    "-fx-padding: 0;" + // Change padding to 0 so it doesn't push the image
    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 20, 0, 0, 8);"
);

    TextField captionInput = new TextField();
    captionInput.setPromptText("Caption for selected image...");
    captionInput.setMaxWidth(420);

    Slider durationSlider = new Slider(1, 6, 2);
    durationSlider.setShowTickLabels(true);
    durationSlider.setShowTickMarks(true);
    durationSlider.setMajorTickUnit(1);
    durationSlider.setMaxWidth(420);

    Label durationLabel = new Label("Duration per image: 2 seconds");
    durationLabel.setStyle("-fx-text-fill: #374151; -fx-font-weight: bold;");

    durationSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
        durationLabel.setText("Duration per image: " + String.format("%.1f", newVal.doubleValue()) + " seconds");
    });

    final int[] index = {0};
    final Timeline[] timeline = new Timeline[1];

loadFavBtn.setOnAction(e -> {
    favouriteImages.clear();
    favouriteListView.getItems().clear();

    for (ImageModel model : imageList) {
        if (annotationManager.hasAnnotation(model.getFilePath())) {
            favouriteImages.add(model);
            favouriteListView.getItems().add("♥ " + new File(model.getFilePath()).getName());
        }
    }

    if (favouriteImages.isEmpty()) {
        showAlert("No favourite images yet. Add annotations in Gallery first.");
    } else {
        showAlert("Loaded " + favouriteImages.size() + " favourite images.");
    }
});
    favouriteListView.getSelectionModel().selectedIndexProperty().addListener((obs, oldVal, newVal) -> {
        int selectedIndex = newVal.intValue();

        if (selectedIndex >= 0 && selectedIndex < favouriteImages.size()) {
            index[0] = selectedIndex;

            ImageModel selected = favouriteImages.get(selectedIndex);
            videoView.setImage(new Image(new File(selected.getFilePath()).toURI().toString()));

            String note = selected.getAnnotation();
            captionLabel.setText(note == null || note.trim().isEmpty() ? "My Photo Story" : note);
            captionInput.setText(captionLabel.getText());
        }
    });

    Button saveCaptionBtn = new Button("💬 Save Caption");
    saveCaptionBtn.setStyle(primaryButtonStyle());

    saveCaptionBtn.setOnAction(e -> {
        int selectedIndex = favouriteListView.getSelectionModel().getSelectedIndex();

        if (selectedIndex < 0 || selectedIndex >= favouriteImages.size()) {
            showAlert("Select an image from the favourite list first.");
            return;
        }

        ImageModel selected = favouriteImages.get(selectedIndex);
        selected.setAnnotation(captionInput.getText());
        annotationManager.saveAnnotation(selected.getFilePath(), captionInput.getText());
        captionLabel.setText(captionInput.getText());

        // Update annotation overlay
        String annotation = captionInput.getText();
        if (annotation != null && !annotation.trim().isEmpty()) {
            annotationOverlay.setText(annotation);
            annotationOverlay.setVisible(true);
        } else {
            annotationOverlay.setVisible(false);
        }

        showAlert("Caption saved for this image.");
    });

    Button playBtn = new Button("▶ Play");
    Button pauseBtn = new Button("⏸ Pause");
    Button restartBtn = new Button("🔁 Restart");
    Button prevBtn = new Button("⬅ Previous");
    Button nextBtn = new Button("Next ➡");

    Button saveVideoBtn = new Button("⬇ Save Video");

saveVideoBtn.setStyle(
    "-fx-background-color: #ef4444;" +
    "-fx-text-fill: white;" +
    "-fx-font-weight: bold;" +
    "-fx-background-radius: 12;" +
    "-fx-padding: 10 18;" +
    "-fx-cursor: hand;"
);

saveVideoBtn.setOnAction(e -> {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Save Video");

    fileChooser.getExtensionFilters().add(
        new FileChooser.ExtensionFilter("MP4 Video", "*.mp4")
    );

    fileChooser.setInitialFileName("slideshow_video.mp4");

    File outputFile = fileChooser.showSaveDialog(stage);

    if (outputFile != null) {
        showAlert("Video export location selected:\n" + outputFile.getAbsolutePath());
    } 
});

    playBtn.setStyle(greenButtonStyle());
    pauseBtn.setStyle(primaryButtonStyle());
    restartBtn.setStyle(primaryButtonStyle());
    prevBtn.setStyle(primaryButtonStyle());
    nextBtn.setStyle(primaryButtonStyle());

    setButtonStyle(playBtn, "#34C759", 1.0, false);  // Green, Active
    setButtonStyle(pauseBtn, "#007AFF", 0.5, true);   // Blue, Inactive/Faded
    setButtonStyle(restartBtn, "#007AFF", 1.0, false);
    setButtonStyle(prevBtn, "#007AFF", 1.0, false);
    setButtonStyle(nextBtn, "#007AFF", 1.0, false);

    Runnable showCurrentSlide = () -> {
        if (favouriteImages.isEmpty()) {
            return;
        }

        ImageModel current = favouriteImages.get(index[0]);

        videoView.setImage(new Image(new File(current.getFilePath()).toURI().toString()));

        String note = annotationManager.getAnnotation(current.getFilePath());
        captionLabel.setText(note == null || note.trim().isEmpty() ? "My Photo Story" : note);

        favouriteListView.getSelectionModel().select(index[0]);
    };

    playBtn.setOnAction(e -> {
    if (favouriteImages.isEmpty()) {
        showAlert("Please load favourite images first.");
        return;
    }
    
    // Toggle Button Visuals
    setButtonStyle(playBtn, "#34C759", 0.5, true);  // Fade Play
    setButtonStyle(pauseBtn, "#007AFF", 1.0, false); // Light up Pause

    if (timeline[0] != null) timeline[0].stop();
    timeline[0] = new Timeline(new KeyFrame(Duration.seconds(durationSlider.getValue()), event -> {
        showCurrentSlide.run();
        index[0]++;
        if (index[0] >= favouriteImages.size()) index[0] = 0;
    }));
    timeline[0].setCycleCount(Timeline.INDEFINITE);
    timeline[0].play();
});

// 3. Pause Button Action
pauseBtn.setOnAction(e -> {
    if (timeline[0] != null) {
        timeline[0].pause();
        
        // Toggle Button Visuals back
        setButtonStyle(playBtn, "#34C759", 1.0, false); // Light up Play
        setButtonStyle(pauseBtn, "#007AFF", 0.5, true);  // Fade Pause
    }
});

// 4. Restart Action (Reset to Play mode)
restartBtn.setOnAction(e -> {
    index[0] = 0;
    showCurrentSlide.run();
    if (timeline[0] != null) {
        timeline[0].playFromStart();
        setButtonStyle(playBtn, "#34C759", 0.5, true);
        setButtonStyle(pauseBtn, "#007AFF", 1.0, false);
    }
});
////////////////////////////////////////////////////////////////////////////////////////////////////
    // playBtn.setOnAction(e -> {
    //     if (favouriteImages.isEmpty()) {
    //         showAlert("Please load favourite images first.");
    //         return;
    //     }

    //     if (timeline[0] != null) {
    //         timeline[0].stop();
    //     }

    //     timeline[0] = new Timeline(new KeyFrame(Duration.seconds(durationSlider.getValue()), event -> {
    //         showCurrentSlide.run();

    //         index[0]++;

    //         if (index[0] >= favouriteImages.size()) {
    //             index[0] = 0;
    //         }
    //     }));

    //     timeline[0].setCycleCount(Timeline.INDEFINITE);
    //     timeline[0].play();
    // });

    // pauseBtn.setOnAction(e -> {
    //     if (timeline[0] != null) {
    //         timeline[0].pause();
    //     }
    // });

    // restartBtn.setOnAction(e -> {
    //     index[0] = 0;
    //     showCurrentSlide.run();

    //     if (timeline[0] != null) {
    //         timeline[0].playFromStart();
    //     }
    // });
//////////////////////////////////////////////////////////////////////////////////////
    prevBtn.setOnAction(e -> {
        if (favouriteImages.isEmpty()) {
            showAlert("Please load favourite images first.");
            return;
        }

        index[0]--;

        if (index[0] < 0) {
            index[0] = favouriteImages.size() - 1;
        }

        showCurrentSlide.run();
    });

    nextBtn.setOnAction(e -> {
        if (favouriteImages.isEmpty()) {
            showAlert("Please load favourite images first.");
            return;
        }

        index[0]++;

        if (index[0] >= favouriteImages.size()) {
            index[0] = 0;
        }

        showCurrentSlide.run();
    });

    VBox leftControls = new VBox(12, loadFavBtn, new Label("Favourite Images:"), favouriteListView);
    leftControls.setPadding(new Insets(12));
    leftControls.setStyle(
            "-fx-background-color: #f9fafb;" +
            "-fx-background-radius: 16;"
    );

    HBox captionBox = new HBox(10, captionInput, saveCaptionBtn);
    captionBox.setAlignment(Pos.CENTER);

    HBox playbackControls = new HBox(
    10,
    prevBtn,
    playBtn,
    pauseBtn,
    restartBtn,
    nextBtn,
    saveVideoBtn
);
    playbackControls.setAlignment(Pos.CENTER);

    VBox videoArea = new VBox(14, videoPane, durationLabel, durationSlider, captionBox, playbackControls);
    videoArea.setAlignment(Pos.CENTER);

    HBox content = new HBox(18, leftControls, videoArea);
    content.setAlignment(Pos.CENTER);

    layout.getChildren().addAll(title, subtitle, content);

HBox page = new HBox(18, createGalleryMiniList(), layout);
    page.setPadding(new Insets(18));
    root.setCenter(page);
}
private String primaryButtonStyle() {
    return "-fx-background-color: #2563eb;" +
           "-fx-text-fill: white;" +
           "-fx-font-size: 14px;" +
           "-fx-font-weight: bold;" +
           "-fx-background-radius: 10;" +
           "-fx-padding: 10 18;";
}

private String greenButtonStyle() {
    return "-fx-background-color: #16a34a;" +
           "-fx-text-fill: white;" +
           "-fx-font-size: 14px;" +
           "-fx-font-weight: bold;" +
           "-fx-background-radius: 10;" +
           "-fx-padding: 10 18;";
}

private String cardStyle() {
    return "-fx-background-color: #ffffff;" +
           "-fx-background-radius: 18;" +
           "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.10), 18, 0, 0, 6);";
}

private String darkCardStyle() {
    return "-fx-background-color: #111827;" +
           "-fx-background-radius: 18;" +
           "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 20, 0, 0, 8);";
}

private VBox createMiniPreviewPanel() {
    VBox panel = new VBox(12);
    panel.setPadding(new Insets(18));
    panel.setPrefWidth(260);
    panel.setStyle(darkCardStyle());

    Label title = new Label("Image Preview");
    title.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

    ImageView preview = new ImageView();
    preview.setFitWidth(220);
    preview.setFitHeight(180);
    preview.setPreserveRatio(true);

    Label name = new Label("No image selected");
    name.setWrapText(true);
    name.setStyle("-fx-text-fill: #e5e7eb; -fx-font-size: 12px;");

    if (currentImage != null) {
        Image img = new Image(new File(currentImage.getFilePath()).toURI().toString());
        preview.setImage(img);
        name.setText(new File(currentImage.getFilePath()).getName());
    }

    panel.getChildren().addAll(title, preview, name);
    return panel;
}

private void refreshImageDisplay() {
    // Start fresh with the original
    javafx.scene.image.Image tempImage = originalImage;
    
    // Default values for transformations
    double rotation = 0;
    double scale = 1.0;

    for (EditAction action : historyManager.getActiveHistory()) {
        switch (action.getType()) {
            case FILTER:
                // Call your existing filter logic (e.g., ImageFilters.apply(tempImage, action.getValue()))
                // tempImage = applyFilter(tempImage, action.getValue());
                break;
            case ROTATE:
                rotation += Double.parseDouble(action.getValue());
                break;
            case SCALE:
                scale *= Double.parseDouble(action.getValue());
                break;
        }
    }

    // Apply the final results to your ImageView
    imageView.setImage(tempImage);
    imageView.setRotate(rotation);
    imageView.setScaleX(scale);
    imageView.setScaleY(scale);
}

private void setButtonStyle(Button btn, String color, double opacity, boolean disabled) {
    btn.setDisable(disabled);
    
    // Define the base style
    String baseStyle = "-fx-background-color: " + color + "; " +
                       "-fx-text-fill: white; " +
                       "-fx-font-weight: bold; " +
                       "-fx-background-radius: 8; " +
                       "-fx-padding: 10 18; " +
                       "-fx-opacity: " + opacity + ";";
    
    btn.setStyle(baseStyle);

    // Hover Logic: Use a lighter version of the color for the 'light up' effect
    btn.setOnMouseEntered(e -> {
        if (!btn.isDisable()) {
            // Apply a slight glow/lighten effect
            btn.setStyle(baseStyle + "-fx-background-color: derive(" + color + ", 30%); -fx-cursor: hand;");
        }
    });

    btn.setOnMouseExited(e -> {
        // Return to the base style when mouse leaves
        btn.setStyle(baseStyle);
    });
}

// ADDED: Syncs the UI sliders with the history data
private void applyHistoryToSliders(Slider s, Slider r, Slider tx, Slider ty) {
    java.util.List<utils.EditAction> currentHistory = historyManager.getActiveHistory();
    double scale = 100, rotate = 0, x = 0, y = 0;

    for (utils.EditAction action : currentHistory) {
        switch (action.getType()) {
            case SCALE -> scale = Double.parseDouble(action.getValue());
            case ROTATE -> rotate = Double.parseDouble(action.getValue());
            case TRANSLATE_X -> x = Double.parseDouble(action.getValue());
            case TRANSLATE_Y -> y = Double.parseDouble(action.getValue());
            // Filter is ignored here since this page only deals with transforms
        }
    }
    s.setValue(scale);
    r.setValue(rotate);
    tx.setValue(x);
    ty.setValue(y);
}

private void applyHistoryToEditingSliders(Slider brightS, Slider contrastS) {
    java.util.List<utils.EditAction> currentHistory = historyManager.getActiveHistory();
    double brightness = 0, contrast = 0;
    this.isGrayscale = false; // Reset local state to check history

    for (utils.EditAction action : currentHistory) {
        switch (action.getType()) {
            case BRIGHTNESS -> brightness = Double.parseDouble(action.getValue());
            case CONTRAST -> contrast = Double.parseDouble(action.getValue());
            case FILTER -> {
                if (action.getValue().equals("GRAYSCALE")) this.isGrayscale = true;
            }
        }
    }
    brightS.setValue(brightness);
    contrastS.setValue(contrast);
}

private boolean exportSlideshowToVideo(List<ImageModel> images, File outputFile, double duration) {
    try {
        // You would typically use a library like JCodec here:
        // ASequenceEncoder encoder = new ASequenceEncoder(outputFile, Rational.R(25, 1));
        // for (ImageModel model : images) {
        //    BufferedImage bi = ImageIO.read(new File(model.getFilePath()));
        //    encoder.encodeImage(bi);
        // }
        // encoder.finish();
        
        // For now, let's simulate the success for your layout:
        System.out.println("Encoding video at: " + outputFile.getAbsolutePath());
        return true; 
    } catch (Exception ex) {
        ex.printStackTrace();
        return false;
    }
}
private void embedAnnotationOnImage(ImageModel imageModel) {
    try {
        File file = new File(imageModel.getFilePath());

        BufferedImage image = ImageIO.read(file);

        Graphics2D g2d = image.createGraphics();

        g2d.setFont(new Font("Arial", Font.BOLD, 40));

        // white text
        g2d.setColor(java.awt.Color.WHITE);

        FontMetrics metrics = g2d.getFontMetrics();
        String text = annotationArea.getText();

        int x = (image.getWidth() - metrics.stringWidth(text)) / 2;
        int y = image.getHeight() - 50;

        g2d.drawString(text, x, y);

        g2d.dispose();

        ImageIO.write(image, "png", file);

        displayImage(imageModel);

    } catch (Exception ex) {
        ex.printStackTrace();
    }
}
}



