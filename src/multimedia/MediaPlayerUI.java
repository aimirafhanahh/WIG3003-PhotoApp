package multimedia;

import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import java.io.File;

public class MediaPlayerUI extends VBox {
    private MediaPlayer mediaPlayer;

    public MediaPlayerUI() {
        this.setAlignment(Pos.CENTER);
        this.setSpacing(15);
    }

    /**
     * Loads and initializes the video player with a file path.
     * @param videoPath Relative or absolute path to the .mp4 file
     */
    public void loadVideo(String videoPath) {
        File file = new File(videoPath);
        if (!file.exists()) {
            System.err.println("Video file not found: " + videoPath);
            return;
        }

        Media media = new Media(file.toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        MediaView mediaView = new MediaView(mediaPlayer);

        // Adjust fit to match the UI layout
        mediaView.setFitWidth(600);
        mediaView.setPreserveRatio(true);

        // Control buttons
        Button playBtn = new Button("Play");
        Button pauseBtn = new Button("Pause");
        Button stopBtn = new Button("Stop");

        playBtn.setOnAction(e -> mediaPlayer.play());
        pauseBtn.setOnAction(e -> mediaPlayer.pause());
        stopBtn.setOnAction(e -> mediaPlayer.stop());

        HBox controls = new HBox(10, playBtn, pauseBtn, stopBtn);
        controls.setAlignment(Pos.CENTER);

        this.getChildren().clear();
        this.getChildren().addAll(mediaView, controls);
    }
}