package filter;

import java.awt.*;
import java.awt.image.BufferedImage;

import static color.Util.clip;

abstract class AbstractLinearFilter extends AbstractFilter {

    BufferedImage applyMask(BufferedImage src, double[][] mask, double z) {
        int width = src.getWidth();
        int height = src.getHeight();
        BufferedImage resultImage = new BufferedImage(width, height, src.getType());
        int maskLength = mask.length;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int b = 0;
                int g = 0;
                int r = 0;
                int[][] pixels = getAperture(src, maskLength, x, y);
                for (int k = 0; k < maskLength; k++) {
                    for (int m = 0; m < maskLength; m++) {
                        double w = mask[k][m];
                        int pixel = pixels[k][m];
                        b += w * ((pixel) & 0xff);
                        g += w * ((pixel >> 8) & 0xff);
                        r += w * ((pixel >> 16) & 0xff);
                    }
                }
                b /= z;
                g /= z;
                r /= z;
                b = clip(b, 0, 255);
                g = clip(g, 0, 255);
                r = clip(r, 0, 255);
                Color resColor = new Color(r, g, b);
                resultImage.setRGB(x, y, resColor.getRGB());
            }
        }
        return resultImage;
    }
}
