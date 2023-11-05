package com.example.binarization;

import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Point2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.awt.image.BufferedImage;

public class SegmentationController {
    private Image image;
    public TextField pencilSize;
    public CheckBox eraser;
    public ColorPicker colorPicker;
    public Canvas canvas;

    boolean[][] visited;



    //private GraphicsContext g;


    public void setSegmentationView(Image image){
        this.image = image;
        canvas.setWidth(image.getWidth());
        canvas.setHeight(image.getHeight());
        GraphicsContext g = canvas.getGraphicsContext2D();
        g.drawImage(image,0,0);
        visited = new boolean[(int) image.getHeight()][(int) image.getWidth()];
    }


    public void onFill() {
        GraphicsContext g = canvas.getGraphicsContext2D();
        Image imageOrginal = image;
        WritableImage wImage = new WritableImage(imageOrginal.getPixelReader(),
                (int) imageOrginal.getWidth(),
                (int) imageOrginal.getHeight()
        );
        canvas.setOnMouseClicked(mouseEvent -> {
            double mouseX = mouseEvent.getX();
            double mouseY = mouseEvent.getY();
            int x = (int) mouseX;
            int y = (int) mouseY;
            BufferedImage bImage = SwingFXUtils.fromFXImage(image,null);
            java.awt.Color c = new java.awt.Color(bImage.getRGB((int)mouseX,(int)mouseY));
            int val = bImage.getRGB((int)mouseX,(int)mouseY);
            int r = (val>>16) & 0xff;
            int green = (val>>8) & 0xff;
            int b = val & 0xff;

            PixelReader pr = image.getPixelReader();
            Color color = pr.getColor(x,y);

            //System.out.println("Pixel color at (" + mouseX + ", " + mouseY + "): val: "+bImage.getRGB((int)mouseX,(int)mouseY)+"| red: "+r+" green: "+g+"blue: "+b );
            System.out.println("Pixel color at (" + mouseX + ", " + mouseY + "): val: "+color+"| red: "+r+" green: "+green+"blue: "+b );


            Color targetColor = colorPicker.getValue();
            FloodFiller filler = new FloodFiller(wImage, color);
            filler.fillImage(new Point2D(mouseX,mouseY), targetColor);
            GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
            graphicsContext.drawImage(wImage,0,0);
            //System.out.println("Pixel color at (" + mouseX + ", " + mouseY + "): " + color);
        });
        //g = canvas.getGraphicsContext2D();
        g.drawImage(wImage,0,0);
    }

    public void onPencil() {
        GraphicsContext g = canvas.getGraphicsContext2D();
        canvas.setOnMouseDragged(e->{
            double size = Double.parseDouble(pencilSize.getText());

            double x = e.getX() - size/2;
            double y = e.getY() - size/2;

            if(eraser.isSelected()){
                g.clearRect(x,y,size,size);
            }else{
                g.setFill(colorPicker.getValue());
                g.fillRect(x,y,size,size);
            }
        });
    }

    private void floodFill(PixelReader pr, PixelWriter pw, int x, int y, Color targetColor, Color replacementColor){
        if (x < 0 || x >= image.getWidth() || y < 0 || y >= image.getHeight()) {
            return;
        }

        Color currentColor = pr.getColor(x, y);
        if (!currentColor.equals(targetColor)) {
            return;
        }

        pw.setColor(x, y, replacementColor);

        floodFill(pr, pw, x - 1, y, targetColor, replacementColor); // Fill left neighbor
        floodFill(pr, pw, x + 1, y, targetColor, replacementColor); // Fill right neighbor
        floodFill(pr, pw, x, y - 1, targetColor, replacementColor); // Fill top neighbor
        floodFill(pr, pw, x, y + 1, targetColor, replacementColor); // Fill bottom neighbor
    }

    public void onTestBtn() {
        canvas.setOnMouseClicked(mouseEvent -> {
            Color targetColor = colorPicker.getValue();
            Image image = canvas.snapshot(new SnapshotParameters(),null);
            BufferedImage bImage = SwingFXUtils.fromFXImage(image,null);
            WritableImage wImage = new WritableImage((int) image.getHeight(), (int) image.getWidth());
            PixelWriter pw = wImage.getPixelWriter();
            double mouseX = mouseEvent.getX();
            double mouseY = mouseEvent.getY();
            int x = (int) mouseX;
            int y = (int) mouseY;
            PixelReader pr = image.getPixelReader();
            Color color = pr.getColor(x,y);
            System.out.println("Pixel color at (" + mouseX + ", " + mouseY + "): val: "+color+"target: "+targetColor);
            flooder(pr, pw, x,y,color, targetColor);
            visited = new boolean[(int) image.getHeight()][(int) image.getWidth()];

        });
    }

    private void flooder(PixelReader pr, PixelWriter pw, int x, int y, Color color, Color targetColor){
        if (x < 0 || x >= image.getWidth() || y < 0 || y >= image.getHeight() || visited[y][x]) {
            return;
        }
        visited[y][x]=true;
        pw.setColor(x,y,targetColor);
        flooder(pr, pw, x-1,y,color, targetColor);
        flooder(pr, pw, x+1,y,color, targetColor);
        flooder(pr, pw, x,y-1,color, targetColor);
        flooder(pr, pw, x,y+1,color, targetColor);
    }


}
