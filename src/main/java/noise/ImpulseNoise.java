package noise;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

public class ImpulseNoise implements ImageNoise {

    private double pa;
    private double pb;

    public ImpulseNoise(double pa, double pb) {
        this.pa = pa;
        this.pb = pb;
    }

    @Override
    public BufferedImage processImage(BufferedImage src) {
        int width = src.getWidth();
        int height = src.getHeight();
        byte[] srcPixels = ((DataBufferByte) src.getRaster().getDataBuffer()).getData();
        BufferedImage result = new BufferedImage(width, height, src.getType());
        byte[] resultPixels = ((DataBufferByte) result.getRaster().getDataBuffer()).getData();
        for (int i = 0; i < width * height; i++) {
            double value = Math.random();
            int resultI = Byte.toUnsignedInt(srcPixels[i * 3]);
            if (value < pa) {
                resultI = 0;
            } else if (value < pa + pb) {
                resultI = 255;
            }
            resultPixels[i * 3] = (byte) resultI;
            resultPixels[i * 3 + 1] = (byte) resultI;
            resultPixels[i * 3 + 2] = (byte) resultI;
        }
        return result;
    }
}
