package multimedia;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class MosaicGenerator {

    public static BufferedImage createMosaic(List<BufferedImage> images, int cols, int tileSize) {
        int rows = (int) Math.ceil(images.size() / (double) cols);

        BufferedImage mosaic = new BufferedImage(
                cols * tileSize,
                rows * tileSize,
                BufferedImage.TYPE_INT_RGB
        );

        Graphics2D g = mosaic.createGraphics();

        int x = 0, y = 0;

        for (BufferedImage img : images) {
            Image scaled = img.getScaledInstance(tileSize, tileSize, Image.SCALE_SMOOTH);
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
}