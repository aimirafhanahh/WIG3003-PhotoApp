package dip_basic;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Border {

    public static BufferedImage addBorder(BufferedImage img, int thickness) {

        int width = img.getWidth();
        int height = img.getHeight();

        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Graphics2D g = result.createGraphics();

        g.drawImage(img, 0, 0, null);

        g.setColor(Color.BLACK);

        g.fillRect(0, 0, width, thickness);

        g.fillRect(0, height - thickness, width, thickness);

        g.fillRect(0, 0, thickness, height);

        g.fillRect(width - thickness, 0, thickness, height);

        g.dispose();

        return result;
    }

    public static BufferedImage addBorder(BufferedImage img, int thickness, Color borderColor) {
    int width = img.getWidth();
    int height = img.getHeight();
    BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    Graphics2D g = result.createGraphics();

    g.drawImage(img, 0, 0, null);
    g.setColor(borderColor); // Uses the color passed from the UI

    g.fillRect(0, 0, width, thickness);
    g.fillRect(0, height - thickness, width, thickness);
    g.fillRect(0, 0, thickness, height);
    g.fillRect(width - thickness, 0, thickness, height);

    g.dispose();
    return result;
}
}