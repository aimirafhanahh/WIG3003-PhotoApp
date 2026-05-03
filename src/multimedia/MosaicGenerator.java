package multimedia;

import repository.ImageModel; // Standard model used by all members
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import javax.imageio.ImageIO;

public class MosaicGenerator {
    /**
     * Creates a grid-style mosaic from a list of images.
     * @param images List of image data
     * @param columns Number of columns in the grid
     * @return A combined BufferedImage
     */
    public BufferedImage createGrid(List<ImageModel> images, int columns) {
        if (images == null || images.isEmpty()) return null;

        try {
            int totalImages = images.size();
            int rows = (int) Math.ceil((double) totalImages / columns);

            // Using the first image to define tile dimensions
            BufferedImage firstImg = ImageIO.read(new File(images.get(0).getFilePath()));
            int tileWidth = firstImg.getWidth();
            int tileHeight = firstImg.getHeight();

            BufferedImage result = new BufferedImage(tileWidth * columns, tileHeight * rows, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = result.createGraphics();

            for (int i = 0; i < totalImages; i++) {
                BufferedImage img = ImageIO.read(new File(images.get(i).getFilePath()));
                int x = (i % columns) * tileWidth;
                int y = (i / columns) * tileHeight;
                g2d.drawImage(img, x, y, tileWidth, tileHeight, null);
            }
            g2d.dispose();
            return result;

        } catch (Exception e) {
            System.err.println("Error creating mosaic: " + e.getMessage());
            return null;
        }
    }
}