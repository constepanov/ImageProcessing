package filter;

import color.Util;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

public class LaplacianFilter extends AbstractLinearFilter {

    private double[][] mask;

    public LaplacianFilter(double alpha) {
        this.mask = new double[][] {
                {0, -1, 0},
                {-1, alpha + 4, -1},
                {0, -1, 0}
        };
    }

    @Override
    public BufferedImage process(BufferedImage src) {
        return applyMask(src, mask, 1);
    }

    public BufferedImage clip(BufferedImage src, BufferedImage img, boolean useDynamicThreshold) {
        int width = src.getWidth();
        int height = src.getHeight();
        int threshold = 128;
        BufferedImage res = new BufferedImage(width, height, src.getType());
        byte[] srcData = ((DataBufferByte) src.getRaster().getDataBuffer()).getData();
        byte[] resData = ((DataBufferByte) res.getRaster().getDataBuffer()).getData();
        byte[] imgData = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();
        for (int i = 0; i < width * height; i++) {
            int blue = Byte.toUnsignedInt(srcData[i * 3]);
            if(useDynamicThreshold) {
                threshold = Byte.toUnsignedInt(imgData[i * 3]);
            }
            resData[i * 3] = (byte) Util.clip(blue + threshold, 0, 255);
            resData[i * 3 + 1] = (byte) Util.clip(blue + threshold, 0, 255);
            resData[i * 3 + 2] = (byte) Util.clip(blue + threshold, 0, 255);
        }
        return res;
    }
}
