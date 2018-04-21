package color;

import filter.GaussianBlurFilter;
import noise.AdditiveNoise;
import noise.ImpulseNoise;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.style.Styler;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    public static void buildChartForAdditiveNoise(BufferedImage input) {
        XYChart chart = new XYChartBuilder()
                .width(800)
                .height(600)
                .theme(Styler.ChartTheme.Matlab)
                .xAxisTitle("σ")
                .yAxisTitle("PSNR")
                .build();
        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNE);
        chart.getStyler().setHasAnnotations(false);
        List<Double> sigma = new ArrayList<>();
        List<Double> psnr = new ArrayList<>();
        for (int i = 1; i < 80; i += 2) {
            AdditiveNoise noise = new AdditiveNoise(i);
            BufferedImage imageWithNoise = noise.processImage(input);
            sigma.add((double) i);
            psnr.add(calculatePSNR(input, imageWithNoise));
        }
        chart.addSeries("1",sigma, psnr);
        chart.getStyler().setLegendVisible(false);
        try {
            BitmapEncoder.saveBitmap(chart, "additiveNoise", BitmapEncoder.BitmapFormat.PNG);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void buildChartForImpulseNoise(BufferedImage input) {
        XYChart chart = new XYChartBuilder()
                .width(800)
                .height(600)
                .theme(Styler.ChartTheme.Matlab)
                .xAxisTitle("pa")
                .yAxisTitle("PSNR")
                .build();
        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNE);
        chart.getStyler().setHasAnnotations(false);
        for (double pa = 0.1; pa <= 0.5; pa += 0.1) {
            List<Double> probability = new ArrayList<>();
            List<Double> psnr = new ArrayList<>();
            for (double pb = 0.1; pb <= 0.5; pb += 0.1) {
                ImpulseNoise noise = new ImpulseNoise(pa, pb);
                BufferedImage imageWithNoise = noise.processImage(input);
                probability.add(pb);
                psnr.add(calculatePSNR(input, imageWithNoise));
            }
            chart.addSeries(String.format("pa = %.1f", pa), probability, psnr);
        }
        try {
            BitmapEncoder.saveBitmap(chart, "impulseNoise", BitmapEncoder.BitmapFormat.PNG);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void buildChartForGaussianFilter(BufferedImage srcImage, BufferedImage noisyImage) {
        XYChart chart = new XYChartBuilder()
                .width(800)
                .height(600)
                .theme(Styler.ChartTheme.Matlab)
                .xAxisTitle("σ")
                .yAxisTitle("PSNR")
                .build();
        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNE);
        chart.getStyler().setHasAnnotations(false);
        int[] radii = {2, 3, 4, 5};
        for (int r : radii) {
            List<Double> sigmaValues = new ArrayList<>();
            List<Double> psnrValues = new ArrayList<>();
            for (int s = 1; s < 16; s++) {
                GaussianBlurFilter gaussianBlurFilter = new GaussianBlurFilter(s, r);
                BufferedImage filtered = gaussianBlurFilter.process(noisyImage);
                double psnr = calculatePSNR(srcImage, filtered);
                psnrValues.add(psnr);
                sigmaValues.add((double) s);
            }
            chart.addSeries("R = " + r, sigmaValues, psnrValues);
        }
        try {
            BitmapEncoder.saveBitmap(chart, "gaussianFilter", BitmapEncoder.BitmapFormat.PNG);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
