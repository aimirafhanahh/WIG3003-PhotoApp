package dip_basic;

import java.awt.image.BufferedImage;

public class Grayscale {

    public static BufferedImage apply(BufferedImage img) {

        int width = img.getWidth();
        int height = img.getHeight();

        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                int pixel = img.getRGB(x, y);

                int r = (pixel >> 16) & 0xff;
                int g = (pixel >> 8) & 0xff;
                int b = pixel & 0xff;

                int gray = (r + g + b) / 3;

                int newPixel = (gray << 16) | (gray << 8) | gray;

                result.setRGB(x, y, newPixel);
            }
        }

        return result;
    }
}