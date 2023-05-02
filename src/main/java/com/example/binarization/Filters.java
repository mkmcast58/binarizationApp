package com.example.binarization;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;


public class Filters {

    public static int getMedian(int[] array){
        Arrays.sort(array);
        return array[array.length/2];
    }

    public static Image doPixelization(BufferedImage bImage, int pixelSize){
        int width = bImage.getWidth();
        int height = bImage.getHeight();
        BufferedImage pixelizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < height; y += pixelSize) {
            for (int x = 0; x < width; x += pixelSize) {
                int avgRed = 0, avgGreen = 0, avgBlue = 0, count = 0;
                for (int dy = 0; dy < pixelSize && y + dy < height; dy++) {
                    for (int dx = 0; dx < pixelSize && x + dx < width; dx++) {
                        int rgb = bImage.getRGB(x + dx, y + dy);
                        avgRed += (rgb >> 16) & 0xFF;
                        avgGreen += (rgb >> 8) & 0xFF;
                        avgBlue += rgb & 0xFF;
                        count++;
                    }
                }
                avgRed /= count;
                avgGreen /= count;
                avgBlue /= count;
                int pixelColor = (avgRed << 16) | (avgGreen << 8) | avgBlue;
                for (int dy = 0; dy < pixelSize && y + dy < height; dy++) {
                    for (int dx = 0; dx < pixelSize && x + dx < width; dx++) {
                        pixelizedImage.setRGB(x + dx, y + dy, pixelColor);
                    }
                }
            }
        }
        return SwingFXUtils.toFXImage(pixelizedImage, null);
    }
    public static Image doKuwahara(BufferedImage bImage, int radius){
        int[][] redVal = new int[bImage.getHeight()][bImage.getWidth()];
        int[][] greenVal = new int[bImage.getHeight()][bImage.getWidth()];
        int[][] blueVal = new int[bImage.getHeight()][bImage.getWidth()];
        int[][] endRedVal = new int[bImage.getHeight()][bImage.getWidth()];
        int[][] endGreenVal = new int[bImage.getHeight()][bImage.getWidth()];
        int[][] endBlueVal = new int[bImage.getHeight()][bImage.getWidth()];

        int brSize = 2*radius+1;

        for (int row = 0; row < bImage.getHeight(); row++) {
            for (int col = 0; col < bImage.getWidth(); col++) {
                int val = bImage.getRGB(col,row);
                int r = (val>>16) & 0xff;
                int g = (val>>8) & 0xff;
                int b = val & 0xff;
                redVal[row][col] = r;
                greenVal[row][col] = g;
                blueVal[row][col] = b;
            }
        }

        int[][] redValPadd = BinarizationMethods.paddingImage(redVal,radius);
        int[][] greenValPadd = BinarizationMethods.paddingImage(greenVal,radius);
        int[][] blueValPadd = BinarizationMethods.paddingImage(blueVal,radius);

        for (int x = radius; x < redValPadd.length-radius; x++) {
            for (int y = radius; y < redValPadd[0].length - radius; y++) {
                int[][] bracketRed = new int[brSize][brSize];
                int[][] bracketGreen = new int[brSize][brSize];
                int[][] bracketBlue = new int[brSize][brSize];
                for (int i = 0; i < brSize; i++) {
                    for (int j = 0; j < brSize; j++) {
                        bracketRed[i][j] = redValPadd[i + x - radius][j + y - radius];
                        bracketGreen[i][j] = greenValPadd[i + x - radius][j + y - radius];
                        bracketBlue[i][j] = blueValPadd[i + x - radius][j + y - radius];
                    }
                }
                int[][] bracketRedA = new int[radius+1][radius+1];
                int[][] bracketRedB = new int[radius+1][radius+1];
                int[][] bracketRedC = new int[radius+1][radius+1];
                int[][] bracketRedD = new int[radius+1][radius+1];

                int[][] bracketGreenA = new int[radius+1][radius+1];
                int[][] bracketGreenB = new int[radius+1][radius+1];
                int[][] bracketGreenC = new int[radius+1][radius+1];
                int[][] bracketGreenD = new int[radius+1][radius+1];

                int[][] bracketBlueA = new int[radius+1][radius+1];
                int[][] bracketBlueB = new int[radius+1][radius+1];
                int[][] bracketBlueC = new int[radius+1][radius+1];
                int[][] bracketBlueD = new int[radius+1][radius+1];
                for (int i = 0; i < radius+1; i++) {
                    for (int j = 0; j < radius+1; j++) {
                        bracketRedA[i][j] = bracketRed[i][j];
                        bracketGreenA[i][j] = bracketGreen[i][j];
                        bracketBlueA[i][j] = bracketBlue[i][j];

                        bracketRedB[i][j] = bracketRed[i][j+radius];
                        bracketGreenB[i][j] = bracketGreen[i][j+radius];
                        bracketBlueB[i][j] = bracketBlue[i][j+radius];

                        bracketRedC[i][j] = bracketRed[i+radius][j];
                        bracketGreenC[i][j] = bracketGreen[i+radius][j];
                        bracketBlueC[i][j] = bracketBlue[i+radius][j];

                        bracketRedD[i][j] = bracketRed[i+radius][j+radius];
                        bracketGreenD[i][j] = bracketGreen[i+radius][j+radius];
                        bracketBlueD[i][j] = bracketBlue[i+radius][j+radius];
                    }
                }
                double meanRedA = BinarizationMethods.getMeanFromBracket(bracketRedA);
                double meanRedB = BinarizationMethods.getMeanFromBracket(bracketRedB);
                double meanRedC = BinarizationMethods.getMeanFromBracket(bracketRedC);
                double meanRedD = BinarizationMethods.getMeanFromBracket(bracketRedD);

                double meanGreenA = BinarizationMethods.getMeanFromBracket(bracketGreenA);
                double meanGreenB = BinarizationMethods.getMeanFromBracket(bracketGreenB);
                double meanGreenC = BinarizationMethods.getMeanFromBracket(bracketGreenC);
                double meanGreenD = BinarizationMethods.getMeanFromBracket(bracketGreenD);

                double meanBlueA = BinarizationMethods.getMeanFromBracket(bracketBlueA);
                double meanBlueB = BinarizationMethods.getMeanFromBracket(bracketBlueA);
                double meanBlueC = BinarizationMethods.getMeanFromBracket(bracketBlueA);
                double meanBlueD = BinarizationMethods.getMeanFromBracket(bracketBlueA);

                double stdDevRedA = BinarizationMethods.getStandardDeviation(BinarizationMethods.get1DimBracket(bracketRedA),meanRedA);
                double stdDevRedB = BinarizationMethods.getStandardDeviation(BinarizationMethods.get1DimBracket(bracketRedB),meanRedB);
                double stdDevRedC = BinarizationMethods.getStandardDeviation(BinarizationMethods.get1DimBracket(bracketRedC),meanRedC);
                double stdDevRedD = BinarizationMethods.getStandardDeviation(BinarizationMethods.get1DimBracket(bracketRedD),meanRedD);

                double stdDevGreenA = BinarizationMethods.getStandardDeviation(BinarizationMethods.get1DimBracket(bracketGreenA),meanGreenA);
                double stdDevGreenB = BinarizationMethods.getStandardDeviation(BinarizationMethods.get1DimBracket(bracketGreenB),meanGreenB);
                double stdDevGreenC = BinarizationMethods.getStandardDeviation(BinarizationMethods.get1DimBracket(bracketGreenC),meanGreenC);
                double stdDevGreenD = BinarizationMethods.getStandardDeviation(BinarizationMethods.get1DimBracket(bracketGreenD),meanGreenD);

                double stdDevBlueA = BinarizationMethods.getStandardDeviation(BinarizationMethods.get1DimBracket(bracketBlueA),meanBlueA);
                double stdDevBlueB = BinarizationMethods.getStandardDeviation(BinarizationMethods.get1DimBracket(bracketBlueB),meanBlueB);
                double stdDevBlueC = BinarizationMethods.getStandardDeviation(BinarizationMethods.get1DimBracket(bracketBlueC),meanBlueC);
                double stdDevBlueD = BinarizationMethods.getStandardDeviation(BinarizationMethods.get1DimBracket(bracketBlueD),meanBlueD);

                Map<Double, Double> values = new TreeMap<>();

                values.put(stdDevRedA,meanRedA);
                values.put(stdDevRedB,meanRedB);
                values.put(stdDevRedC,meanRedC);
                values.put(stdDevRedD,meanRedD);

                values.put(stdDevGreenA,meanGreenA);
                values.put(stdDevGreenB,meanGreenB);
                values.put(stdDevGreenC,meanGreenC);
                values.put(stdDevGreenD,meanGreenD);

                values.put(stdDevBlueA,meanBlueA);
                values.put(stdDevBlueB,meanBlueB);
                values.put(stdDevBlueC,meanBlueC);
                values.put(stdDevBlueD,meanBlueD);

                double minStdRed = Math.min(Math.min(stdDevRedA,stdDevRedB),Math.min(stdDevRedC,stdDevRedD));
                double minStdGreen = Math.min(Math.min(stdDevGreenA,stdDevGreenB),Math.min(stdDevGreenC,stdDevGreenD));
                double minStdBlue = Math.min(Math.min(stdDevBlueA,stdDevBlueB),Math.min(stdDevBlueC,stdDevBlueD));

               double redValEnd =  values.get(minStdRed);
               double greenValEnd =  values.get(minStdGreen);
               double blueValEnd =  values.get(minStdBlue);

               endRedVal[x-radius][y-radius] = (int)redValEnd;
               endGreenVal[x-radius][y-radius] = (int)greenValEnd;
               endBlueVal[x-radius][y-radius] = (int)blueValEnd;
            }
        }
        for (int row = 0; row < bImage.getHeight(); row++) {
            for (int col = 0; col < bImage.getWidth(); col++) {
                int val = bImage.getRGB(col,row);
                int a = (val>>24) & 0xff;
                val = (a<<24) | (endRedVal[row][col]<<16) | (endGreenVal[row][col]<<8) | endBlueVal[row][col];
                bImage.setRGB(col,row,val);
            }
        }
        return SwingFXUtils.toFXImage(bImage, null);
    }

    public static Image doMedianFilter(BufferedImage bImage, int radius) {
        int[][] redVal = new int[bImage.getHeight()][bImage.getWidth()];
        int[][] greenVal = new int[bImage.getHeight()][bImage.getWidth()];
        int[][] blueVal = new int[bImage.getHeight()][bImage.getWidth()];
        int[][] endRedVal = new int[bImage.getHeight()][bImage.getWidth()];
        int[][] endGreenVal = new int[bImage.getHeight()][bImage.getWidth()];
        int[][] endBlueVal = new int[bImage.getHeight()][bImage.getWidth()];

        int brSize = 2*radius+1;
        for (int row = 0; row < bImage.getHeight(); row++) {
            for (int col = 0; col < bImage.getWidth(); col++) {
                int val = bImage.getRGB(col,row);
                int r = (val>>16) & 0xff;
                int g = (val>>8) & 0xff;
                int b = val & 0xff;
                redVal[row][col] = r;
                greenVal[row][col] = g;
                blueVal[row][col] = b;
            }
        }

        int[][] redValPadd = BinarizationMethods.paddingImage(redVal,radius);
        int[][] greenValPadd = BinarizationMethods.paddingImage(greenVal,radius);
        int[][] blueValPadd = BinarizationMethods.paddingImage(blueVal,radius);

        for (int x = radius; x < redValPadd.length-radius; x++) {
            for (int y = radius; y < redValPadd[0].length-radius; y++) {
                int[][]bracketRed = new int[brSize][brSize];
                int[][]bracketGreen = new int[brSize][brSize];
                int[][]bracketBlue = new int[brSize][brSize];
                for (int i = 0; i < brSize; i++) {
                    for (int j = 0; j < brSize; j++) {
                        bracketRed[i][j] = redValPadd[i + x - radius][j + y - radius];
                        bracketGreen[i][j] = greenValPadd[i + x - radius][j + y - radius];
                        bracketBlue[i][j] = blueValPadd[i + x - radius][j + y - radius];
                    }
                }
                int[] bracketRed1Dim = BinarizationMethods.get1DimBracket(bracketRed);
                int[] bracketGreen1Dim = BinarizationMethods.get1DimBracket(bracketGreen);
                int[] bracketBlue1Dim = BinarizationMethods.get1DimBracket(bracketBlue);

                int medianRed = getMedian(bracketRed1Dim);
                int medianGreen = getMedian(bracketGreen1Dim);
                int medianBlue = getMedian(bracketBlue1Dim);

                endRedVal[x-radius][y-radius] = medianRed;
                endGreenVal[x-radius][y-radius] = medianGreen;
                endBlueVal[x-radius][y-radius] = medianBlue;
            }
        }
        for (int row = 0; row < bImage.getHeight(); row++) {
            for (int col = 0; col < bImage.getWidth(); col++) {
                int val = bImage.getRGB(col,row);
                int a = (val>>24) & 0xff;
                val = (a<<24) | (endRedVal[row][col]<<16) | (endGreenVal[row][col]<<8) | endBlueVal[row][col];
                bImage.setRGB(col,row,val);
            }
        }
        return SwingFXUtils.toFXImage(bImage, null);
    }
}
