package com.mate.specular.model;


public class Object {
    private String title;
    private String info;
    private double x_coord;
    private double y_coord;


    /**
     * Creates Object object with given
     *
     * @param  title name of the object
     * @param  info information that object has, not need to be defined.
     * @param x_coord the object has to have coordinates. This is x coordinate of it.
     * @param y_coord the object has to have coordinates. This is y coordinate of it.
     */
    public Object(String title, String info, double x_coord, double y_coord) {
        this.title = title;
        this.info = info;
        this.x_coord = x_coord;
        this.y_coord = y_coord;
    }

    /**
     * Creates Object object with given
     *
     * @param  title name of the object
     * @param x_coord the object has to have coordinates. This is x coordinate of it.
     * @param y_coord the object has to have coordinates. This is y coordinate of it.
     */
    public Object(String title, double x_coord, double y_coord) {
        this.title = title;
        this.info = "";
        this.x_coord = x_coord;
        this.y_coord = y_coord;
    }

    /**
     * Creates Object object with given
     *
     * @param x_coord the object has to have coordinates. This is x coordinate of it.
     * @param y_coord the object has to have coordinates. This is y coordinate of it.
     */
    public Object(double x_coord, double y_coord) {
        this.title = "";
        this.info = "";
        this.x_coord = x_coord;
        this.y_coord = y_coord;
    }
    /**
     * This won't be used.
     */
    public Object() {
        this.x_coord = 0;
        this.y_coord = 0;
        this.info = "";
        this.title = "";
    }

    /**
     * @return title of the object
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets
     *
     * @param title name of the object
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return information that object has.
     */
    public String getInfo() {
        return info;
    }

    /**
     * Sets
     * @param info information that object has.
     */
    public void setInfo(String info) {
        this.info = info;
    }

    /**
     * @return x_coord the object has to have coordinates. This is x coordinate of it.
     */
    public double getX_coord() {
        return x_coord;
    }

    /**
     * Sets
     *
     * @param x_coord the object has to have coordinates. This is x coordinate of it.
     */
    public void setX_coord(double x_coord) {
        this.x_coord = x_coord;
    }

    /**
     * @return y_coord the object has to have coordinates. This is y coordinate of it.
     */
    public double getY_coord() {
        return y_coord;
    }

    /**
     * Sets
     *
     * @param y_coord the object has to have coordinates. This is y coordinate of it.
     */
    public void setY_coord(double y_coord) {
        this.y_coord = y_coord;
    }
}
