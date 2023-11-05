package com.example.binarization;

import javafx.geometry.Point2D;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.util.Stack;

public class FloodFiller {
    private final WritableImage img;
    private final Color colorClicked;

    private final double E = 0.5;

    public FloodFiller(WritableImage img, Color colorClicked){
        this.img = img;
        this.colorClicked = colorClicked;
    }

    public void fillImage(Point2D start, Color color){
        PixelReader reader = img.getPixelReader();
        PixelWriter writer = img.getPixelWriter();

        Stack<Point2D> stack = new Stack<>();
        stack.push(start);

        while(!stack.isEmpty()){
            Point2D point = stack.pop();
            int x = (int) point.getX();
            int y = (int) point.getY();
            if (filled(reader, x, y)) {
                continue;
            }

            writer.setColor(x,y,color);

            push(stack, x - 1, y - 1);
            push(stack, x - 1, y    );
            push(stack, x - 1, y + 1);
            push(stack, x    , y + 1);
            push(stack, x + 1, y + 1);
            push(stack, x + 1, y    );
            push(stack, x + 1, y - 1);
            push(stack, x,     y - 1);
        }
    }

    private void push(Stack<Point2D> stack, int x, int y) {
        if (x < 0 || x >= img.getWidth() || y < 0 || y >= img.getHeight()) {
            return;
        }
        stack.push(new Point2D(x, y));
    }

    private boolean filled(PixelReader reader, int x, int y){
        Color c = reader.getColor(x,y);
        return !withinTolerance(c, colorClicked,E);
    }
    private boolean withinTolerance(Color a, Color b, double epsilon) {
        return
                withinTolerance(a.getRed(),   b.getRed(),   epsilon) &&
                        withinTolerance(a.getGreen(), b.getGreen(), epsilon) &&
                        withinTolerance(a.getBlue(),  b.getBlue(),  epsilon);
    }

    private boolean withinTolerance(double a, double b, double epsilon) {
        return Math.abs(a - b) < epsilon;
    }

}
