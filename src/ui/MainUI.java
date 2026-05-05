package ui;

//import javafx.embed.swing.SwingFXUtils;
import java.awt.image.BufferedImage;
// Ensure you also have this for the conversion logic
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
//import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import repository.AnnotationManager;
import repository.DatabaseManager;
import repository.ImageModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import integration.AppController;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javafx.embed.swing.SwingFXUtils;
import multimedia.MosaicGenerator;

public class MainUI {

    private BorderPane root;
    private FlowPane thumbnailPane;
    private ImageView mainImageView;
    private TextArea annotationArea;
    private Label heartLabel;
    private Button themeToggle; // <--- ADD THIS LINE HERE
    private Label fileNameLabel;
    private AppController controller = new AppController();

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
    Button openFolderButton = new Button("Open Folder");
    openFolderButton.setStyle(primaryButtonStyle());
    openFolderButton.setOnAction(e -> openImageFolder());

    // 2. Setup the Title Label
    Label title = new Label("Photo Repository System");
   title.setStyle(
    "-fx-font-size: 24px;" +
    "-fx-font-weight: bold;" +
    "-fx-text-fill: #2e7d32;"
);


  // REMOVE "Button" from the start. Just use the variable name.
    // Inside createTopBar()
themeToggle = new Button("☀️"); // Use the class variable
themeToggle.setStyle("-fx-background-color: transparent; -fx-font-size: 18px; -fx-cursor: hand;");

themeToggle.setOnAction(e -> {
    if (themeToggle.getText().equals("☀️")) {
        applyDarkMode();
        themeToggle.setText("🌙");
        themeToggle.setStyle("-fx-background-color: transparent; -fx-font-size: 18px; -fx-text-fill: white;");
    } else {
        applyLightMode();
        themeToggle.setText("☀️");
        themeToggle.setStyle("-fx-background-color: transparent; -fx-font-size: 18px; -fx-text-fill: black;");
    }
});

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    HBox topBar = new HBox(15, title, openFolderButton, spacer, themeToggle);

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
        "-fx-background-color: transparent;"
);

        Label galleryTitle = new Label("📂 Image Gallery");
      galleryTitle.setStyle(
        "-fx-font-size: 20px;" +
        "-fx-font-weight: bold;" +
        "-fx-padding: 0 0 14 0;" +
        "-fx-text-fill: #111827;"
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

        ScrollPane scrollPane = new ScrollPane(thumbnailPane);
        scrollPane.setPrefWidth(250);
        scrollPane.setFitToWidth(true);
      scrollPane.setStyle(cardStyle());
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

        StackPane imageStack = new StackPane(mainImageView, heartLabel);
        StackPane.setAlignment(heartLabel, Pos.TOP_RIGHT);
        StackPane.setMargin(heartLabel, new Insets(20));

        VBox centerBox = new VBox(15, fileNameLabel, imageStack);
        centerBox.setPadding(new Insets(20));
        centerBox.setAlignment(Pos.TOP_CENTER);

        StackPane previewPane = new StackPane(centerBox);
previewPane.setStyle(darkCardStyle());
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
      

VBox rightBox = new VBox(10, annotationLabel, annotationArea, saveButton);       
      rightBox.setPadding(new Insets(15));
      rightBox.setPrefWidth(280);
      rightBox.setStyle(cardStyle());

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

  private void showObjectTransformPage() {
    currentSection = "object";
    if (currentImage == null) {
        showAlert("Please select an image first!");
        return;
    }

    HBox mainLayout = new HBox(0);
    mainLayout.setStyle("-fx-background-color: #000000;"); 
    VBox.setVgrow(mainLayout, Priority.ALWAYS);

    // --- LEFT SIDE: PREVIEW AREA ---
    VBox imageSection = new VBox(20);
    imageSection.setPadding(new Insets(20));
    imageSection.setAlignment(Pos.CENTER);
    HBox.setHgrow(imageSection, Priority.ALWAYS);

    Image sourceImg = mainImageView.getImage();
    ImageView preview = new ImageView(sourceImg);
    preview.setPreserveRatio(true);
    preview.setSmooth(true);
    
    double imgW = sourceImg.getWidth();
    double imgH = sourceImg.getHeight();
    double maxViewW = 900;
    double maxViewH = 650;
    double ratio = Math.min(maxViewW / imgW, maxViewH / imgH);
    
    double canvasWidth = imgW * ratio;
    double canvasHeight = imgH * ratio;

    Pane clipWindow = new Pane(preview); 
    clipWindow.setPrefSize(canvasWidth, canvasHeight);
    clipWindow.setMaxSize(canvasWidth, canvasHeight);
    clipWindow.setStyle("-fx-background-color: #111111; -fx-border-color: #2F3336; -fx-border-width: 1;");
    
    Rectangle clipRegion = new Rectangle(canvasWidth, canvasHeight);
    clipWindow.setClip(clipRegion);

    preview.setFitWidth(canvasWidth);
    preview.setFitHeight(canvasHeight);
    preview.setManaged(false); 

    // --- OBJECT EXTRACTION LOGIC (The "Cursor Pick" Part) ---
    Label selectedColorLabel = new Label("Click image to pick color");
    selectedColorLabel.setStyle("-fx-text-fill: #8E8E93; -fx-font-size: 12px;");

    preview.setOnMouseClicked(e -> {
        // Logic from ObjectExtractor.java: Map mouse click to original image pixels
        java.awt.image.BufferedImage bimg = fxToBufferedImage(preview.getImage());
        int x = (int) (e.getX() * bimg.getWidth() / preview.getFitWidth());
        int y = (int) (e.getY() * bimg.getHeight() / preview.getFitHeight());

        if (x >= 0 && y >= 0 && x < bimg.getWidth() && y < bimg.getHeight()) {
            this.pickedColor = new java.awt.Color(bimg.getRGB(x, y), true);
            selectedColorLabel.setText(String.format("Selected: %d, %d, %d", 
                pickedColor.getRed(), pickedColor.getGreen(), pickedColor.getBlue()));
            selectedColorLabel.setStyle("-fx-text-fill: #34C759;");
        }
    });

    imageSection.getChildren().addAll(
        new Label("Transformation Preview") {{ setStyle("-fx-text-fill: white; -fx-opacity: 0.6;"); }},
        clipWindow
    );

    // --- RIGHT SIDE: TOOLS ---
    VBox controlSideBar = new VBox(15);
    controlSideBar.setPadding(new Insets(30, 25, 30, 25));
    controlSideBar.setPrefWidth(350);
    controlSideBar.setStyle("-fx-background-color: #000000; -fx-border-color: #2F3336; -fx-border-width: 0 0 0 1;");

    Label toolsLabel = new Label("Geometric & Selection");
    toolsLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 22px; -fx-text-fill: white;");

    // Sliders
    Label scaleLab = new Label("Scale: 100%");
    Slider scaleS = new Slider(10, 300, 100); 
    Label rotateLab = new Label("Rotation: 0°");
    Slider rotateS = new Slider(-180, 180, 0);
    Label transXLab = new Label("Translation X: 0");
    Slider transX = new Slider(-canvasWidth, canvasWidth, 0);
    Label transYLab = new Label("Translation Y: 0");
    Slider transY = new Slider(-canvasHeight, canvasHeight, 0);

    List.of(scaleLab, rotateLab, transXLab, transYLab).forEach(l -> l.setStyle("-fx-text-fill: #EBEBF5; -fx-font-size: 13px; -fx-opacity: 0.85;"));
    List.of(scaleS, rotateS, transX, transY).forEach(this::setupSliderDesign);

    Button saveBtn = new Button("Save Changes");
    saveBtn.setMaxWidth(Double.MAX_VALUE);
    saveBtn.setDisable(true);
    saveBtn.setStyle("-fx-background-color: #007AFF; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 12; -fx-opacity: 0.5;");

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

    // --- EXTRACTION SECTION ---
    Label extractLabel = new Label("Object Extraction");
    extractLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: white;");

    Slider toleranceS = new Slider(0, 150, 60);
    this.setupSliderDesign(toleranceS);

    Button extractBtn = new Button("Extract & Save Object");
    extractBtn.setMaxWidth(Double.MAX_VALUE);
    extractBtn.setStyle("-fx-background-color: #34C759; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 12;");
    
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

    // Assemble Sidebar
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
HBox page = new HBox(18, createGalleryMiniList(), mainLayout);
page.setPadding(new Insets(18));
root.setCenter(page);}

private java.awt.Color pickedColor = java.awt.Color.WHITE;

  private boolean isGrayscale = false; // Class-level flag


private void showEditingPage() {
    currentSection = "editing";
    if (currentImage == null) {
        showAlert("Please select an image from the Gallery first!");
        return;
    }
    
    isGrayscale = false; 

    HBox mainLayout = new HBox(0);
    mainLayout.setStyle("-fx-background-color: #000000;"); 
    VBox.setVgrow(mainLayout, Priority.ALWAYS);

    // --- LEFT SIDE: PREVIEW ---
    VBox imageSection = new VBox(20);
    imageSection.setPadding(new Insets(30));
    imageSection.setAlignment(Pos.CENTER);
    HBox.setHgrow(imageSection, Priority.ALWAYS);

    ImageView editPreview = new ImageView(mainImageView.getImage());
    editPreview.setFitHeight(550);
    editPreview.setPreserveRatio(true);

    StackPane displayStack = new StackPane(editPreview);
    displayStack.setStyle("-fx-border-width: 0; -fx-padding: 0;");
    
    StackPane container = new StackPane(displayStack);
    container.setStyle("-fx-background-color: #1a1a1a; -fx-background-radius: 12; -fx-padding: 20;");
    
    imageSection.getChildren().addAll(new Label("Edit Photo") {{ 
        setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold;"); 
    }}, container);

    // --- RIGHT SIDE: TOOLS ---
    VBox controlSideBar = new VBox(20);
    controlSideBar.setPadding(new Insets(30, 20, 30, 20));
    controlSideBar.setPrefWidth(320);
    controlSideBar.setStyle("-fx-background-color: #000000; -fx-border-color: #2F3336; -fx-border-width: 0 0 0 1;");

    Label toolsLabel = new Label("Adjustment Tools");
    toolsLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: white;");

    // --- NEW SAVE CHANGES BUTTON (Starts Inactive) ---
    Button saveBtn = new Button("Save Changes");
    saveBtn.setMaxWidth(Double.MAX_VALUE);
    saveBtn.setDisable(true); // Disable interaction
    saveBtn.setStyle("-fx-background-color: #007AFF; -fx-text-fill: white; -fx-font-weight: bold; " +
                     "-fx-background-radius: 10; -fx-padding: 12; -fx-opacity: 0.5;"); // Dimmed look

    // Helper function to "Wake Up" the save button
    Runnable activateSave = () -> {
        saveBtn.setDisable(false);
        saveBtn.setStyle("-fx-background-color: #007AFF; -fx-text-fill: white; -fx-font-weight: bold; " +
                         "-fx-background-radius: 10; -fx-padding: 12; -fx-opacity: 1.0;"); // Fully lit
    };

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

    // Shared update logic 
    Runnable updateImage = () -> {
        activateSave.run(); // Light up save button on change
        int bVal = (int) brightSlider.getValue();
        int cVal = (int) contrastSlider.getValue();
        brightTitle.setText("Brightness: " + bVal);
        contrastTitle.setText("Contrast: " + cVal);

        java.awt.image.BufferedImage img = fxToBufferedImage(mainImageView.getImage());
        img = dip_basic.BrightnessContrast.adjustBrightness(img, bVal);
        img = dip_basic.BrightnessContrast.adjustContrast(img, cVal);
        
        if (isGrayscale) {
            img = dip_basic.Grayscale.apply(img);
        }
        editPreview.setImage(bufferedToFxImage(img));
    };

    brightSlider.valueProperty().addListener((obs, old, val) -> updateImage.run());
    contrastSlider.valueProperty().addListener((obs, old, val) -> updateImage.run());

    grayBtn.setOnAction(e -> {
        isGrayscale = true;
        updateImage.run(); 
    });

    // --- BORDER SLIDER ---
    Label borderTitle = new Label("Frame Settings (Color Hue)");
    borderTitle.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");
    Slider hueSlider = new Slider(0, 360, 0);
    hueSlider.setStyle("-fx-background-color: linear-gradient(to right, red, orange, yellow, green, cyan, blue, violet, red); -fx-background-radius: 5;");
    hueSlider.valueProperty().addListener((obs, old, val) -> {
        activateSave.run(); // Light up save button on change
        javafx.scene.paint.Color fxColor = javafx.scene.paint.Color.hsb(val.doubleValue(), 1.0, 1.0);
        String hex = String.format("#%02X%02X%02X", (int)(fxColor.getRed()*255), (int)(fxColor.getGreen()*255), (int)(fxColor.getBlue()*255));
        displayStack.setStyle("-fx-border-color: " + hex + "; -fx-border-width: 20;");
    });

    // --- RESET / DISCARD ---
    Button resetBtn = new Button("Discard Changes");
    resetBtn.setMaxWidth(Double.MAX_VALUE);
    resetBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #FF3B30; -fx-font-weight: bold;");
    resetBtn.setOnAction(e -> {
        isGrayscale = false;
        brightSlider.setValue(0);
        contrastSlider.setValue(0);
        hueSlider.setValue(0);
        displayStack.setStyle("-fx-border-width: 0;");
        editPreview.setImage(mainImageView.getImage());
        
        // Reset save button to inactive state
        saveBtn.setDisable(true);
        saveBtn.setStyle("-fx-background-color: #007AFF; -fx-text-fill: white; -fx-font-weight: bold; " +
                         "-fx-background-radius: 10; -fx-padding: 12; -fx-opacity: 0.5;");
    });

    // Assembly
    controlSideBar.getChildren().addAll(
        toolsLabel, grayBtn, new Separator() {{ setStyle("-fx-background-color: #2F3336;"); }},
        brightTitle, brightSlider, 
        contrastTitle, contrastSlider, new Separator() {{ setStyle("-fx-background-color: #2F3336;"); }},
        borderTitle, hueSlider,
        saveBtn, // Added the professional blue button here
        new Region() {{ VBox.setVgrow(this, Priority.ALWAYS); }}, // Spacer
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
        "-fx-background-color: #f9fafb;" +
        "-fx-background-radius: 12;" +
        "-fx-border-color: #e5e7eb;" +
        "-fx-border-radius: 12;" +
        "-fx-padding: 6;" +
        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.10), 6, 0, 0, 2);"
);

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
    }

    private void saveAnnotation() {
        if (currentImage == null) {
            showAlert("Please select an image first.");
            return;
        }

        String annotation = annotationArea.getText();

        currentImage.setAnnotation(annotation);
        annotationManager.saveAnnotation(currentImage.getFilePath(), annotation);

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

    private void applyDarkMode() {
    String blackBg = "-fx-background-color: #000000;";
    String borderStyle = "-fx-border-color: #2F3336; -fx-border-width: 0 1 0 0;";
    
    // 1. Main Background
    root.setStyle(blackBg);
    
    // 2. Top Bar
    HBox topBar = (HBox) root.getTop();
topBar.setStyle(
        "-fx-background-color: rgba(255,255,255,0.95);" +
        "-fx-border-color: #e5e7eb;" +
        "-fx-border-width: 0 0 1 0;"
);
    topBar.getChildren().forEach(n -> {
        if (n instanceof Label) n.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 22px;");
    });

    // 3. Navigation (Left)
    VBox nav = (VBox) root.getLeft();
    nav.setStyle(blackBg + borderStyle);

    // 4. Center Gallery (Fixes the big white area in image_5c9d98.png)
    if (root.getCenter() instanceof javafx.scene.layout.Region) {
        javafx.scene.layout.Region center = (javafx.scene.layout.Region) root.getCenter();
        center.setStyle(blackBg);
        
        // This targets the internal scroll pane and viewport to kill the white light
        center.lookupAll(".scroll-pane").forEach(node -> 
            node.setStyle("-fx-background: #000000; -fx-background-color: transparent; -fx-border-color: transparent;")
        );
        center.lookupAll(".viewport").forEach(node -> 
            node.setStyle("-fx-background-color: transparent;")
        );
    }

    // 5. Right Side (Annotation)
    VBox right = (VBox) root.getRight();
    right.setStyle("-fx-background-color: #000000; -fx-border-color: #2F3336; -fx-border-width: 0 0 0 1;");
    annotationArea.setStyle("-fx-control-inner-background: #121212; -fx-text-fill: white; -fx-border-color: #2F3336;");
}

private void applyLightMode() {
    // Restore the standard light background
root.setStyle("-fx-background-color: #eef2f3;");    
    HBox topBar = (HBox) root.getTop();
topBar.setStyle(
        "-fx-background-color: #ffffff;" +
        "-fx-border-color: #dcdcdc;" +
        "-fx-border-width: 0 0 1 0;"
);
    // Update the button icon visibility
    if (themeToggle != null) {
        themeToggle.setText("☀️");
        themeToggle.setStyle("-fx-background-color: transparent; -fx-font-size: 18px; -fx-text-fill: black;");
    }
}

private void showSharePage() {
    VBox shareLayout = new VBox(20);
    shareLayout.setAlignment(Pos.CENTER);
    shareLayout.setPadding(new Insets(30));

    Label title = new Label("📤 External Distribution");
    title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

    Label desc = new Label("Select a platform to share your current image:");
    
    // Create the Action Buttons
    Button whatsappBtn = new Button("Share to WhatsApp");
    Button emailBtn = new Button("Share via Email");
    
    // Style them (assuming you have a nav-button style)
    whatsappBtn.getStyleClass().add("nav-button");
    emailBtn.getStyleClass().add("nav-button");

    // BRIDGE TO CONTROLLER
    whatsappBtn.setOnAction(e -> {
        if (currentImage != null) {
            
            // 2. Show the instructions clearly
            Alert instructions = new Alert(Alert.AlertType.INFORMATION);
            instructions.setTitle("WhatsApp Sharing Instructions");
            instructions.setHeaderText("Image Copied to Clipboard!");
            instructions.setContentText(
                "1. WhatsApp Web will now open in your browser.\n" +
                "2. Select the contact you want to share with.\n" +
                "3. Click on the message box and press CTRL + V to paste the image.\n"
            );
            instructions.showAndWait();

            controller.handleWhatsAppShare(currentImage);
        }
    });

    shareLayout.getChildren().addAll(title, desc, whatsappBtn, emailBtn);
    
    // Set this as the center of your main layout
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
private void showMosaicPage() {
    currentSection = "mosaic";
    VBox layout = new VBox(18);
    layout.setPadding(new Insets(22));
    layout.setAlignment(Pos.TOP_CENTER);
    layout.setStyle(cardStyle());

    Label title = new Label("🖼️ Shape Mosaic Studio");
    title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #111827;");

    Label subtitle = new Label("Create a large shape using many small photo tiles from your collection.");
    subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #6b7280;");

    ComboBox<String> shapeChoice = new ComboBox<>();
    shapeChoice.getItems().addAll("Circle", "Heart", "Diamond", "Hexagon");
    shapeChoice.setValue("Hexagon");

    TextField tileSizeField = new TextField("60");
    tileSizeField.setMaxWidth(90);

    TextField canvasSizeField = new TextField("720");
    canvasSizeField.setMaxWidth(90);

    CheckBox useAnnotatedOnly = new CheckBox("Use favourite/annotated images only");
    CheckBox shuffleImages = new CheckBox("Shuffle tiles");

    HBox settings = new HBox(12,
            new Label("Shape:"), shapeChoice,
            new Label("Tile Size:"), tileSizeField,
            new Label("Canvas:"), canvasSizeField
    );
    settings.setAlignment(Pos.CENTER);

    HBox options = new HBox(20, useAnnotatedOnly, shuffleImages);
    options.setAlignment(Pos.CENTER);

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

    Button generateBtn = new Button("✨ Generate Shape Mosaic");
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

    generateBtn.setOnAction(e -> {
        if (imageList.isEmpty()) {
            showAlert("Please open an image folder first.");
            return;
        }

        try {
            int tileSize = Integer.parseInt(tileSizeField.getText());
            int canvasSize = Integer.parseInt(canvasSizeField.getText());

            List<ImageModel> selectedModels = new ArrayList<>();

            for (ImageModel model : imageList) {
                if (!useAnnotatedOnly.isSelected() || annotationManager.hasAnnotation(model.getFilePath())) {
                    selectedModels.add(model);
                }
            }

            if (selectedModels.isEmpty()) {
                showAlert("No images available for mosaic.");
                return;
            }

            if (shuffleImages.isSelected()) {
                java.util.Collections.shuffle(selectedModels);
            }

            List<BufferedImage> tiles = new ArrayList<>();

            for (ImageModel model : selectedModels) {
                BufferedImage img = ImageIO.read(new File(model.getFilePath()));
                if (img != null) {
                    tiles.add(img);
                }
            }

            currentMosaic[0] = createShapeMosaic(tiles, shapeChoice.getValue(), canvasSize, tileSize);
            mosaicView.setImage(SwingFXUtils.toFXImage(currentMosaic[0], null));

        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert("Failed to generate mosaic.");
        }
    });

    saveBtn.setOnAction(e -> {
        if (currentMosaic[0] == null) {
            showAlert("Please generate mosaic first.");
            return;
        }

        try {
            File output = new File("shape_mosaic_" + System.currentTimeMillis() + ".png");
            ImageIO.write(currentMosaic[0], "png", output);
            showAlert("Mosaic saved as: " + output.getName());
        } catch (Exception ex) {
            showAlert("Failed to save mosaic.");
        }
    });

    clearBtn.setOnAction(e -> {
        mosaicView.setImage(null);
        currentMosaic[0] = null;
    });

    HBox buttons = new HBox(12, generateBtn, saveBtn, clearBtn);
    buttons.setAlignment(Pos.CENTER);

    layout.getChildren().addAll(title, subtitle, settings, options, buttons, previewBox);

HBox page = new HBox(18, createGalleryMiniList(), layout);
    page.setPadding(new Insets(18));
    root.setCenter(page);
}
private BufferedImage createShapeMosaic(List<BufferedImage> tiles, String shape, int canvasSize, int tileSize) {
    BufferedImage output = new BufferedImage(canvasSize, canvasSize, BufferedImage.TYPE_INT_ARGB);
    java.awt.Graphics2D g = output.createGraphics();

    g.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);

    // Base background full frame
    g.setColor(new java.awt.Color(230, 235, 240));
    g.fillRect(0, 0, canvasSize, canvasSize);

    int tileIndex = 0;

    for (int y = 0; y < canvasSize; y += tileSize) {
        for (int x = 0; x < canvasSize; x += tileSize) {

            BufferedImage tile = tiles.get(tileIndex % tiles.size());
            java.awt.Image scaled = tile.getScaledInstance(tileSize, tileSize, java.awt.Image.SCALE_SMOOTH);

            int centerX = x + tileSize / 2;
            int centerY = y + tileSize / 2;

            if (isPointInsideShape(centerX, centerY, canvasSize, shape)) {
                // Inside shape = full bright photo tiles
                g.drawImage(scaled, x, y, null);
            } else {
                // Outside shape = faded base tiles
                java.awt.Graphics2D tileG = (java.awt.Graphics2D) g.create();
                tileG.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 0.18f));
                tileG.drawImage(scaled, x, y, null);
                tileG.dispose();

                // soft white overlay
                g.setColor(new java.awt.Color(255, 255, 255, 130));
                g.fillRect(x, y, tileSize, tileSize);
            }

            tileIndex++;
        }
    }

  
    g.dispose();
    return output;
}

