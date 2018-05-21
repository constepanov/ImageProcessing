import chart.ChartBuilder;
import color.ColorConverter;
import filter.*;
import noise.GaussianNoise;
import noise.ImageNoise;
import noise.ImpulseNoise;
import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;

import javax.imageio.ImageIO;
import java.awt.*;
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
        ChartBuilder.gaussianNoiseChart(grayScaleImage);
        ChartBuilder.impulseNoiseChart(grayScaleImage);
        imageProcessingWithGaussianNoise(grayScaleImage);
        imageProcessingWithImpulseNoise(grayScaleImage);
        laplacian(grayScaleImage);
        sobelBruteForce(grayScaleImage);
        int thr = 127;
        SobelFilter sobelFilter = new SobelFilter(thr);
        BufferedImage filtered = sobelFilter.process(grayScaleImage);
        String pathName = String.format("sobel/thr%d.bmp", thr);
        ImageIO.write(filtered, "BMP", new File(pathName));
        double[][] direction = sobelFilter.getDirection();
        BufferedImage colorMap = sobelFilter.getGradientDirectionsMap(direction);
        ImageIO.write(colorMap, "BMP", new File("sobel/colorMap.bmp"));

        BufferedImage lenaLow = ImageIO.read(new File("lenaLow.bmp"));
        BufferedImage lenaLowGradTransform = twoDotGradTransformation(lenaLow);
        ImageIO.write(lenaLowGradTransform, "BMP", new File("highQuality1.bmp"));
        ChartBuilder.imageHistogram(lenaLow, "lenaLow");
        ChartBuilder.imageHistogram(lenaLowGradTransform, "lenaLowGradTransform");

        BufferedImage lenaHigh = ImageIO.read(new File("lenaHigh.bmp"));
        BufferedImage lenaHighGradTransform = twoDotGradTransformation(lenaHigh);
        ImageIO.write(lenaHighGradTransform, "BMP", new File("highQuality2.bmp"));
        ChartBuilder.imageHistogram(lenaHigh, "lenaHigh");
        ChartBuilder.imageHistogram(lenaHighGradTransform, "lenaHighGradTransform");
    }

    private static void sobelBruteForce(BufferedImage image) throws IOException {
        for (int thr = 0; thr <= 255; thr += 5) {
            SobelFilter sobelFilter = new SobelFilter(thr);
            BufferedImage filtered = sobelFilter.process(image);
            String pathName = String.format("sobel/thr%d.bmp", thr);
            ImageIO.write(filtered, "BMP", new File(pathName));
        }
    }

    private static BufferedImage twoDotGradTransformation(BufferedImage image) {
        double[] a = {0, 65, 180, 255};
        double[] b = {0, 40, 210, 255};
        LinearInterpolator interpolator = new LinearInterpolator();
        PolynomialSplineFunction f = interpolator.interpolate(a, b);
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage result = new BufferedImage(width, height, image.getType());
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double color = f.value(new Color(image.getRGB(x, y)).getRed());
                Color resColor = new Color(clip(color, 0, 255),
                        clip(color, 0, 255), clip(color, 0, 255));
                result.setRGB(x, y, resColor.getRGB());
            }
        }
        return result;
    }

    private static void laplacian(BufferedImage image) throws IOException {
        final String outputPath = System.getProperty("user.dir") +
                File.separator + "laplacian" + File.separator;
        new File(outputPath).mkdir();
        System.out.println("LAPLACE OPERATOR");
        LaplacianFilter filter = new LaplacianFilter(0);
        BufferedImage filtered = filter.process(image);
        BufferedImage clipped1 = filter.clip(filtered, image, false);
        BufferedImage clipped2 = filter.clip(filtered, image, true);
        ImageIO.write(clipped1, "bmp", new File(outputPath + "clipped1.bmp"));
        ImageIO.write(clipped2, "bmp", new File(outputPath + "clipped2.bmp"));
        for (double alpha = 1; alpha <= 1.6; alpha += 0.1) {
            System.out.printf("α = %.1f\n", alpha);
            LaplacianFilter laplacianFilter = new LaplacianFilter(alpha);
            filtered = laplacianFilter.process(image);
            String fileName = String.format(outputPath + "highBoost(α = %.1f).bmp", alpha);
            ImageIO.write(filtered, "bmp", new File(fileName));
            System.out.printf("Average luma = %f\n", averageLuma(filtered));
            fileName = String.format("histogram(α = %.1f)", alpha);
            ChartBuilder.imageHistogram(filtered, fileName);
        }
        ChartBuilder.imageHistogram(image, "grayScaleImageHistogram");
    }

    private static void imageProcessingWithGaussianNoise(BufferedImage image) throws IOException {
        final String outputPath = System.getProperty("user.dir") +
                File.separator + "gaussianNoise" + File.separator;
        new File(outputPath).mkdir();
        System.out.println("IMAGE PROCESSING WITH ADDITIVE NOISE");

        double sigma = 10;
        ImageNoise gaussianNoise = new GaussianNoise(sigma);
        BufferedImage imageWithGaussianNoise = gaussianNoise.processImage(image);
        double psnr = calculatePSNR(image, imageWithGaussianNoise);
        System.out.printf("PSNR for noisy image: %f\n", psnr);
        String fileName = String.format("noisyImage(%.0f).bmp", sigma);
        ImageIO.write(imageWithGaussianNoise, "bmp",
                new File(outputPath + fileName));

        AveragingFilter averagingFilter = new AveragingFilter(2);
        BufferedImage filtered = averagingFilter.process(imageWithGaussianNoise);
        psnr = calculatePSNR(image, filtered);
        System.out.printf("PSNR after averaging filter: %f\n", psnr);
        ImageIO.write(filtered, "bmp",
                new File(outputPath + "averagingFilter.bmp"));

        GaussianBlurFilter gaussianBlurFilter = new GaussianBlurFilter(2.5, 8);
        filtered = gaussianBlurFilter.process(imageWithGaussianNoise);
        psnr = calculatePSNR(image, filtered);
        System.out.printf("PSNR after gaussian filter: %f\n", psnr);
        ImageIO.write(filtered, "bmp",
                new File(outputPath + "gaussianFilter.bmp"));

        MedianFilter medianFilter = new MedianFilter(2);
        filtered = medianFilter.process(imageWithGaussianNoise);
        psnr = calculatePSNR(image, filtered);
        System.out.printf("PSNR after median filter: %f\n", psnr);
        ImageIO.write(filtered, "bmp",
                new File(outputPath + "medianFilter.bmp"));

        ChartBuilder.averagingFilterChart(image, imageWithGaussianNoise);
        ChartBuilder.medianFilterChart(image, imageWithGaussianNoise);
        ChartBuilder.gaussianFilterChart(image, imageWithGaussianNoise);
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
        ChartBuilder.medianFilterChart(image, noisyImages, percentages);
    }
}
