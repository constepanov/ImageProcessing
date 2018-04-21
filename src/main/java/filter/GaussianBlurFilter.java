package filter;

import java.awt.image.BufferedImage;

import static java.lang.Math.*;

public class GaussianBlurFilter extends AbstractLinearFilter {

    private double sigma;
    private int radius;

    public GaussianBlurFilter(double sigma, int radius) {
        this.sigma = sigma;
        this.radius = radius;
    }

    @Override
    public BufferedImage process(BufferedImage src) {
        int maskLength = 2 * radius + 1;
        double[][] mask = new double[maskLength][maskLength];
        double z = 0.0;
        for (int i = 0; i < maskLength; i++) {
            for (int j = 0; j < maskLength; j++) {
                int x = i - radius;
                int y = j - radius;
                mask[i][j] = exp(-(pow(x, 2) + pow(y, 2)) / (2.0 * pow(sigma, 2)));
                z += mask[i][j];
            }
        }
        return applyMask(src, mask, z);
    }
}
