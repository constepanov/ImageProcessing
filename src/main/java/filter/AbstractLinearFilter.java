package filter;

import java.awt.image.BufferedImage;

import static color.Util.clip;

public abstract class AbstractLinearFilter {

    public abstract BufferedImage process(BufferedImage src);

    BufferedImage applyMask(BufferedImage src, double[][] mask, double z) {
        int width = src.getWidth();
        int height = src.getHeight();
        BufferedImage resultImage = new BufferedImage(width, height, src.getType());
        int maskLength = mask.length;
        int radius = maskLength / 2;
        int[] pixel = new int[3];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int b = 0;
                int g = 0;
                int r = 0;
                for (int k = 0; k < maskLength; k++) {
                    for (int m = 0; m < maskLength; m++) {
                        int offsetX = x + (m - radius);
                        int offsetY = y + (k - radius);
                        double w = mask[k][m];
                        if (offsetY < 0 || offsetX < 0 || offsetY >= height || offsetX >= width) {
                            pixel = getNearestPixel(src, offsetX,offsetY);
                        } else {
                            pixel = src.getRaster().getPixel(offsetX, offsetY, new int[3]);
                        }
                        b += w * pixel[0];
                        g += w * pixel[1];
                        r += w * pixel[2];
                    }
                }
                b /= z;
                g /= z;
                r /= z;
                pixel[0] = clip(b, 0, 255);
                pixel[1] = clip(g, 0, 255);
                pixel[2] = clip(r, 0, 255);
                resultImage.getRaster().setPixel(x, y, pixel);
            }
        }
        return resultImage;
    }

    private int[] getNearestPixel(BufferedImage src, int offsetX, int offsetY) {
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
        return src.getRaster().getPixel(x, y, new int[3]);
    }
}
