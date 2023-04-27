package com.example.binarization;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import java.awt.image.BufferedImage;
import java.util.Arrays;

public class Filters {

    public static int getMedian(int[] array){
        Arrays.sort(array);
        return array[array.length/2];
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
        int[][] greenValPadd = BinarizationMethods.paddingImage(redVal,radius);
        int[][] blueValPadd = BinarizationMethods.paddingImage(redVal,radius);

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
                val = (a<<24) | (endRedVal[col][row]<<16) | (endGreenVal[col][row]<<8) | endBlueVal[col][row];
                bImage.setRGB(row,col,val);
            }
        }
        return SwingFXUtils.toFXImage(bImage, null);
    }
}
