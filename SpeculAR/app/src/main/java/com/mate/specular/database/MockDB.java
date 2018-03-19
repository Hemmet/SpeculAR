package com.mate.specular.database;

import com.mate.specular.model.Circle;
import com.mate.specular.model.Color;
import com.mate.specular.model.Frame;
import com.mate.specular.model.ObjectModel;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class MockDB {
    public static Map<String, String> infoTags = new HashMap<>();
    public static List<Frame> frames = new LinkedList<>();

    public static void initializeMockDB(){
        initializeFrames();
    }
    private static void initializeFrames(){
        createInfoTags();
        Circle[] circles = createCircles();
        List<ObjectModel> objects = createObjects();
        frames.add(new Frame(objects,circles));
    }
    private static List<ObjectModel> createObjects(){
        List<ObjectModel> objects = new LinkedList<>();
        objects.add(new ObjectModel("pencil",infoTags.get("pencil"),100,100));

        return objects;
    }
    private static Circle[] createCircles(){
        Circle[] circle = new Circle[4];
        circle[0] = new Circle(Color.GOLD,0,0);
        circle[1] = new Circle(Color.BLUE,0,250);
        circle[2] = new Circle(Color.GREEN,-50,0);
        circle[3] = new Circle(Color.RED,-50,250);

        return circle;
    }
    private static void createInfoTags(){
        infoTags.put("pencil", "A pencil is a writing implement or art medium constructed of a narrow, solid pigment core inside a protective casing which prevents the core from being broken and/or from leaving marks on the user’s hand during use.");
        infoTags.put("eraser", "An eraser is an article of stationery that is used for removing writing from paper or skin. Erasers have a rubbery consistency and come in a variety of shapes, sizes and colours.");
        infoTags.put("pencil_sharpener", "A pencil sharpener is a device for sharpening a pencil's writing point by shaving away its worn surface.");
    }
}
