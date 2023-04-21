package com.example.binarization;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

import java.util.Optional;

public class Test {
    Dialog<Pair<String,String>> dialog = new Dialog<>();
    private int contrastVal;
    private int radiusVal;
    public static void main(String[] args) {
        for (int i = 0; i < 0; i++) {
            System.out.println(i);
        }
    }

    private void dialogFail(){
        dialog.setTitle("Wprowadź dane");

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        Label label1 = new Label("Limit kontrastu");
        Label label2 = new Label("Rozmiar");
        TextField contrastField = new TextField();
        TextField radiusField = new TextField();
        contrastField.setText("15");
        radiusField.setText("15");
        gridPane.add(label1,0,0);
        gridPane.add(label2,0,1);
        gridPane.add(contrastField,1,0);
        gridPane.add(radiusField,1,1);

        dialog.getDialogPane().setContent(gridPane);
        ButtonType confirmBtnType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmBtnType, ButtonType.CANCEL);

        contrastField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String oldVal, String newVal) {
                if (!newVal.matches("\\d*")) {
                    contrastField.setText(newVal.replaceAll("[^\\d]", ""));
                }
            }
        });

        radiusField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String oldVal, String newVal) {
                if (!newVal.matches("\\d*")) {
                    radiusField.setText(newVal.replaceAll("[^\\d]", ""));
                }
            }
        });

//        Node confirmButton = dialog.getDialogPane().lookupButton(confirmBtnType);
//        confirmButton.setDisable(true);

        dialog.setResultConverter(dialogBtn->{
            if(dialogBtn==confirmBtnType){
                return new Pair<>(contrastField.getText(), radiusField.getText());
            }
            return null;
        });
        Optional<Pair<String, String>> result = dialog.showAndWait();

        result.ifPresent(data->{
            contrastVal = Integer.parseInt(data.getKey());
            radiusVal = Integer.parseInt(data.getValue());
        });

        System.out.println("Kontrast="+contrastVal+" | Radius="+radiusVal);
    }

}
