package color;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import static color.Util.clip;

public class ColorConverter {
    public static BufferedImage fromRGBToYYY(BufferedImage src) {
        int width = src.getWidth();
        int height = src.getHeight();
        byte[] rgb = ((DataBufferByte) src.getRaster().getDataBuffer()).getData();
        BufferedImage result = new BufferedImage(width, height, src.getType());
        byte[] pixels = ((DataBufferByte) result.getRaster().getDataBuffer()).getData();
        for (int i = 0; i < width * height; i++) {
            int blue = Byte.toUnsignedInt(rgb[i * 3]);
            int green = Byte.toUnsignedInt(rgb[i * 3 + 1]);
            int red = Byte.toUnsignedInt(rgb[i * 3 + 2]);
            int y = clip(0.299 * red + 0.587 * green + 0.114 * blue, 0, 255);
            pixels[i * 3] = (byte) y;
            pixels[i * 3 + 1] = (byte) y;
            pixels[i * 3 + 2] = (byte) y;
        }
        return result;
    }
}
