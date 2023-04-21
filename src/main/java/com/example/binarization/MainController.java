package com.example.binarization;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class MainController implements Initializable {
    @FXML
    public ImageView imgOrg;
    @FXML
    public ImageView imgEdit;
    @FXML
    private TextField binarTreshField;
    public Button histBtn;
    @FXML
    private TextField radiusFld;
    @FXML
    private TextField contrastFld;
    @FXML
    private Button bernsenBtn;

    Image imageOrginal;
    private BufferedImage bufferedImage;
    public Button resetBtn;
    private int width;
    private int height;
    Stage stage;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        binarTreshField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String oldVal, String newVal) {
                if (!newVal.matches("\\d*")) {
                    binarTreshField.setText(newVal.replaceAll("[^\\d]", ""));
                }
            }
        });
        contrastFld.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String oldVal, String newVal) {
                if (!newVal.matches("\\d*")) {
                    contrastFld.setText(newVal.replaceAll("[^\\d]", ""));
                }
            }
        });

        radiusFld.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String oldVal, String newVal) {
                if (!newVal.matches("\\d*")) {
                    radiusFld.setText(newVal.replaceAll("[^\\d]", ""));
                }
            }
        });
    }
    @FXML
    protected void onOpenAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("C:\\Users\\Marcin\\Desktop\\images"));
        fileChooser.setTitle("Open Resource File");
        File file = fileChooser.showOpenDialog(stage);
        try {
            bufferedImage = ImageIO.read(file);
            width = bufferedImage.getWidth();
            height = bufferedImage.getHeight();

            imageOrginal = SwingFXUtils.toFXImage(bufferedImage, null);
            imgOrg.setImage(imageOrginal);
            imgEdit.setImage(imageOrginal);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
    public void onResetClick() {
        imgEdit.setImage(imageOrginal);
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

    public void onBinarizationClick(ActionEvent actionEvent) {
        int threshold = Integer.parseInt(binarTreshField.getText());
        BufferedImage img = SwingFXUtils.fromFXImage(imgOrg.getImage(), null);
        Image imageBinarized = BinarizationMethods.doBinarization(img,threshold);
        imgEdit.setImage(imageBinarized);
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

    public void onOtsuBtn(ActionEvent actionEvent) {
        BufferedImage bImageOtsu = SwingFXUtils.fromFXImage(imgEdit.getImage(), null);
        int threshold = BinarizationMethods.getOtsuThreshold(bImageOtsu);
        BinarizationMethods.doBinarization(bImageOtsu, threshold);
        Image imageBinarizedOtsu = BinarizationMethods.doBinarization(bImageOtsu,threshold);
        imgEdit.setImage(imageBinarizedOtsu);
    }



    public void onBernsenBtn() {
        BufferedImage bImageBernsen = SwingFXUtils.fromFXImage(imgEdit.getImage(),null);
        doGreyScale(bImageBernsen);
        int contrastVal = Integer.parseInt(contrastFld.getText());
        int radiusVal = Integer.parseInt(radiusFld.getText());
        Image imageBinarizedBernsen = BinarizationMethods.doBinarizationBernsen(bImageBernsen,contrastVal,radiusVal);
        imgEdit.setImage(imageBinarizedBernsen);
    }
    public void onNiblackBtn() {
        BufferedImage bImageNiblack = SwingFXUtils.fromFXImage(imgEdit.getImage(),null);
        doGreyScale(bImageNiblack);
        int radiusVal = Integer.parseInt(radiusFld.getText());
        Image imageBinarizedBernsen = BinarizationMethods.doBinarizationNiblack(bImageNiblack,radiusVal);
        imgEdit.setImage(imageBinarizedBernsen);
    }


    public void onSauvolaBtn() {
        BufferedImage bImageSauvola = SwingFXUtils.fromFXImage(imgEdit.getImage(),null);
        doGreyScale(bImageSauvola);
        int radiusVal = Integer.parseInt(radiusFld.getText());
        Image imageBinarizedBernsen = BinarizationMethods.doBinarizationSauvola(bImageSauvola,radiusVal);
        imgEdit.setImage(imageBinarizedBernsen);
    }
}