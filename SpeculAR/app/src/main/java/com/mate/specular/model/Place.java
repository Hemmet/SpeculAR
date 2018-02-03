package com.mate.specular.model;

import java.util.ArrayList;
import java.util.List;


public class Place {
    private String name;
    private List<Frame> frames;

    /**
     * Creates Place object with given
     *
     * @param  name unique string identifier for the place
     * @param  frames list of frames that the place has
     * @see         Frame
     */

    public Place(String name, List<Frame> frames) {
        this.name = name;
        this.frames = frames;
    }

    /**
     * Creates Place object with given name. This might not be used in the project however it could be useful
     * when to create a place with an empty list of frames
     *
     * @param  name unique string identifier for the place
     */

    public Place(String name) {
        this.name = name;
        this.frames = new ArrayList<>();
    }

    /**
     * Creates Place object with default values. This won't be used in anytime.
     *
     */

    public Place() {
        this.name = "";
        this.frames = new ArrayList<>();
    }

    /**
     * @return the name of the place
     */

    public String getName() {
        return name;
    }

    /**
     * Sets a name to the place.
     *
     * @param  name unique string identifier for the place. Uniqueness is controlled in the database
     */

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the list of frames that the place has
     * @see         Frame
     */

    public List<Frame> getFrames() {
        return frames;
    }

    /**
     * Sets a list of frames to the place
     *
     * @param  frames list of frames that the place has
     * @see         Frame
     */

    public void setFrames(List<Frame> frames) {
        this.frames = frames;
    }
}
