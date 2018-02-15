package com.mate.specular.model;


import java.util.ArrayList;
import java.util.List;

public class Frame {
    private List<Object> objects;
    private Circle[] circles = new Circle[4];

    /**
     * Creates Frame object with given
     *
     * @param  objects list of objects that are inside the frame
     * @param  circles array of circles with size of 4 ,
     *                that are located at top-left, top-right, bottom-left, bottom-right of the frame
     * @see         Object
     * @see         Circle
     */
    public Frame(List<Object> objects, Circle[] circles) {
        this.objects = objects;
        this.circles = circles;
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
     * @retun  circles array of circles with size of 4 ,
     *                that are located at top-left, top-right, bottom-left, bottom-right of the frame
     * @see         Circle
     */
    public Circle[] getCircles() {
        return circles;
    }

    /**
     * Sets
     * @param  circles array of circles with size of 4 ,
     *                that are located at top-left, top-right, bottom-left, bottom-right of the frame
     * @see         Circle
     */
    public void setCircles(Circle[] circles) {
        this.circles = circles;
    }
}
