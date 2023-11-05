package com.example.binarization;

public class Pixels {
    private int red;
    private int green;
    private int blue;
    private boolean visited;

    public Pixels(int red, int green, int blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        visited = false;
    }

    public int getRed() {
        return red;
    }

    public void setRed(int red) {
        this.red = red;
    }

    public int getGreen() {
        return green;
    }

    public void setGreen(int green) {
        this.green = green;
    }

    public int getBlue() {
        return blue;
    }

    public void setBlue(int blue) {
        this.blue = blue;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited() {
        visited = true;
    }

    @Override
    public String toString() {
        return "Pixels{" +
                "red=" + red +
                ", green=" + green +
                ", blue=" + blue +
                '}';
    }
}
