package com.example.binarization;

import java.awt.image.BufferedImage;

public class Helpers {


    public static void display2DArr(int[][] tab){
        for (int row = 0; row < tab.length; row++) {
            for (int col = 0; col < tab[0].length; col++) {
                //System.out.print("tab["+row+"]["+col+"] = "+tab[row][col]+"|");
                System.out.print(tab[row][col]+"|");
            }
            System.out.println();
        }
    }

    public static int[][] greyImageTab(BufferedImage bImage){
        int[][] imageGreyTab = new int [bImage.getHeight()][bImage.getWidth()];
        for (int row = 0; row < bImage.getHeight(); row++) {
            for (int col = 0; col < bImage.getWidth(); col++) {
                int val = bImage.getRGB(col,row);
                int r = (val>>16) & 0xff;
                int g = (val>>8) & 0xff;
                int b = val & 0xff;
                int m = (r + g + b)/3;
                imageGreyTab[row][col] = m;
            }
        }
        return imageGreyTab;
    }
}
