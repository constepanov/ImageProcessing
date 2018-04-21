package filter;

import color.Util;

import java.awt.image.BufferedImage;
import java.util.Arrays;

import static java.lang.Math.floor;
import static java.lang.Math.pow;

public class MedianFilter extends AbstractFilter {

    private int radius;

    public MedianFilter(int radius) {
        this.radius = radius;
    }

    @Override
    public BufferedImage process(BufferedImage src) {
        int apertureLength = 2 * radius + 1;
        int width = src.getWidth();
        int height = src.getHeight();
        BufferedImage resultImage = new BufferedImage(width, height, src.getType());
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int[][] aperture = getAperture(src, apertureLength, x, y);
                int[] pixels = Util.flatten(aperture);
                Arrays.sort(pixels);
                int index = (int) (floor(pow(apertureLength, 2) / 2) + 1);
                resultImage.setRGB(x, y, pixels[index]);
            }
        }
        return resultImage;
    }
}
