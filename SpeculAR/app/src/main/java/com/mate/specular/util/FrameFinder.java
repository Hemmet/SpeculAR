package com.mate.specular.util;

import com.mate.specular.database.MockDB;
import com.mate.specular.model.Circle;
import com.mate.specular.model.Color;
import com.mate.specular.model.Frame;

import java.util.List;

/**
 * Created by met on 2/16/2018.
 */

public class FrameFinder {

    public static Frame findFrameWith(List<Color> colorOrder){
        /*
        if(circles.length != 4) return null; //the frame have to have exactly 4 points! If not then something's wrong.

        //I assumed that I know which place I am in.

        Map<String, Boolean> frameIDs = null;//curl 'https://specular-bymate.firebaseio.com/places/'+$placeid/frames/'
        List<Frame> frames = new ArrayList<>();

        for(String id : frameIDs.keySet()){
            frames.add(new Frame());//curl 'https://specular-bymate.firebaseio.com/frames/'+id');
        }

        for (Frame frame: frames) {
            int match = 0;
            for(int i = 0; i < 4; i++){
                if(frame.getCircles()[i].getX_coord() == circles[i].getX_coord()
                        && frame.getCircles()[i].getY_coord() == circles[i].getY_coord()
                        && frame.getCircles()[i].getRadius() == circles[i].getRadius()
                        && frame.getCircles()[i].getColor() == circles[i].getColor()){
                    match++;
                }else {
                    break;
                }
            }
            if(match == 4){
                return frame;
            }
        }
        return null;
        */
        if(colorOrder == null || colorOrder.size() != 4) return null; //the frame have to have exactly 4 points! If not then something's wrong.

        //I assumed that I know which place I am in.

        for (Frame frame: MockDB.frames) {
            Circle[] circles = frame.getCircles();
            if(colorOrder.get(0).equals(circles[0].getColor()) &&
                    colorOrder.get(1).equals(circles[1].getColor()) &&
                    colorOrder.get(2).equals(circles[2].getColor()) &&
                    colorOrder.get(3).equals(circles[3].getColor())) return frame;
        }
        return null;
    }
}
