package com.example.binarization;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.awt.image.BufferedImage;

public class HistogramController {
    @FXML
    private LineChart histChart;
    @FXML
    private ImageView histImageView;
    private Image image;
    private int histImageWidth;
    private int histImageHeight;
    private int numOfPixels;
    private int red[] = new int[256];
    private int green[] = new int[256];
    private int blue[] = new int[256];
    private double dystrybuantaR[] = new double [256];
    private double dystrybuantaG[] = new double [256];
    private double dystrybuantaB[] = new double [256];


    public void setHistogramView(Image image) {
        this.image = image;
        histImageView.setImage(image);
        histImageWidth = (int) image.getWidth();
        histImageHeight = (int) image.getHeight();
        numOfPixels = histImageWidth*histImageHeight;
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
        ImageHistogram imageHistogram = new ImageHistogram(image);
        histChart.getData().addAll(
                imageHistogram.getSeriesRed(),
                imageHistogram.getSeriesGreen(),
                imageHistogram.getSeriesBlue(),
                imageHistogram.getSeriesGrey());

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int val = bufferedImage.getRGB(x, y);
                int r = (val>>16) & 0xff;
                int g = (val>>8) & 0xff;
                int b = val & 0xff;
                red[r]++;
                green[g]++;
                blue[b]++;
            }
        }
    }

    private double getD0(int tab[]){
        int i = 0;
        double d0 = 0;
        while(tab[i]==0){
            i++;
        }
        d0 = (double)tab[i]/numOfPixels;
        return d0;
    }

    @FXML
    private void equalizeHistogram(){
        System.out.println("Rozmiar = "+ histImageWidth+"x"+histImageHeight+ " Liczba pikseli = "+numOfPixels);
        BufferedImage bImage = SwingFXUtils.fromFXImage(image,null);

        double redD0 = getD0(red);
        double greenD0 = getD0(green);
        double blueD0 = getD0(blue);

        double sumR=0;
        double sumG=0;
        double sumB=0;

        int[] redLUTarray = new int [256];
        int[] greenLUTarray = new int [256];
        int[] blueLUTarray = new int [256];

        for (int i = 0; i < 256; i++) {
            sumR+=red[i];
            sumG+=green[i];
            sumB+=blue[i];
            dystrybuantaR[i]=sumR/numOfPixels;
            dystrybuantaG[i]=sumG/numOfPixels;
            dystrybuantaB[i]=sumB/numOfPixels;
            redLUTarray[i] = (int) Math.round(((dystrybuantaR[i] - redD0)/(1-redD0))*255);
            greenLUTarray[i] = (int) Math.round(((dystrybuantaG[i] - greenD0)/(1-greenD0))*255);
            blueLUTarray[i] = (int) Math.round(((dystrybuantaB[i] - blueD0)/(1-blueD0))*255);
            System.out.println("numOfPixels of value "+i+" = "+red[i]+" dystrybuanta = "+dystrybuantaR[i]+" sum = "+ sumR+" LUT = "+redLUTarray[i]);
        }

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int val = bImage.getRGB(x, y);
                int a = (val >> 24) & 0xff;
                int r = (val >> 16) & 0xff;
                int g = (val >> 8) & 0xff;
                int b = val & 0xff;
                val = (a<<24) | (redLUTarray[r]<<16) | (greenLUTarray[g]<<8) | blueLUTarray[b];
                bImage.setRGB(x,y,val);
            }
        }

        Image img = SwingFXUtils.toFXImage(bImage,null);
        histImageView.setImage(img);

        ImageHistogram imageHistogram = new ImageHistogram(img);
        histChart.getData().clear();
        histChart.getData().addAll(
                imageHistogram.getSeriesRed(),
                imageHistogram.getSeriesGreen(),
                imageHistogram.getSeriesBlue(),
                imageHistogram.getSeriesGrey());
    }

    private double getMinVal(int tab[]){
        double minVal = 0;
        for (int i = 0; i < 256; i++) {
            if (tab[i] != 0)
            {
                minVal = i;
                break;
            }
        }
        return minVal;
    }
    private double getMaxVal(int tab[]){
        double maxVal = 255;
        for (int i = 255; i >0; i--) {
            if (tab[i] != 0)
            {
                maxVal = i;
                break;
            }
        }
        return maxVal;
    }
    @FXML
    private void onStretchHist(ActionEvent actionEvent) {
        BufferedImage bImage = SwingFXUtils.fromFXImage(image,null);

        double minValRed = getMinVal(red);
        double maxValRed = getMaxVal(red);
        double minValGreen = getMinVal(green);
        double maxValGreen = getMaxVal(green);
        double minValBlue = getMinVal(blue);
        double maxValBlue = getMaxVal(blue);

        int[] redLUTarray = new int [256];
        int[] greenLUTarray = new int [256];
        int[] blueLUTarray = new int [256];

        for (int i = 0; i < 256; i++) {
            redLUTarray[i] = (int) Math.round((255.0 *(i-minValRed)/(maxValRed-minValRed)));
            greenLUTarray[i] = (int) Math.round((255.0 *(i-minValGreen)/(maxValGreen-minValGreen)));
            blueLUTarray[i] = (int) Math.round((255.0 *(i-minValBlue)/(maxValBlue-minValBlue)));
        }

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int val = bImage.getRGB(x, y);
                int a = (val >> 24) & 0xff;
                int r = (val >> 16) & 0xff;
                int g = (val >> 8) & 0xff;
                int b = val & 0xff;
                val = (a<<24) | (redLUTarray[r]<<16) | (greenLUTarray[g]<<8) | blueLUTarray[b];
                bImage.setRGB(x,y,val);
            }
        }

        Image img = SwingFXUtils.toFXImage(bImage,null);
        histImageView.setImage(img);

        ImageHistogram imageHistogram = new ImageHistogram(img);
        histChart.getData().clear();
        histChart.getData().addAll(
                imageHistogram.getSeriesRed(),
                imageHistogram.getSeriesGreen(),
                imageHistogram.getSeriesBlue(),
                imageHistogram.getSeriesGrey());
        //System.out.println(minValRed+" "+maxValRed+" "+minValGreen+" "+maxValGreen+" "+minValBlue+" "+maxValBlue);
    }


}
