package multimedia;

import org.jcodec.api.awt.AWTSequenceEncoder;
import repository.ImageModel;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

public class VideoCreator {
    public void createSlideshow(List<ImageModel> imageList, String outputFileName) {
        AWTSequenceEncoder encoder = null;
        try {
            File out = new File(outputFileName);

            if (out.exists()) {
                out.delete();
                System.out.println("Deleted old file: " + outputFileName);
            }

            encoder = AWTSequenceEncoder.createSequenceEncoder(out, 25);

            int frameCount = 0;
            for (ImageModel model : imageList) {
                File imageFile = new File(model.getFilePath());
                if (!imageFile.exists()) {
                    System.err.println("Warning: Image file not found: " + model.getFilePath());
                    continue;
                }

                BufferedImage originalImg = ImageIO.read(imageFile);
                if (originalImg == null) {
                    System.err.println("Warning: Failed to read image: " + model.getFilePath());
                    continue;
                }

                int maxWidth = 1280;
                int maxHeight = 720;

                int originalWidth = originalImg.getWidth();
                int originalHeight = originalImg.getHeight();

                double scale = Math.min((double)maxWidth / originalWidth, (double)maxHeight / originalHeight);

                int targetWidth = (int)(originalWidth * scale);
                int targetHeight = (int)(originalHeight * scale);

                targetWidth = (targetWidth + 1) & ~1;
                targetHeight = (targetHeight + 1) & ~1;

                BufferedImage resizedImg = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
                Graphics2D g2 = resizedImg.createGraphics();

                g2.setColor(Color.WHITE);
                g2.fillRect(0, 0, targetWidth, targetHeight);

                int scaledWidth = (int)(originalWidth * scale);
                int scaledHeight = (int)(originalHeight * scale);
                int x = (targetWidth - scaledWidth) / 2;
                int y = (targetHeight - scaledHeight) / 2;

                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2.drawImage(originalImg, x, y, scaledWidth, scaledHeight, null);
                g2.dispose();

                encoder.encodeImage(resizedImg);
                frameCount++;
                System.out.println("Encoded frame " + frameCount + ": " + imageFile.getName() +
                        " (" + targetWidth + "x" + targetHeight + ")");
            }

            if (frameCount > 0) {
                encoder.finish();
                System.out.println("Video synthesis completed: " + outputFileName + " (Total frames: " + frameCount + ")");
            } else {
                System.err.println("No frames were successfully encoded");
            }
        } catch (Exception e) {
            System.err.println("Video creation failed: " + e.getClass().getSimpleName());
            e.printStackTrace();
        } finally {
            if (encoder != null) {
                try {
                    encoder.finish();
                } catch (Exception e) {
                    // Ignore errors from duplicate close
                }
            }
        }
    }
}
