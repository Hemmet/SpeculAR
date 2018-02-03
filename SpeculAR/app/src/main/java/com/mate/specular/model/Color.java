package com.mate.specular.model;


public enum Color {
    RED(255, 0, 0),
    GREEN(0, 128, 0),
    PURPLE(102, 0, 102),
    BLUE (0, 0, 128),
    MAGENTA(255, 0, 255),
    GOLD(255, 215, 0),
    BLACK(25, 25, 25),
    COFFEE(84, 49, 52);

    private final int r;
    private final int g;
    private final int b;

    /*private*/ Color(final int r,final int g,final int b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public String getColor(){
        return r+","+g+","+b;
    }
}
