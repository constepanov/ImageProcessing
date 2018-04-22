package chart;

import filter.GaussianBlurFilter;
import filter.MedianFilter;
import noise.AdditiveNoise;
import noise.ImpulseNoise;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.style.Styler;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static color.Util.calculatePSNR;

public class ChartBuilder {
    private static final String outputFolder = "charts" + File.separator;

    static {
        new File(System.getProperty("user.dir") +
                File.separator + outputFolder).mkdir();
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
            BitmapEncoder.saveBitmap(chart, outputFolder + "additiveNoise", BitmapEncoder.BitmapFormat.PNG);
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
            BitmapEncoder.saveBitmap(chart, outputFolder + "impulseNoise", BitmapEncoder.BitmapFormat.PNG);
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
            BitmapEncoder.saveBitmap(chart, outputFolder + "gaussianFilter", BitmapEncoder.BitmapFormat.PNG);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void buildChartForMedianFilter(BufferedImage srcImage, BufferedImage[] noisyImages, int[] percentages) {
        XYChart chart = new XYChartBuilder()
                .width(800)
                .height(600)
                .theme(Styler.ChartTheme.Matlab)
                .xAxisTitle("R")
                .yAxisTitle("PSNR")
                .build();
        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNE);
        chart.getStyler().setHasAnnotations(false);
        for (int i = 0; i < noisyImages.length; i++) {
            List<Double> radii = new ArrayList<>();
            List<Double> psnrValues = new ArrayList<>();
            for (int r = 1; r < 10; r++) {
                MedianFilter medianFilter = new MedianFilter(r);
                BufferedImage filtered = medianFilter.process(noisyImages[i]);
                double psnr = calculatePSNR(srcImage, filtered);
                psnrValues.add(psnr);
                radii.add((double) r);
            }
            chart.addSeries(percentages[i] + "%", radii, psnrValues);
        }
        try {
            BitmapEncoder.saveBitmap(chart, outputFolder + "medianFilter", BitmapEncoder.BitmapFormat.PNG);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
