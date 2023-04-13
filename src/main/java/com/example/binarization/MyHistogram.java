package com.example.binarization;

import javafx.scene.chart.XYChart;

import java.awt.image.BufferedImage;

public class MyHistogram {
    private BufferedImage image;
    int width = (int) image.getWidth();
    int height = (int) image.getHeight();
    private int[] alpha = new int[256];
    private int[] red = new int[256];
    private int[] green = new int[256];
    private int[] blue = new int[256];

    XYChart.Series seriesA;
    XYChart.Series seriesR;
    XYChart.Series seriesG;
    XYChart.Series seriesB;

    private boolean success;

    public MyHistogram(BufferedImage img) {
        image = img;
        success = false;

        for (int i = 0; i < 256; i++) {
            alpha[i] = 0;
            red[i] = 0;
            green[i] = 0;
            blue[i] = 0;
        }

        for (int row = 0; row < width; row++) {
            for (int col = 0; col < height; col++) {
                int val = img.getRGB(row, col);
                int a = (0xff000000 & val) >> 24;
                int r = (0x00ff0000 & val) >> 16;
                int g = (0x0000ff00 & val) >> 8;
                int b = (0x000000ff & val);

                alpha[a]++;
                red[r]++;
                green[g]++;
                blue[b]++;
            }
        }

        seriesA = new XYChart.Series();
        seriesR = new XYChart.Series();
        seriesG = new XYChart.Series();
        seriesB = new XYChart.Series();
        seriesA.setName("alpha");
        seriesR.setName("red");
        seriesG.setName("green");
        seriesB.setName("blue");

        for (int i = 0; i < 256; i++) {
            seriesA.getData().add(new XYChart.Data(String.valueOf(i), alpha[i]));
            seriesR.getData().add(new XYChart.Data(String.valueOf(i), red[i]));
            seriesG.getData().add(new XYChart.Data(String.valueOf(i), green[i]));
            seriesB.getData().add(new XYChart.Data(String.valueOf(i), blue[i]));
        }
        success = true;
    }
    public boolean isSuccess() {
        return success;
    }

    public XYChart.Series getSeriesAlpha() {
        return seriesA;
    }

    public XYChart.Series getSeriesRed() {
        return seriesB;
    }

    public XYChart.Series getSeriesGreen() {
        return seriesG;
    }

    public XYChart.Series getSeriesBlue() {
        return seriesB;
    }
}
