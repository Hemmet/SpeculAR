package com.mate.specular.model;


public class Circle {
    private Color color;
    private int x_coord;
    private int y_coord;
    private int radius;

    /**
     * Creates Circle object with given
     *
     * @param color the point has to have a color defined in Color enum.
     * @param x_coord the point has to have coordinates. This is x coordinate of it.
     * @param y_coord the point has to have coordinates. This is y coordinate of it.
     */

    public Circle(Color color, int x_coord, int y_coord) {
        this.color = color;
        this.x_coord = x_coord;
        this.y_coord = y_coord;
        this.radius = 0;
    }

    /**
     *
     * */

    public Circle(Color color, int x_coord, int y_coord, int radius) {
        this.color = color;
        this.x_coord = x_coord;
        this.y_coord = y_coord;
        this.radius = radius;
    }

    /**
     *
     * */

    public Circle(int x_coord, int y_coord, int radius) {
        this.color = Color.BLACK;
        this.x_coord = x_coord;
        this.y_coord = y_coord;
        this.radius = radius;
    }

    /**
     * This won't be used.
     */

    public Circle() {
        this.color = Color.BLACK;
        this.x_coord = 0;
        this.y_coord = 0;
        this.radius = 0;
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
    public int getX_coord() {
        return x_coord;
    }

    /**
     * Sets
     *
     * @param x_coord the point has to have coordinates. This is x coordinate of it.
     */
    public void setX_coord(int x_coord) {
        this.x_coord = x_coord;
    }

    /**
     * @return y_coord the point has to have coordinates. This is y coordinate of it.
     */
    public int getY_coord() {
        return y_coord;
    }

    /**
     * Sets
     *
     * @param y_coord the point has to have coordinates. This is y coordinate of it.
     */
    public void setY_coord(int y_coord) {
        this.y_coord = y_coord;
    }

    /**
     *
     * */

    public int getRadius() {
        return radius;
    }

    /**
     *
     * */

    public void setRadius(int radius) {
        this.radius = radius;
    }
}
