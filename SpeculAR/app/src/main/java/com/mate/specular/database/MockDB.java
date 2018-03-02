package com.mate.specular.database;

import com.mate.specular.model.Frame;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class MockDB {
    public static Map<String, String> infoTags = new HashMap<>();
    public static List<Frame> frames = new LinkedList<>();

    public static void inializeMockDB(){
        initializeFrames();
    }
    public static void initializeFrames(){

    }
    private static void createInfoTags(){
        infoTags.put("pencil", "A pencil is a writing implement or art medium constructed of a narrow, solid pigment core inside a protective casing which prevents the core from being broken and/or from leaving marks on the user’s hand during use.");
        infoTags.put("eraser", "An eraser is an article of stationery that is used for removing writing from paper or skin. Erasers have a rubbery consistency and come in a variety of shapes, sizes and colours.");
        infoTags.put("pencil_sharpener", "A pencil sharpener is a device for sharpening a pencil's writing point by shaving away its worn surface.");
    }
}
