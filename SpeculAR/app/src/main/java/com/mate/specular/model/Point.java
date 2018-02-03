package com.mate.specular.model;


public class Point {
    private Color color;
    private double x_coord;
    private double y_coord;

    /**
     * Creates Point object with given
     *
     * @param color the point has to have a color defined in Color enum.
     * @param x_coord the point has to have coordinates. This is x coordinate of it.
     * @param y_coord the point has to have coordinates. This is y coordinate of it.
     */

    public Point(Color color, double x_coord, double y_coord) {
        this.color = color;
        this.x_coord = x_coord;
        this.y_coord = y_coord;
    }

    /**
     * This won't be used.
     */

    public Point() {
    }

    /**
     *
     * @return color the point has to have a color defined in Color enum.
     */
    public Color getColor() {
        return color;
    }

    /**
     * Sets
     *
     * @param color the point has to have a color defined in Color enum.
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * @return x_coord the point has to have coordinates. This is x coordinate of it.
     */
    public double getX_coord() {
        return x_coord;
    }

    /**
     * Sets
     *
     * @param x_coord the point has to have coordinates. This is x coordinate of it.
     */
    public void setX_coord(double x_coord) {
        this.x_coord = x_coord;
    }

    /**
     * @return y_coord the point has to have coordinates. This is y coordinate of it.
     */
    public double getY_coord() {
        return y_coord;
    }

    /**
     * Sets
     *
     * @param y_coord the point has to have coordinates. This is y coordinate of it.
     */
    public void setY_coord(double y_coord) {
        this.y_coord = y_coord;
    }
}
