package com.example.binarization;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.chart.XYChart;
import javafx.scene.image.Image;

import java.awt.image.BufferedImage;

public class ImageHistogram {
    private Image img;

    private long alpha[] = new long[256];
    private long red[] = new long[256];
    private long green[] = new long[256];
    private long blue[] = new long[256];

    XYChart.Series seriesAlpha;
    XYChart.Series seriesRed;
    XYChart.Series seriesGreen;
    XYChart.Series seriesBlue;

    private boolean success;

    ImageHistogram(Image src) {
        img = src;
        BufferedImage image = SwingFXUtils.fromFXImage(img, null);
        success = false;

        //init
        for (int i = 0; i < 256; i++) {
            alpha[i] = red[i] = green[i] = blue[i] = 0;
        }

//        PixelReader pixelReader = image.getPixelReader();
//        if (pixelReader == null) {
//            return;
//        }

        //count pixels
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int val = image.getRGB(x, y);
                //int a = (0xff000000 & val) >> 24;
                int r = (0x00ff0000 & val) >> 16;
                int g = (0x0000ff00 & val) >> 8;
                int b = (0x000000ff & val);

                //alpha[a]++;
                red[r]++;
                green[g]++;
                blue[b]++;

            }
        }

        //seriesAlpha = new XYChart.Series();
        seriesRed = new XYChart.Series();
        seriesGreen = new XYChart.Series();
        seriesBlue = new XYChart.Series();
        //seriesAlpha.setName("alpha");
        seriesRed.setName("red");
        seriesGreen.setName("green");
        seriesBlue.setName("blue");

        //copy alpha[], red[], green[], blue[]
        //to seriesAlpha, seriesRed, seriesGreen, seriesBlue
        for (int i = 0; i < 256; i++) {
            //seriesAlpha.getData().add(new XYChart.Data(String.valueOf(i), alpha[i]));
            seriesRed.getData().add(new XYChart.Data(String.valueOf(i), red[i]));
            seriesGreen.getData().add(new XYChart.Data(String.valueOf(i), green[i]));
            seriesBlue.getData().add(new XYChart.Data(String.valueOf(i), blue[i]));
        }

        success = true;
    }

    public boolean isSuccess() {
        return success;
    }

    public XYChart.Series getSeriesAlpha() {
        return seriesAlpha;
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
}

