package com.example.binarization;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Histograms {
    Histograms() throws IOException {
        FXMLLoader histloader = new FXMLLoader(getClass().getResource("histogram-view.fxml"));
        Stage histStage = new Stage();
        Scene scene = new Scene(histloader.load());
        histStage.setTitle("Histogramy");
        histStage.setScene(scene);
        histStage.show();
    }
}
