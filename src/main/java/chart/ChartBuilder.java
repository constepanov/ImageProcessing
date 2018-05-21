package chart;

import filter.AveragingFilter;
import filter.GaussianBlurFilter;
import filter.MedianFilter;
import noise.GaussianNoise;
import noise.ImpulseNoise;
import org.knowm.xchart.*;
import org.knowm.xchart.style.Styler;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static color.Util.calculatePSNR;

public class ChartBuilder {
    private static final String outputFolder = "charts" + File.separator;

    static {
        new File(System.getProperty("user.dir") +
                File.separator + outputFolder).mkdir();
    }

    public static void gaussianNoiseChart(BufferedImage input) {
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
            GaussianNoise noise = new GaussianNoise(i);
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

    public static void impulseNoiseChart(BufferedImage input) {
        XYChart chart = new XYChartBuilder()
                .width(800)
                .height(600)
                .theme(Styler.ChartTheme.Matlab)
                .xAxisTitle("pb")
                .yAxisTitle("PSNR")
                .build();
        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNE);
        chart.getStyler().setHasAnnotations(false);
        for (double pa = 0.00001; pa <= 0.5; pa += 0.1) {
            List<Double> probability = new ArrayList<>();
            List<Double> psnr = new ArrayList<>();
            for (double pb = 0; pb <= 0.5; pb += 0.1) {
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

    public static void averagingFilterChart(BufferedImage srcImage, BufferedImage noisyImage) {
        XYChart chart = new XYChartBuilder()
                .width(800)
                .height(600)
                .theme(Styler.ChartTheme.Matlab)
                .xAxisTitle("R")
                .yAxisTitle("PSNR")
                .build();
        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNE);
        chart.getStyler().setHasAnnotations(false);
        chart.getStyler().setLegendVisible(false);
        List<Integer> radii = new ArrayList<>();
        List<Double> psnrValues = new ArrayList<>();
        for (int r = 1; r < 10; r++) {
            AveragingFilter filter = new AveragingFilter(r);
            BufferedImage filtered = filter.process(noisyImage);
            double psnr = calculatePSNR(srcImage, filtered);
            radii.add(r);
            psnrValues.add(psnr);
        }
        chart.addSeries("filter", radii, psnrValues);
        try {
            BitmapEncoder.saveBitmap(chart, outputFolder + "averagingFilter", BitmapEncoder.BitmapFormat.PNG);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void medianFilterChart(BufferedImage srcImage, BufferedImage noisyImage) {
        XYChart chart = new XYChartBuilder()
                .width(800)
                .height(600)
                .theme(Styler.ChartTheme.Matlab)
                .xAxisTitle("R")
                .yAxisTitle("PSNR")
                .build();
        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNE);
        chart.getStyler().setHasAnnotations(false);
        chart.getStyler().setLegendVisible(false);
        List<Integer> radii = new ArrayList<>();
        List<Double> psnrValues = new ArrayList<>();
        for (int r = 1; r < 10; r++) {
            MedianFilter filter = new MedianFilter(r);
            BufferedImage filtered = filter.process(noisyImage);
            double psnr = calculatePSNR(srcImage, filtered);
            radii.add(r);
            psnrValues.add(psnr);
        }
        chart.addSeries("filter", radii, psnrValues);
        try {
            BitmapEncoder.saveBitmap(chart, outputFolder + "medianFilterGaussNoise", BitmapEncoder.BitmapFormat.PNG);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void gaussianFilterChart(BufferedImage srcImage, BufferedImage noisyImage) {
        XYChart chart = new XYChartBuilder()
                .width(800)
                .height(600)
                .theme(Styler.ChartTheme.Matlab)
                .xAxisTitle("σ")
                .yAxisTitle("PSNR")
                .build();
        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNE);
        chart.getStyler().setHasAnnotations(false);
        int[] radii = {1, 2, 3, 4, 5, 6, 7, 8};
        for (int r : radii) {
            List<Double> sigmaValues = new ArrayList<>();
            List<Double> psnrValues = new ArrayList<>();
            for (double s = 0.5; s <= 3; s += 0.5) {
                GaussianBlurFilter gaussianBlurFilter = new GaussianBlurFilter(s, r);
                BufferedImage filtered = gaussianBlurFilter.process(noisyImage);
                double psnr = calculatePSNR(srcImage, filtered);
                psnrValues.add(psnr);
                sigmaValues.add(s);
            }
            chart.addSeries("R = " + r, sigmaValues, psnrValues);
        }
        try {
            BitmapEncoder.saveBitmap(chart, outputFolder + "gaussianFilter", BitmapEncoder.BitmapFormat.PNG);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void medianFilterChart(BufferedImage srcImage, BufferedImage[] noisyImages, int[] percentages) {
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

    private static Map<Integer, Integer> getFrequency(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        Map<Integer, Integer> result = new TreeMap<>();
        byte[] data = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        for (int i = 0; i < width * height; i++) {
            int value = Byte.toUnsignedInt(data[i * 3]);
            if (result.containsKey(value)) {
                result.put(value, result.get(value) + 1);
            } else {
                result.put(value, 1);
            }
        }
        return result;
    }

    public static void imageHistogram(BufferedImage image, String fileName) {
        Map<Integer, Integer> frequency = getFrequency(image);
        CategoryChart chart = new CategoryChartBuilder()
                .width(800)
                .height(600)
                .theme(Styler.ChartTheme.Matlab)
                .build();
        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNE);
        chart.getStyler().setHasAnnotations(false);
        chart.getStyler().setPlotGridLinesVisible(false);
        chart.getStyler().setLegendVisible(false);
        List<Integer> x = new ArrayList<>(frequency.keySet());
        List<Integer> y = new ArrayList<>(frequency.values());
        chart.addSeries("1", x, y);
        Map<Object, Object> customCategoryLabels = new TreeMap<>();
        for (int i = 0; i < x.size(); i += 50) {
            customCategoryLabels.put(x.get(i), x.get(i));
        }
        chart.setCustomCategoryLabels(customCategoryLabels);
        try {
            BitmapEncoder.saveBitmap(chart, outputFolder + fileName, BitmapEncoder.BitmapFormat.PNG);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
