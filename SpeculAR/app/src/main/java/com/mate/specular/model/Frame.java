package com.mate.specular.model;


import java.util.ArrayList;
import java.util.List;

public class Frame {
    private List<ObjectModel> objects;
    private Circle[] circles = new Circle[4];

    /**
     * Creates Frame object with given
     *
     * @param  objects list of objects that are inside the frame
     * @param  circles array of circles with size of 4 ,
     *                that are located at top-left, top-right, bottom-left, bottom-right of the frame
     * @see         ObjectModel
     * @see         Circle
     */
    public Frame(List<ObjectModel> objects, Circle[] circles) {
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
     * @see         ObjectModel
     */
    public List<ObjectModel> getObjects() {
        return objects;
    }
    /**
     * Sets
     *
     * @param  objects list of objects that are inside the frame
     * @see         ObjectModel
     */
    public void setObjects(List<ObjectModel> objects) {
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
