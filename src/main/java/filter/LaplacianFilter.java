package filter;

import color.Util;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

public class LaplacianFilter extends AbstractLinearFilter {

    private double[][] mask;

    public LaplacianFilter(double[][] mask) {
        this.mask = mask;
    }

    @Override
    public BufferedImage process(BufferedImage src) {
        return applyMask(src, mask, 1);
    }

    public BufferedImage clip(BufferedImage src, BufferedImage img, boolean useDynamicThreshold) {
        int width = src.getWidth();
        int height = src.getHeight();
        int thresholdB = 128;
        int thresholdG = 128;
        int thresholdR = 128;
        BufferedImage res = new BufferedImage(width, height, src.getType());
        byte[] srcData = ((DataBufferByte) src.getRaster().getDataBuffer()).getData();
        byte[] resData = ((DataBufferByte) res.getRaster().getDataBuffer()).getData();
        byte[] imgData = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();
        for (int i = 0; i < width * height; i++) {
            int blue = Byte.toUnsignedInt(srcData[i * 3]);
            int green = Byte.toUnsignedInt(srcData[i * 3 + 1]);
            int red = Byte.toUnsignedInt(srcData[i * 3 + 2]);
            if(useDynamicThreshold) {
                thresholdB = Byte.toUnsignedInt(imgData[i * 3]);
                thresholdG = Byte.toUnsignedInt(imgData[i * 3 + 1]);
                thresholdR = Byte.toUnsignedInt(imgData[i * 3 + 2]);
            }
            resData[i * 3] = (byte) Util.clip(blue + thresholdB, 0, 255);
            resData[i * 3 + 1] = (byte) Util.clip(green + thresholdG, 0, 255);
            resData[i * 3 + 2] = (byte) Util.clip(red + thresholdR, 0, 255);
        }
        return res;
    }
}
