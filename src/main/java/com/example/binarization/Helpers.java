package com.example.binarization;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import java.awt.*;
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
    public static float[] rgbToHsv(int r, int g, int b) {
        float rf = r / 255f;
        float gf = g / 255f;
        float bf = b / 255f;
        float max = Math.max(Math.max(rf, gf), bf);
        float min = Math.min(Math.min(rf, gf), bf);
        float delta = max - min;
        float h, s, v;
        if (delta == 0) {
            h = 0;
        } else if (max == rf) {
            h = ((gf - bf) / delta) % 6;
        } else if (max == gf) {
            h = (bf - rf) / delta + 2;
        } else {
            h = (rf - gf) / delta + 4;
        }
        h *= 60;
        if (h < 0) {
            h += 360;
        }
        if (max == 0) {
            s = 0;
        } else {
            s = delta / max;
        }
        v = max;
        return new float[] {h, s, v};
    }

    public double getMeanFrom2DArr(int[][] arr){
        double mean = 0.0;
        int count =0;
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                mean+=arr[i][j];
                count++;
            }
        }
        return mean/count;
    }

    public static Image convertRgbToHsv2(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int rgb = image.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                float[] hsv = rgbToHsv(r, g, b);
                int h = (int) (hsv[0] / 360 * 255);
                int s = (int) (hsv[1] * 255);
                int v = (int) (hsv[2] * 255);
                result.setRGB(x, y, (h << 16) | (s << 8) | v);
            }
        }
        return SwingFXUtils.toFXImage(result, null);
    }

    public static Image convertRgbToHsv(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        float[] hsv = new float[3];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int rgb = image.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                Color.RGBtoHSB(r, g, b, hsv);
                int h = (int)(hsv[0] * 255);
                int s = (int)(hsv[1] * 255);
                int v = (int)(hsv[2] * 255);
                result.setRGB(x, y, (h << 16) | (s << 8) | v);
            }
        }
        return SwingFXUtils.toFXImage(result, null);
    }

    public static Image convertToHSV(BufferedImage bImage){
        for (int x = 0; x < bImage.getWidth(); x++) {
            for (int y = 0; y < bImage.getHeight(); y++) {
                int val = bImage.getRGB(x, y);
                int a = (val>>24) & 0xff;
                int r = (val>>16) & 0xff;
                int g = (val>>8) & 0xff;
                int b = val & 0xff;
                double rH = r/255.0;
                double gH = g/255.0;
                double bH = b/255.0;
                double cMax = Math.max(rH, Math.max(gH,bH));
                double cMin = Math.min(rH, Math.max(gH,bH));
                double diff = cMax - cMin;
                double hue = -1, sat =-1;
                if (cMax == cMin){
                    hue = 0;
                } else if (cMax == rH) {
                    hue = (60 * ((gH - bH) / diff) + 360) % 360;
                } else if (cMax == gH) {
                    hue = (60 * ((bH - rH) / diff) + 120) % 360;
                } else if (cMax == bH) {
                    hue = (60 * ((rH - gH) / diff) + 240) % 360;
                }
                if(cMax==0){
                    sat=0;
                }else {
                    sat = diff/cMax;
                }
                double value = cMax*100;
                int hueInt = (int) hue;
                int satInt = (int) sat;
                int valueInt = (int) value;
                val = (a<<24) | (hueInt<<16) | (satInt<<8) | valueInt;
                bImage.setRGB(x,y,val);
            }
        }
        return SwingFXUtils.toFXImage(bImage, null);
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
