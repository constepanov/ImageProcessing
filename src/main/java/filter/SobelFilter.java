package filter;

import java.awt.*;
import java.awt.image.BufferedImage;

import static java.lang.Math.*;

public class SobelFilter extends AbstractLinearFilter {

    private double[][] xMask = {
            {-1, 0, 1},
            {-2, 0, 2},
            {-1, 0, 1}
    };

    private double[][] yMask = {
            {1, 2, 1},
            {0, 0, 0},
            {-1, -2, -1}
    };

    private double[][] direction;

    private int threshold;

    public SobelFilter(int threshold) {
        this.threshold = threshold;
    }

    private double[][] applyMaskDouble(BufferedImage src, double[][] mask) {
        int width = src.getWidth();
        int height = src.getHeight();
        double[][] result = new double[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double b = 0;
                int[][] pixels = getAperture(src, mask.length, x, y);
                for (int k = 0; k < mask.length; k++) {
                    for (int m = 0; m < mask.length; m++) {
                        double w = mask[k][m];
                        Color pixel = new Color(pixels[k][m]);
                        b += w * pixel.getBlue();
                    }
                }
                result[x][y] = b;
            }
        }
        return result;
    }

    @Override
    public BufferedImage process(BufferedImage src) {
        int width = src.getWidth();
        int height = src.getHeight();
        double[][] xFiltered = applyMaskDouble(src, xMask);
        double[][] yFiltered = applyMaskDouble(src, yMask);
        double[][] magnitude = new double[height][width];
        direction = new double[height][width];
        for (int x = 0; x < height; x++) {
            for (int y = 0; y < width; y++) {
                magnitude[x][y] = hypot(xFiltered[x][y], yFiltered[x][y]);
                direction[x][y] = toDegrees(atan2(xFiltered[x][y], yFiltered[x][y]));
            }
        }

        BufferedImage result = new BufferedImage(width, height, src.getType());
        for (int x = 0; x < height; x++) {
            for (int y = 0; y < width; y++) {
                int[] pixel = result.getRaster().getPixel(x, y, new int[3]);
                int value = magnitude[x][y] > threshold ? 255 : 0;
                pixel[0] = value;
                pixel[1] = value;
                pixel[2] = value;
                result.getRaster().setPixel(x, y, pixel);
            }
        }
        return result;
    }

    public double[][] getDirection() {
        return direction;
    }

    public BufferedImage getGradientDirectionsMap(double[][] direction) {
        int width = direction[0].length;
        int height = direction.length;
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        for (int i = 0; i < direction.length; i++) {
            for (int j = 0; j < direction[0].length; j++) {
                if (direction[i][j] <= -90 && direction[i][j] >= -180) {
                    result.setRGB(i, j, Color.RED.getRGB());
                } else if (direction[i][j] <= 0 && direction[i][j] >= -90) {
                    result.setRGB(i, j, Color.GREEN.getRGB());
                } else if (direction[i][j] <= 90 && direction[i][j] >= 0) {
                    result.setRGB(i, j, Color.BLUE.getRGB());
                } else {
                    result.setRGB(i, j, Color.WHITE.getRGB());
                }
            }
        }
        return result;
    }
}
