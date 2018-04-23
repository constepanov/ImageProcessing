import chart.ChartBuilder;
import color.ColorConverter;
import filter.*;
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
        BufferedImage grayScaleImage = ColorConverter.fromRGBToYYY(image);
        ImageIO.write(grayScaleImage, "bmp", new File("lenaGrayScale.bmp"));
        ChartBuilder.buildChartForAdditiveNoise(grayScaleImage);
        ChartBuilder.buildChartForImpulseNoise(grayScaleImage);
        imageProcessingWithGaussianNoise(grayScaleImage);
        imageProcessingWithImpulseNoise(grayScaleImage);
        laplacian(grayScaleImage);
    }

    private static void laplacian(BufferedImage image) throws IOException {
        final String outputPath = System.getProperty("user.dir") +
                File.separator + "laplacian" + File.separator;
        System.out.println("LAPLACE OPERATOR");
        for (double alpha = 1; alpha <= 1.6; alpha += 0.1) {
            System.out.printf("α = %.1f\n", alpha);
            LaplacianFilter laplacianFilter = new LaplacianFilter(ConvolutionMask.highBoostMask(alpha));
            BufferedImage filtered = laplacianFilter.process(image);
            String fileName = String.format(outputPath + "highBoost(α = %.1f).bmp", alpha);
            ImageIO.write(filtered, "bmp", new File(fileName));
            System.out.printf("Average luma = %f\n", averageLuma(filtered));
            ChartBuilder.histogramImage(filtered, alpha);
        }
        ChartBuilder.histogramImage(image, 0);
    }

    private static void imageProcessingWithGaussianNoise(BufferedImage image) throws IOException {
        final String outputPath = System.getProperty("user.dir") +
                File.separator + "gaussianNoise" + File.separator;
        new File(outputPath).mkdir();
        System.out.println("IMAGE PROCESSING WITH ADDITIVE NOISE");

        ImageNoise additiveNoise = new AdditiveNoise(5);
        BufferedImage imageWithAdditiveNoise = additiveNoise.processImage(image);
        double psnr = calculatePSNR(image, imageWithAdditiveNoise);
        System.out.printf("PSNR for noisy image: %f\n", psnr);
        ImageIO.write(imageWithAdditiveNoise, "bmp",
                new File(outputPath + "noisyImage.bmp"));

        AveragingFilter averagingFilter = new AveragingFilter(3);
        BufferedImage filtered = averagingFilter.process(imageWithAdditiveNoise);
        psnr = calculatePSNR(image, filtered);
        System.out.printf("PSNR after averaging filter: %f\n", psnr);
        ImageIO.write(filtered, "bmp",
                new File(outputPath + "averagingFilter.bmp"));

        GaussianBlurFilter gaussianBlurFilter = new GaussianBlurFilter(2, 3);
        filtered = gaussianBlurFilter.process(imageWithAdditiveNoise);
        psnr = calculatePSNR(image, filtered);
        System.out.printf("PSNR after gaussian filter: %f\n", psnr);
        ImageIO.write(filtered, "bmp",
                new File(outputPath + "gaussianFilter.bmp"));

        MedianFilter medianFilter = new MedianFilter(4);
        filtered = medianFilter.process(imageWithAdditiveNoise);
        psnr = calculatePSNR(image, filtered);
        System.out.printf("PSNR after median filter: %f\n", psnr);
        ImageIO.write(filtered, "bmp",
                new File(outputPath + "medianFilter.bmp"));
        //ChartBuilder.buildChartForGaussianFilter(image, imageWithAdditiveNoise);
    }

    private static void imageProcessingWithImpulseNoise(BufferedImage image) throws IOException {
        final String outputPath = System.getProperty("user.dir") +
                File.separator + "impulseNoise" + File.separator;
        new File(outputPath).mkdir();
        System.out.println("IMAGE PROCESSING WITH IMPULSE NOISE");

        double[] pa = {0.025, 0.05, 0.125, 0.25};
        double[] pb = {0.025, 0.05, 0.125, 0.25};
        int[] radii = {1, 2, 2, 3};

        BufferedImage[] noisyImages = new BufferedImage[4];
        int[] percentages = new int[4];

        for (int i = 0; i < pa.length; i++) {
            System.out.printf("pa = %f, pb = %f\n", pa[i], pb[i]);
            ImageNoise impulseNoise = new ImpulseNoise(pa[i], pb[i]);
            BufferedImage imageWithImpulseNoise = impulseNoise.processImage(image);
            noisyImages[i] = imageWithImpulseNoise;
            double psnr = calculatePSNR(image, imageWithImpulseNoise);
            System.out.printf("PSNR for noisy image: %f\n", psnr);
            int percent = (int) ((pa[i] + pb[i]) * 100);
            percentages[i] = percent;
            ImageIO.write(imageWithImpulseNoise, "bmp",
                    new File(outputPath + String.format("noisyImage(%d).bmp", percent)));

            MedianFilter medianFilter = new MedianFilter(radii[i]);
            BufferedImage filtered = medianFilter.process(imageWithImpulseNoise);
            psnr = calculatePSNR(image, filtered);
            System.out.printf("PSNR after median filter: %f\n", psnr);
            ImageIO.write(filtered, "bmp",
                    new File(outputPath +
                            String.format("medianFilter(%d, R = %d).bmp", percent, radii[i])));
        }
        //ChartBuilder.buildChartForMedianFilter(image, noisyImages, percentages);
    }
}
