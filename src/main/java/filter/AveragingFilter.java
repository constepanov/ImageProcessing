package filter;

import java.awt.image.BufferedImage;

public class AveragingFilter extends AbstractLinearFilter {

    private int radius;

    public AveragingFilter(int radius) {
        this.radius = radius;
    }

    @Override
    public BufferedImage process(BufferedImage src) {
        int maskLength = 2 * radius + 1;
        double[][] mask = new double[maskLength][maskLength];
        double z = 0.0;
        for (int i = 0; i < maskLength; i++) {
            for (int j = 0; j < maskLength; j++) {
                mask[i][j] = 1;
                z += mask[i][j];
            }
        }
        return applyMask(src, mask, z);
    }
}
