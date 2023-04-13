package com.example.binarization;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
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
    public LineChart lineChartRed;
    public NumberAxis yAxisRed;
    public CategoryAxis xAxisRed;

    private BufferedImage bufferedImage;

    public LineChart lineChartExample;
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
        fileChooser.setInitialDirectory(new File("C:\\Users\\Marcin\\Desktop"));
        fileChooser.setTitle("Open Resource File");
        File file = fileChooser.showOpenDialog(stage);
        try {
            bufferedImage = ImageIO.read(file);
            Image image = SwingFXUtils.toFXImage(bufferedImage, null);
            width = bufferedImage.getWidth();
            height = bufferedImage.getHeight();
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

        lineChartExample.getData().clear();

        ImageHistogram imageHistogram = new ImageHistogram(imgOrg.getImage());
        if(imageHistogram.isSuccess()){
            lineChartExample.getData().addAll(
                    //imageHistogram.getSeriesAlpha(),
                    imageHistogram.getSeriesRed(),
                    imageHistogram.getSeriesGreen(),
                    imageHistogram.getSeriesBlue());
        }
    }


    public void onGreyScaleClick(ActionEvent actionEvent) {
        BufferedImage img = SwingFXUtils.fromFXImage(imgEdit.getImage(), null);
        doGreyScale(img);
    }
}