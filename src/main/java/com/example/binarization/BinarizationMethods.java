package com.example.binarization;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import java.awt.*;
import java.awt.image.BufferedImage;

public class BinarizationMethods {

    public static Image doBinarization(BufferedImage img, int threshold){
        for (int row = 0; row < img.getWidth(); row++) {
            for (int col = 0; col < img.getHeight(); col++) {
                int val = img.getRGB(row, col);
                int r = (val>>16) & 0xff;
                int g = (val>>8) & 0xff;
                int b = val & 0xff;
                int m = (r + g + b)/3;
                if (m >= threshold) {
                    img.setRGB(row, col, Color.WHITE.getRGB());
                }else{
                    img.setRGB(row, col, Color.BLACK.getRGB());
                }
            }
        }
        return SwingFXUtils.toFXImage(img, null);
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

    private static double getWeight(int[] tab, int pixelCount, int start, int end){
        double sum = 0;
        for (int i = start; i < end; i++) {
            sum += tab[i];
        }
        return sum/pixelCount;
    }

    private static double getMean(int[] tab, int start, int end){
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




    public static int[][] paddingImage(int[][] imageArr, int radius){
        int rows = imageArr.length;
        int col = imageArr[0].length;
        int paddedRows = rows+2*radius;
        int paddedCols = col+2*radius;
        int[][] imageArrPadded = new int[paddedRows][paddedCols];
        //System.out.println("Wiersz = "+rows+" Kolumny = "+col);
        //ystem.out.println("Wiersz = "+paddedRows+" Kolumny = "+paddedCols);
        for (int i = radius; i < rows+radius; i++) {
            for (int j = radius; j < col+radius; j++) {
                imageArrPadded[i][j]=imageArr[i-radius][j-radius];
            }
        }
        return imageArrPadded;
    }

    private static int getMinimum(int[][] block) {
        int minVal = block[0][0];
        for (int i = 0; i < block.length; i++) {
            for (int j = 0; j < block[i].length; j++) {
                if (block[i][j] < minVal) {
                    minVal = block[i][j];
                }
            }
        }
        return minVal;
    }

    private static int getMaximum(int[][] block) {
        int maxVal = block[0][0];
        for (int i = 0; i < block.length; i++) {
            for (int j = 0; j < block[i].length; j++) {
                if (block[i][j] > maxVal) {
                    maxVal = block[i][j];
                }
            }
        }
        return maxVal;
    }



    public static Image doBinarizationBernsen(BufferedImage bImage, int contrastLimit, int radius) {
        int[][] imageGrey = Helpers.greyImageTab(bImage);
        int[][] imgPadded = paddingImage(imageGrey, radius);
        //
        int brSize = 1+2*radius;
        int[][] endTresh = new int[bImage.getHeight()][bImage.getWidth()];

        //System.out.println(imgPadded.length);
        //System.out.println(imgPadded[0].length);
        //display2DArr(imgPadded);
        for (int x = radius; x < imgPadded.length-radius; x++) {
            for (int y = radius; y < imgPadded[0].length-radius; y++) {
                int[][]bracket = new int[brSize][brSize];
                for (int i = 0; i < brSize; i++) {
                    for (int j = 0; j < brSize; j++) {
                        bracket[i][j] = imgPadded[i+x-radius][j+y-radius];
                    }
                }
                //display2DArr(bracket);
                int zLow = getMinimum(bracket);
                int zHigh = getMaximum(bracket);

                int basicTresh= (zLow+zHigh)/2;
                int localContrast = zHigh-zLow;

                if(localContrast<contrastLimit){
                    endTresh[x-radius][y-radius] = 0;
                }else{
                    endTresh[x-radius][y-radius] = basicTresh;
                }

            }
        }
        for (int row = 0; row < bImage.getHeight(); row++) {
            for (int col = 0; col < bImage.getWidth(); col++) {
                int val = bImage.getRGB(col, row);
                int grey = (val>>16) & 0xff;
                if (grey<endTresh[row][col]){
                    bImage.setRGB(col, row, Color.BLACK.getRGB());
                }else {
                    bImage.setRGB(col, row, Color.WHITE.getRGB());
                }
            }
        }

        return SwingFXUtils.toFXImage(bImage, null);
    }

    public static double getMeanFromBracket(int[][] tab){
        double sum = 0;
        for (int i = 0; i < tab.length; i++) {
            for (int j = 0; j < tab[i].length; j++) {
                sum+=tab[i][j];
            }
        }
        return sum/(tab.length * tab.length);
    }


    public static Image doBinarizationNiblack(BufferedImage bImage, int radius) {
        int[][] imageGrey = Helpers.greyImageTab(bImage);
        int[][] imgPadded = paddingImage(imageGrey, radius);
        int brSize = 1+2*radius;
        double k = -0.2;
        int[][] tNiblack = new int[bImage.getHeight()][bImage.getWidth()];
        for (int x = radius; x < imgPadded.length-radius; x++) {
            for (int y = radius; y < imgPadded[0].length-radius; y++) {
                int[][] bracket = new int[brSize][brSize];
                for (int i = 0; i < brSize; i++) {
                    for (int j = 0; j < brSize; j++) {
                        bracket[i][j] = imgPadded[i + x - radius][j + y - radius];
                    }
                }
                double mean = getMeanFromBracket(bracket);
                int[] bracket1dim = get1DimBracket(bracket);
                double stdDev = getStandardDeviation(bracket1dim,mean);
                tNiblack[x-radius][y-radius] = (int) (mean + k*stdDev);
            }
        }
        //display2DArr(tNiblack);
        for (int row = 0; row < bImage.getHeight(); row++) {
            for (int col = 0; col < bImage.getWidth(); col++) {
                int val = bImage.getRGB(col, row);
                int grey = (val>>16) & 0xff;
                if (grey<tNiblack[row][col]){
                    bImage.setRGB(col, row, Color.BLACK.getRGB());
                }else {
                    bImage.setRGB(col, row, Color.WHITE.getRGB());
                }
            }
        }
        Image imageBinarizedNiblack = SwingFXUtils.toFXImage(bImage, null);
        return imageBinarizedNiblack;
    }
    public static double[] getDouble1DimBracket(double[][] bracket) {
        double[] bracket1dim = new double[bracket.length*bracket.length];
        for (int i = 0; i < bracket.length; i++) {
            for (int j = 0; j < bracket[i].length; j++) {
                bracket1dim[i*bracket.length+j] = bracket[i][j];
            }
        }
        return bracket1dim;
    }
    public static int[] get1DimBracket(int[][] bracket) {
        int[] bracket1dim = new int[bracket.length*bracket.length];
        for (int i = 0; i < bracket.length; i++) {
            for (int j = 0; j < bracket[i].length; j++) {
                bracket1dim[i*bracket.length+j] = bracket[i][j];
            }
        }
        return bracket1dim;
    }

    public static double getStandardDeviation(int[] data, double mean){
        double sumOfSquares = 0.0;
        double[] deviation = new double[data.length];
        for (int i = 0; i < deviation.length; i++) {
            deviation[i] = data[i]-mean; //dewiacja od średniej
            deviation[i] *= deviation[i];
            sumOfSquares+=deviation[i];
        }
        double variance = sumOfSquares/(data.length-1);
        double stdDeviation = Math.sqrt(variance);
        return stdDeviation;
    }

    public static void dis1DArray(double[] tab){
        for (int i = 0; i < tab.length; i++) {
            System.out.print(tab[i]+" ");
        }
        System.out.println();
    }

    public static Image doBinarizationSauvola(BufferedImage bImage, int radius) {
        int[][] imageGrey = Helpers.greyImageTab(bImage);
        int[][] imgPadded = paddingImage(imageGrey, radius);
        int brSize = 1+2*radius;
        double k = -0.2;
        double dynRange = 128.0;
        int[][] tSauvola = new int[bImage.getHeight()][bImage.getWidth()];
        for (int x = radius; x < imgPadded.length-radius; x++) {
            for (int y = radius; y < imgPadded[0].length-radius; y++) {
                int[][] bracket = new int[brSize][brSize];
                for (int i = 0; i < brSize; i++) {
                    for (int j = 0; j < brSize; j++) {
                        bracket[i][j] = imgPadded[i + x - radius][j + y - radius];
                    }
                }
                double mean = getMeanFromBracket(bracket);
                int[] bracket1dim = get1DimBracket(bracket);
                double stdDev = getStandardDeviation(bracket1dim,mean);
                tSauvola[x-radius][y-radius] = (int) (mean * (1 - k * (stdDev/dynRange-1)));
            }
        }
        for (int row = 0; row < bImage.getHeight(); row++) {
            for (int col = 0; col < bImage.getWidth(); col++) {
                int val = bImage.getRGB(col, row);
                int grey = (val>>16) & 0xff;
                if (grey<tSauvola[row][col]){
                    bImage.setRGB(col, row, Color.BLACK.getRGB());
                }else {
                    bImage.setRGB(col, row, Color.WHITE.getRGB());
                }
            }
        }
        Image imageBinarizedSauvola= SwingFXUtils.toFXImage(bImage, null);
        return imageBinarizedSauvola;
    }
}
