package color;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import static java.lang.Math.*;

public class Util {

    private Util() {}

    public static int clip(double x, int min, int max) {
        if (x < min) {
            return min;
        } else if (x > max) {
            return max;
        } else {
            return (int) x;
        }
    }

    public static int[] flatten(int[][] array) {
        int height = array.length;
        int width = array[0].length;
        int[] result = new int[height * width];
        for (int i = 0; i < height; i++) {
            System.arraycopy(array[i], 0, result, i * width, width);
        }
        return result;
    }

    public static double calculatePSNR(BufferedImage original, BufferedImage processed) {
        byte[] a = ((DataBufferByte) original.getRaster().getDataBuffer()).getData();
        byte[] b = ((DataBufferByte) processed.getRaster().getDataBuffer()).getData();
        int width = original.getWidth();
        int height = original.getHeight();
        double sum = 0;
        for (int i = 0; i < width * height; i++) {
            sum += pow(Byte.toUnsignedInt(a[i * 3]) - Byte.toUnsignedInt(b[i * 3]), 2);
        }
        return 10 * log10((width * height * pow(255, 2)) / sum);
    }
}
