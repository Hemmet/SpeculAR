package com.mate.specular.util;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mate.specular.model.Circle;
import com.mate.specular.model.Frame;
import com.mate.specular.model.Place;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by met on 2/16/2018.
 */

public class FrameFinder {

    public static Frame findFrameWith(Circle[] circles){
        if(circles.length != 4) return null; //the frame have to have exactly 4 points! If not then something's wrong.

        //I assumed that I know which place I am in.

        Map<String, Boolean> frameIDs = null;//curl 'https://specular-bymate.firebaseio.com/places/'+$placeid/frames/'
        List<Frame> frames = new ArrayList<>();

        for(String id : frameIDs.keySet()){
            frames.add(new Frame()//curl 'https://specular-bymate.firebaseio.com/frames/'+id');
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
    }
}