private boolean isPointInsideShape(int x, int y, int size, String shape) {
    double cx = size / 2.0;
    double cy = size / 2.0;

    double dx = (x - cx) / cx;
    double dy = (y - cy) / cy;

    switch (shape) {
        case "Circle":
            return dx * dx + dy * dy <= 0.85;

        case "Diamond":
            return Math.abs(dx) + Math.abs(dy) <= 1.1;

        case "Hexagon":
            return Math.abs(dx) <= 0.9 &&
                   Math.abs(dy) <= 0.75 &&
                   Math.abs(dx) * 0.6 + Math.abs(dy) <= 0.95;

        case "Heart":
            double heartX = dx * 1.25;
            double heartY = -dy * 1.25;
            double formula = Math.pow(heartX * heartX + heartY * heartY - 1, 3)
                    - heartX * heartX * Math.pow(heartY, 3);
            return formula <= 0;

        default:
            return true;
    }
}

private BufferedImage createSimpleMosaic(List<BufferedImage> images, int cols, int tileSize) {
    int rows = (int) Math.ceil(images.size() / (double) cols);

    BufferedImage mosaic = new BufferedImage(
            cols * tileSize,
            rows * tileSize,
            BufferedImage.TYPE_INT_RGB
    );

    java.awt.Graphics2D g = mosaic.createGraphics();

    int x = 0;
    int y = 0;

    for (BufferedImage img : images) {
        java.awt.Image scaled = img.getScaledInstance(tileSize, tileSize, java.awt.Image.SCALE_SMOOTH);
        g.drawImage(scaled, x, y, null);

        x += tileSize;

        if (x >= cols * tileSize) {
            x = 0;
            y += tileSize;
        }
    }

    g.dispose();
    return mosaic;
}

