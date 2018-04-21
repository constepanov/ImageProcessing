package noise;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.Random;

import static color.Util.clip;

public class AdditiveNoise implements ImageNoise {

    private double sigma;

    public AdditiveNoise(double sigma) {
        this.sigma = sigma;
    }

    @Override
    public BufferedImage processImage(BufferedImage src) {
        int width = src.getWidth();
        int height = src.getHeight();
        Random r = new Random();
        double sigmaPow2 = Math.pow(sigma, 2);
        byte[] srcPixels = ((DataBufferByte) src.getRaster().getDataBuffer()).getData();
        BufferedImage result = new BufferedImage(width, height, src.getType());
        byte[] resultPixels = ((DataBufferByte) result.getRaster().getDataBuffer()).getData();
        for (int i = 0; i < width * height; i++) {
            int currentI = Byte.toUnsignedInt(srcPixels[i * 3]);
            int resultI = clip(currentI + r.nextGaussian() * sigmaPow2, 0, 255);
            resultPixels[i * 3] = (byte) resultI;
            resultPixels[i * 3 + 1] = (byte) resultI;
            resultPixels[i * 3 + 2] = (byte) resultI;
        }
        return result;
    }
}
