import color.ColorConverter;
import filter.AveragingFilter;
import filter.GaussianBlurFilter;
import filter.MedianFilter;
import noise.AdditiveNoise;
import noise.ImageNoise;
import noise.ImpulseNoise;
import javax.imageio.ImageIO;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;

import static color.Util.*;

public class Main {

    public static void main(String[] args) throws IOException {
        File input = new File("lena.bmp");
        BufferedImage image = ImageIO.read(input);
        image = ColorConverter.fromRGBToYYY(image);
        ImageIO.write(image, "bmp", new File("lenaY.bmp"));
        //imageProcessingWithGaussianNoise(image);
        imageProcessingWithImpulseNoise(image);

        //buildChartForAdditiveNoise(inputImage);
        //buildChartForImpulseNoise(inputImage);
    }

    private static void imageProcessingWithGaussianNoise(BufferedImage image) throws IOException {
        System.out.println("IMAGE PROCESSING WITH ADDITIVE NOISE");

        ImageNoise additiveNoise = new AdditiveNoise(7);
        BufferedImage imageWithAdditiveNoise = additiveNoise.processImage(image);
        double psnr = calculatePSNR(image, imageWithAdditiveNoise);
        System.out.printf("PSNR for noisy image: %f\n", psnr);
        ImageIO.write(imageWithAdditiveNoise, "bmp", new File("lenaAN.bmp"));

        AveragingFilter averagingFilter = new AveragingFilter(10);
        BufferedImage filtered = averagingFilter.process(imageWithAdditiveNoise);
        psnr = calculatePSNR(image, filtered);
        System.out.printf("PSNR after averaging filter: %f\n", psnr);
        ImageIO.write(filtered, "bmp", new File("lenaAveragingFilter.bmp"));

        GaussianBlurFilter gaussianBlurFilter = new GaussianBlurFilter(2, 3);
        filtered = gaussianBlurFilter.process(imageWithAdditiveNoise);
        psnr = calculatePSNR(image, filtered);
        System.out.printf("PSNR after gaussian filter: %f\n", psnr);
        ImageIO.write(filtered, "bmp", new File("lenaGaussianFilter.bmp"));

        MedianFilter medianFilter = new MedianFilter(5);
        filtered = medianFilter.process(imageWithAdditiveNoise);
        psnr = calculatePSNR(image, filtered);
        System.out.printf("PSNR after median filter: %f\n", psnr);
        ImageIO.write(filtered, "bmp", new File("lenaMedianFilter.bmp"));
        //buildChartForGaussianFilter(image, imageWithAdditiveNoise);
    }

    private static void imageProcessingWithImpulseNoise(BufferedImage image) throws IOException {
        System.out.println("IMAGE PROCESSING WITH IMPULSE NOISE");

        ImageNoise impulseNoise = new ImpulseNoise(0.25, 0.25);
        BufferedImage imageWithImpulseNoise = impulseNoise.processImage(image);
        double psnr = calculatePSNR(image, imageWithImpulseNoise);
        System.out.printf("PSNR for noisy image: %f\n", psnr);
        ImageIO.write(imageWithImpulseNoise, "bmp", new File("lenaIN.bmp"));

        MedianFilter medianFilter = new MedianFilter(4);
        BufferedImage filtered = medianFilter.process(imageWithImpulseNoise);
        psnr = calculatePSNR(image, filtered);
        System.out.printf("PSNR after median filter: %f\n", psnr);
        ImageIO.write(filtered, "bmp", new File("lenaMedian.bmp"));
    }
}
