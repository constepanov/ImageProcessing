import color.ColorConverter;
import filter.AveragingFilter;
import filter.GaussianBlurFilter;
import noise.AdditiveNoise;
import noise.ImageNoise;
import noise.ImpulseNoise;
import javax.imageio.ImageIO;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;

import static color.Util.buildChartForAdditiveNoise;
import static color.Util.buildChartForImpulseNoise;
import static color.Util.calculatePSNR;

public class Main {

    public static void main(String[] args) throws IOException {
        File input = new File("lena.bmp");
        BufferedImage inputImage = ImageIO.read(input);
        inputImage = ColorConverter.fromRGBToYYY(inputImage);
        ImageIO.write(inputImage, "bmp", new File("lenaY.bmp"));

        ImageNoise additiveNoise = new AdditiveNoise(10);
        BufferedImage imageWithAdditiveNoise = additiveNoise.processImage(inputImage);
        double psnr = calculatePSNR(inputImage, imageWithAdditiveNoise);
        System.out.printf("PSNR for noisy image: %f\n", psnr);
        ImageIO.write(imageWithAdditiveNoise, "bmp", new File("lenaAN.bmp"));

        AveragingFilter averagingFilter = new AveragingFilter(6);
        BufferedImage filtered = averagingFilter.process(imageWithAdditiveNoise);
        psnr = calculatePSNR(inputImage, filtered);
        System.out.printf("PSNR after averaging filter: %f\n", psnr);
        ImageIO.write(filtered, "bmp", new File("lenaAveragingFilter.bmp"));

        GaussianBlurFilter gaussianBlurFilter = new GaussianBlurFilter(10, 3);
        filtered = gaussianBlurFilter.process(imageWithAdditiveNoise);
        psnr = calculatePSNR(inputImage, filtered);
        System.out.printf("PSNR after gaussian filter: %f\n", psnr);
        ImageIO.write(filtered, "bmp", new File("lenaGaussianFilter.bmp"));

        ImageNoise impulseNoise = new ImpulseNoise(0.1, 0.1);
        BufferedImage imageWithImpulseNoise = impulseNoise.processImage(inputImage);
        ImageIO.write(imageWithImpulseNoise, "bmp", new File("lenaIN.bmp"));

        buildChartForAdditiveNoise(inputImage);
        buildChartForImpulseNoise(inputImage);
    }
}