private void showVideoPage() {
    currentSection = "video";
    VBox layout = new VBox(18);
    layout.setPadding(new Insets(22));
    layout.setAlignment(Pos.TOP_CENTER);
    layout.setStyle(cardStyle());

    Label title = new Label("🎬 Video Story Creator");
    title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #111827;");

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
    videoPane.setStyle(
            "-fx-background-color: #111827;" +
            "-fx-background-radius: 18;" +
            "-fx-padding: 18;" +
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

        showAlert("Caption saved for this image.");
    });

    Button playBtn = new Button("▶ Play");
    Button pauseBtn = new Button("⏸ Pause");
    Button restartBtn = new Button("🔁 Restart");
    Button prevBtn = new Button("⬅ Previous");
    Button nextBtn = new Button("Next ➡");

    playBtn.setStyle(greenButtonStyle());
    pauseBtn.setStyle(primaryButtonStyle());
    restartBtn.setStyle(primaryButtonStyle());
    prevBtn.setStyle(primaryButtonStyle());
    nextBtn.setStyle(primaryButtonStyle());

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

        if (timeline[0] != null) {
            timeline[0].stop();
        }

        timeline[0] = new Timeline(new KeyFrame(Duration.seconds(durationSlider.getValue()), event -> {
            showCurrentSlide.run();

            index[0]++;

            if (index[0] >= favouriteImages.size()) {
                index[0] = 0;
            }
        }));

        timeline[0].setCycleCount(Timeline.INDEFINITE);
        timeline[0].play();
    });

    pauseBtn.setOnAction(e -> {
        if (timeline[0] != null) {
            timeline[0].pause();
        }
    });

    restartBtn.setOnAction(e -> {
        index[0] = 0;
        showCurrentSlide.run();

        if (timeline[0] != null) {
            timeline[0].playFromStart();
        }
    });

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

    HBox playbackControls = new HBox(10, prevBtn, playBtn, pauseBtn, restartBtn, nextBtn);
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
}



