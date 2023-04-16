package com.example.binarization;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


public class MainController {
    @FXML
    public ImageView imgOrg;
    @FXML
    public ImageView imgEdit;
    public TextField binarTreshField;
    public Button histBtn;

    private BufferedImage bufferedImage;

    public Button resetBtn;
    private int width;
    private int height;
    Stage stage;
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    @FXML
    protected void onOpenAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("C:\\Users\\Marcin\\Desktop\\images"));
        fileChooser.setTitle("Open Resource File");
        File file = fileChooser.showOpenDialog(stage);
        try {
            bufferedImage = ImageIO.read(file);
            Image image = SwingFXUtils.toFXImage(bufferedImage, null);
            width = bufferedImage.getWidth();
            height = bufferedImage.getHeight();
//            System.out.println("Rozmiar = "+ width+"x"+height+ " Liczba pikseli = "+width*height);
            imgOrg.setImage(image);
            imgEdit.setImage(image);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
    public void onResetClick() {
        Image image = SwingFXUtils.toFXImage(bufferedImage, null);
        imgEdit.setImage(image);
    }

    public void doGreyScale(BufferedImage img) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int val = img.getRGB(x,y);
                int a = (val>>24) & 0xff;
                int r = (val>>16) & 0xff;
                int g = (val>>8) & 0xff;
                int b = val & 0xff;
                int avg = (r+g+b)/3;
                val = (a<<24) | (avg<<16) | (avg<<8) | avg;
                img.setRGB(x,y,val);
            }
        }
        Image image = SwingFXUtils.toFXImage(img, null);
        imgEdit.setImage(image);
    }

    public void doBinarization(BufferedImage img, int treshold){

        for (int row = 0; row < width; row++) {
            for (int col = 0; col < height; col++) {
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
        Image image = SwingFXUtils.toFXImage(img, null);
        imgEdit.setImage(image);
    }

    public void onBinarizationClick(ActionEvent actionEvent) {
        //imgEdit.setImage(imgOrg.getImage());
        int treshold = Integer.parseInt(binarTreshField.getText());
        //System.out.println(treshold);
        BufferedImage img = SwingFXUtils.fromFXImage(imgOrg.getImage(), null);
        doBinarization(img,treshold);

    }



    public void onHistogramClick(ActionEvent actionEvent) throws IOException {
        //new Histograms();
        //lineChartRed.setCreateSymbols(false);
//        Parent root = FXMLLoader.load(getClass().getResource("histogram-view.fxml"));
        FXMLLoader loader = new FXMLLoader(getClass().getResource("histogram-view.fxml"));
        Parent root = loader.load();
        ((HistogramController)loader.getController()).setHistogramView(imgOrg.getImage());
        Stage histStage = new Stage();
        histStage.setTitle("Histogram");
        histStage.setScene(new Scene(root));
        histStage.show();
//        lineChartExample.getData().clear();
//
//        ImageHistogram imageHistogram = new ImageHistogram(imgOrg.getImage());
//        if(imageHistogram.isSuccess()){
//            lineChartExample.getData().addAll(
//                    //imageHistogram.getSeriesAlpha(),
//                    imageHistogram.getSeriesRed(),
//                    imageHistogram.getSeriesGreen(),
//                    imageHistogram.getSeriesBlue());
//        }
    }


    public void onGreyScaleClick(ActionEvent actionEvent) {
        BufferedImage img = SwingFXUtils.fromFXImage(imgEdit.getImage(), null);
        doGreyScale(img);
    }

    private int[] arrayOfOccurence(BufferedImage bImage){
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



    private int getOtsuTreshold() {
        int[] tab = arrayOfOccurence(bufferedImage);


        double[] weightBG = new double[256];
        double[] weightFG = new double[256];
        double[] meanBG = new double[256];
        double[] meanFG = new double[256];

        int pixelCount = bufferedImage.getWidth()*bufferedImage.getHeight();

        for (int i =0; i<256; i++){
            weightBG[i] = getWeight(tab, pixelCount, 0, i);
            weightFG[i] = getWeight(tab,pixelCount,i+1,256);
            meanBG[i] = getMean(tab,0,i);
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

    public void onOtsuBtn(ActionEvent actionEvent) {
        int treshold = getOtsuTreshold();
        doBinarization(bufferedImage, treshold);
    }
}