package com.mate.specular.model;


import java.util.ArrayList;
import java.util.List;

public class Frame {
    private List<Object> objects;
    private Point[] points = new Point[4];

    /**
     * Creates Frame object with given
     *
     * @param  objects list of objects that are inside the frame
     * @param  points array of points with size of 4 ,
     *                that are located at top-left, top-right, bottom-left, bottom-right of the frame
     * @see         Object
     * @see         Point
     */
    public Frame(List<Object> objects, Point[] points) {
        this.objects = objects;
        this.points = points;
    }

    /**
     * This won't be used. It is implemented for that the data type has default constructor
     */
    public Frame() {
        this.objects = new ArrayList<>();
    }

    /**
     * @return list of objects that are inside the frame
     * @see         Object
     */
    public List<Object> getObjects() {
        return objects;
    }
    /**
     * Sets
     *
     * @param  objects list of objects that are inside the frame
     * @see         Object
     */
    public void setObjects(List<Object> objects) {
        this.objects = objects;
    }

    /**
     * @retun  points array of points with size of 4 ,
     *                that are located at top-left, top-right, bottom-left, bottom-right of the frame
     * @see         Point
     */
    public Point[] getPoints() {
        return points;
    }

    /**
     * Sets
     * @param  points array of points with size of 4 ,
     *                that are located at top-left, top-right, bottom-left, bottom-right of the frame
     * @see         Point
     */
    public void setPoints(Point[] points) {
        this.points = points;
    }
}
