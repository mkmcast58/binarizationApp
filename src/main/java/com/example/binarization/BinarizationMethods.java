package com.example.binarization;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import java.awt.*;
import java.awt.image.BufferedImage;

public class BinarizationMethods {

    public static Image doBinarization(BufferedImage img, int treshold){
        for (int row = 0; row < img.getWidth(); row++) {
            for (int col = 0; col < img.getHeight(); col++) {
                int val = img.getRGB(row, col);
                int r = (val>>16) & 0xff;
                int g = (val>>8) & 0xff;
                int b = val & 0xff;
                int m = (r + g + b)/3;
                if (m >= treshold) {
                    img.setRGB(row, col, Color.WHITE.getRGB());
                }else{
                    img.setRGB(row, col, Color.BLACK.getRGB());
                }
            }
        }
        Image imageBinarized = SwingFXUtils.toFXImage(img, null);
        return imageBinarized;
    }

    private static int[] arrayOfOccurrence(BufferedImage bImage){
        int[] tab = new int[256];
        for (int row = 0; row < bImage.getWidth(); row++) {
            for (int col = 0; col < bImage.getHeight(); col++) {
                int val = bImage.getRGB(row,col);
                int r = (val>>16) & 0xff;
                int g = (val>>8) & 0xff;
                int b = val & 0xff;
                int m = (r + g + b)/3;
                tab[m]++;
            }
        }
        return tab;
    }

    private static double getWeight(int tab[], int pixelCount, int start,int end){
        double sum = 0;
        for (int i = start; i < end; i++) {
            sum += tab[i];
        }
        return sum/pixelCount;
    }

    private static double getMean(int tab[], int start, int end){
        double max = 0;
        double sum = 0;
        for (int i = start; i < end; i++) {
            sum += tab[i]*i;
            max += tab[i];
        }
        return sum/max;
    }

    public static int getOtsuThreshold(BufferedImage bImage) {
        int[] tab = arrayOfOccurrence(bImage);

        double[] weightBG = new double[256];
        double[] weightFG = new double[256];
        double[] meanBG = new double[256];
        double[] meanFG = new double[256];

        int pixelCount = bImage.getWidth()*bImage.getHeight();

        for (int i = 0; i<256; i++){
            weightBG[i] = getWeight(tab, pixelCount, 0, i);
            weightFG[i] = getWeight(tab,pixelCount,i+1,256);
            meanBG[i] = getMean(tab,0, i);
            meanFG[i] = getMean(tab,i+1,256);
        }

        double max = 0;
        int pos = 0;

        for (int i = 0; i < 256; i++) {
            double war = weightBG[i]*weightFG[i] * (meanBG[i]-meanFG[i]) * (meanBG[i]-meanFG[i]);
            if (war > max){
                max = war;
                pos = i;
            }
        }
        System.out.println(pos);
        return pos;
    }

    public static void display2DArr(int[][] tab){
        for (int row = 0; row < tab.length; row++) {
            for (int col = 0; col < tab[0].length; col++) {
                System.out.print(tab[row][col]+"|");
            }
            System.out.println();
        }
    }

    public static Image doBinarizationBernsen(BufferedImage bImage) {
        System.out.println("test");
        int[][] imageGrey = new int[bImage.getWidth()][bImage.getHeight()];
        System.out.println("Image Width="+imageGrey.length+" | Image Height="+imageGrey[0].length);
        //display2DArr(imageGrey);
        Image imageBinarizedBernsen = SwingFXUtils.toFXImage(bImage, null);
        return imageBinarizedBernsen;
    }
}
