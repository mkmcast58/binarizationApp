package com.example.binarization;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.chart.XYChart;
import javafx.scene.image.Image;

import java.awt.image.BufferedImage;

public class ImageHistogram {
    private Image img;

    private int red[] = new int[256];
    private int green[] = new int[256];
    private int blue[] = new int[256];

    private int grey[] = new int[256];

    XYChart.Series seriesRed;
    XYChart.Series seriesGreen;
    XYChart.Series seriesBlue;
    XYChart.Series seriesGrey;



    ImageHistogram(Image src) {
        img = src;
        BufferedImage image = SwingFXUtils.fromFXImage(img, null);

        for (int i = 0; i < 256; i++) {
            red[i] = green[i] = blue[i] = grey[i] = 0;
        }

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int val = image.getRGB(x, y);
                int r = (val>>16) & 0xff;
                int g = (val>>8) & 0xff;
                int b = val & 0xff;
                int m = (r+g+b)/3;
                red[r]++;
                green[g]++;
                blue[b]++;
                grey[m]++;
            }
        }


        seriesRed = new XYChart.Series();
        seriesGreen = new XYChart.Series();
        seriesBlue = new XYChart.Series();
        seriesGrey = new XYChart.Series();
        seriesRed.setName("red");
        seriesGreen.setName("green");
        seriesBlue.setName("blue");
        seriesGrey.setName("grey");



        for (int i = 0; i < 256; i++) {
            seriesRed.getData().add(new XYChart.Data(String.valueOf(i), red[i]));
            seriesGreen.getData().add(new XYChart.Data(String.valueOf(i), green[i]));
            seriesBlue.getData().add(new XYChart.Data(String.valueOf(i), blue[i]));
            seriesGrey.getData().add(new XYChart.Data(String.valueOf(i), grey[i]));
        }
    }



    public XYChart.Series getSeriesRed() {
        return seriesRed;
    }

    public XYChart.Series getSeriesGreen() {
        return seriesGreen;
    }

    public XYChart.Series getSeriesBlue() {
        return seriesBlue;
    }
    public XYChart.Series getSeriesGrey() {return seriesGrey;}
}