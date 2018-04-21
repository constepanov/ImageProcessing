package filter;

import java.awt.image.BufferedImage;

public abstract class AbstractFilter {
    public abstract BufferedImage process(BufferedImage src);

    private int getNearestPixel(BufferedImage src, int offsetX, int offsetY) {
        int width = src.getWidth();
        int height = src.getHeight();
        int x, y;
        if (offsetX < 0) {
            x = 0;
        } else if (offsetX >= width) {
            x = width - 1;
        } else {
            x = offsetX;
        }
        if (offsetY < 0) {
            y = 0;
        } else if (offsetY >= height) {
            y = height - 1;
        } else {
            y = offsetY;
        }
        return src.getRGB(x, y);
    }

    int[][] getAperture(BufferedImage src, int apertureLength, int x, int y) {
        int[][] aperture = new int[apertureLength][apertureLength];
        int radius = apertureLength / 2;
        int width = src.getWidth();
        int height = src.getHeight();
        for (int k = 0; k < apertureLength; k++) {
            for (int m = 0; m < apertureLength; m++) {
                int offsetX = x + (m - radius);
                int offsetY = y + (k - radius);
                if (offsetY < 0 || offsetX < 0 || offsetY >= height || offsetX >= width) {
                    aperture[k][m] = getNearestPixel(src, offsetX, offsetY);
                } else {
                    aperture[k][m] = src.getRGB(offsetX, offsetY);
                }
            }
        }
        return aperture;
    }
}
