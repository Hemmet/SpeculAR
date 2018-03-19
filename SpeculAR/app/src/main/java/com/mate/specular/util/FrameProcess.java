package com.mate.specular.util;

import android.util.Log;
import com.mate.specular.model.Circle;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ETS on 1.03.2018.
 */

public class FrameProcess {
    public static Map<String, List<Integer>> colorHueCodes = new HashMap<String, List<Integer>>();
    public static final String TAG = "FrameProcess.java";
    public static Circle[] currentCircles = new Circle[4];
    int scaleFactor = 2;

    public FrameProcess(){
        initializColorHashMap(colorHueCodes);
    }

    private static Mat shiftChannel(Mat H, int shift, int maxVal){
        Log.i(TAG, "shiftChannel");
        Mat shiftedH = H.clone();
        for (int j = 0; j < shiftedH.rows(); ++j)
            for (int i = 0; i < shiftedH.cols(); ++i){
                shiftedH.put(j, i,(shiftedH.get(j,i)[0] + shift) % maxVal);
            }
        return shiftedH;
    }

    public Mat thresholdHue(Mat hsvImage, int hueValMin, int hueValMax, int minSat, int minValue){
        Mat mask = new Mat();
        Core.inRange(hsvImage, new Scalar(hueValMin, minSat, minValue), new Scalar(hueValMax, 255, 255), mask);
        return mask;
    }

    public static List<Circle> hsvColorCircleDetect(Mat mask, Mat image){
        List<Circle> circles = new ArrayList<Circle>();
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(mask, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
        for(int i = 0; i < contours.size(); i++){
            Circle temp = new Circle(0,0,0);
            float[] radius = new float[1];
            Point center = new Point();
            Imgproc.minEnclosingCircle(new MatOfPoint2f(contours.get(i).toArray()), center, radius);
            if(Math.round(radius[0]) > 5) {
                temp.setX_coord((int) center.x);
                temp.setY_coord((int) center.y);
                temp.setRadius(Math.round(radius[0]));
                circles.add(temp);
            }
        }
        return circles;
    }

    public Map<String, Circle> detectColor(Mat originalImage){

        Map<String, Circle> circleCoordinates = new HashMap<String, Circle>();
        Mat hsvImage = new Mat();
        Mat image = new Mat();
        Imgproc.pyrDown(originalImage, image);
        Imgproc.GaussianBlur(image, image, new Size(3,3), 2, 2);
        Imgproc.cvtColor(image, hsvImage, Imgproc.COLOR_RGB2HSV);
        Mat mask =  new Mat();
        List<Circle> houghCircleList = houghTransformCircle(image);

        for(Map.Entry<String, List<Integer>> hValue : colorHueCodes.entrySet()){
            String colorCode = hValue.getKey();
            List<Integer> thresholdValues = hValue.getValue();
            mask = thresholdHue(hsvImage, thresholdValues.get(0), thresholdValues.get(1), thresholdValues.get(2), thresholdValues.get(3));
            List<Circle> hsvCircles = hsvColorCircleDetect(mask, image);
            //Sample down orani artirilirsa asagidaki hesaplamalar kontrol edilmelidir.
            Circle tempC = new Circle(0,0,0);
            tempC = findMatchCircle(hsvCircles, houghCircleList);
            Point center = new Point(tempC.getX_coord(), tempC.getY_coord());
            center.x *= scaleFactor;
            center.y *= scaleFactor;
            tempC.setX_coord((int) center.x);
            tempC.setY_coord((int) center.y);
            tempC.setRadius(tempC.getRadius() * scaleFactor);
            circleCoordinates.put(colorCode, tempC);
        }
        return circleCoordinates;
    }
    private void drawCircle (Circle c, Mat image) {
        Point center = new Point(c.getX_coord(), c.getY_coord());
        Imgproc.circle(image, center, (int) c.getRadius(), new Scalar(127, 255, 212), 3);
    }
    public static List<Circle> houghTransformCircle(Mat image){
        List<Circle> houghPointList = new ArrayList<Circle>();
        Mat circles = new Mat();
        Mat tempMat = new Mat();
        Imgproc.cvtColor(image, tempMat, Imgproc.COLOR_RGB2GRAY);
        Imgproc.HoughCircles(tempMat,circles,Imgproc.CV_HOUGH_GRADIENT,2,150,200,100,0,0);

        for (int x = 0; x < circles.cols(); x++) {
            Circle temp = new Circle(0,0,0);
            double[] c = circles.get(0, x);
            Point center = new Point(Math.round(c[0]), Math.round(c[1]));
            temp.setX_coord((int) Math.round(c[0]));
            temp.setY_coord((int) Math.round(c[1]));
            temp.setRadius((int) Math.round(c[2]));
            houghPointList.add(temp);
        }
        return houghPointList;
    }

    public static Circle findMatchCircle(List<Circle> hsvCircles, List<Circle> hCircles){
        int tolerance = 15;
        int toleranceRadius = 10;
        Circle matchedCircle = new Circle(0,0,0);
        for (Circle hofCircle : hCircles){
            for(Circle hsvCir : hsvCircles){
                if((hsvCir.getX_coord() - tolerance) < hofCircle.getX_coord()
                        && hofCircle.getX_coord() < (hsvCir.getX_coord() + tolerance)
                        && (hsvCir.getY_coord() - tolerance) < hofCircle.getY_coord()
                        && hofCircle.getY_coord() < (hsvCir.getY_coord() + tolerance)
                        && (hsvCir.getRadius() - toleranceRadius) < hofCircle.getRadius()
                        && hofCircle.getRadius() < (hsvCir.getRadius() + toleranceRadius)){
                    matchedCircle.setX_coord(hsvCir.getX_coord());
                    matchedCircle.setY_coord(hsvCir.getY_coord());
                    matchedCircle.setRadius(hsvCir.getRadius());
                }
            }
        }
        return matchedCircle;
    }

    public static String pointOrder(int screenOrien, Map<String, Circle> circleCoordinates){
        if(screenOrien == -1)
            return null;
        String order = "";
        int meanX = 0, meanY = 0; //should be double
        Map<String, String> orders = new HashMap<String, String>();
        if(screenOrien == 0){
            for(Map.Entry<String, Circle> circle : circleCoordinates.entrySet()){
                if(circle.getValue().getX_coord() == 0 || circle.getValue().getY_coord() == 0){// 4 noktayi da ayni anda bulamazsa
                    return null;
                }
                meanX = meanX + circle.getValue().getX_coord();
                meanY = meanY + circle.getValue().getY_coord();
            }
            meanX = meanX / 4;
            meanY = meanY / 4;
            for(Map.Entry<String, Circle> circle : circleCoordinates.entrySet()){
                if(circle.getValue().getX_coord() < meanX && circle.getValue().getY_coord() > meanY){// sol ust
                    orders.put("1", circle.getKey());
                    currentCircles[0] = circle.getValue();
                }
                else if(circle.getValue().getX_coord() < meanX && circle.getValue().getY_coord() < meanY){ //sag ust
                    orders.put("2", circle.getKey());
                    currentCircles[1] = circle.getValue();
                }
                else if(circle.getValue().getX_coord() > meanX && circle.getValue().getY_coord() < meanY){// sag alt
                    orders.put("3", circle.getKey());
                    currentCircles[2] = circle.getValue();
                }
                else if(circle.getValue().getX_coord() > meanX && circle.getValue().getY_coord() > meanY){// sol alt
                    orders.put("4", circle.getKey());
                    currentCircles[3] = circle.getValue();
                }
            }
            order = orders.get("1") + orders.get("2") + orders.get("3") + orders.get("4");
            return order;
        }
        else if(screenOrien == 1){
            for(Map.Entry<String, Circle> circle : circleCoordinates.entrySet()){
                if(circle.getValue().getX_coord() == 0 || circle.getValue().getY_coord() == 0){// 4 noktayi da ayni anda bulamazsa
                    return null;
                }
                meanX = meanX + circle.getValue().getX_coord();
                meanY = meanY + circle.getValue().getY_coord();
            }
            meanX = meanX / 4;
            meanY = meanY / 4;
            for(Map.Entry<String, Circle> circle : circleCoordinates.entrySet()){
                if(circle.getValue().getX_coord() < meanX && circle.getValue().getY_coord() < meanY){// sol ust
                    orders.put("1", circle.getKey());
                    currentCircles[0] = circle.getValue();
                }
                else if(circle.getValue().getX_coord() > meanX && circle.getValue().getY_coord() < meanY){// sag ust
                    orders.put("2", circle.getKey());
                    currentCircles[1] = circle.getValue();
                }
                else if(circle.getValue().getX_coord() > meanX && circle.getValue().getY_coord() > meanY){// sag alt
                    orders.put("3", circle.getKey());
                    currentCircles[2] = circle.getValue();
                }
                else if(circle.getValue().getX_coord() < meanX && circle.getValue().getY_coord() > meanY){// sol alt
                    orders.put("4", circle.getKey());
                    currentCircles[3] = circle.getValue();
                }
            }
            order = orders.get("1") + orders.get("2") + orders.get("3") + orders.get("4");
            return order;
        }
        else if(screenOrien == 2){
            for(Map.Entry<String, Circle> circle : circleCoordinates.entrySet()){
                if(circle.getValue().getX_coord() == 0 || circle.getValue().getY_coord() == 0){// 4 noktayi da ayni anda bulamazsa
                    return null;
                }
                meanX = meanX + circle.getValue().getX_coord();
                meanY = meanY + circle.getValue().getY_coord();
            }
            meanX = meanX / 4;
            meanY = meanY / 4;
            for(Map.Entry<String, Circle> circle : circleCoordinates.entrySet()){
                if(circle.getValue().getX_coord() > meanX && circle.getValue().getY_coord() > meanY){// sol ust
                    orders.put("1", circle.getKey());
                    currentCircles[0] = circle.getValue();
                }
                else if(circle.getValue().getX_coord() < meanX && circle.getValue().getY_coord() > meanY){// sag ust
                    orders.put("2", circle.getKey());
                    currentCircles[1] = circle.getValue();
                }
                else if(circle.getValue().getX_coord() < meanX && circle.getValue().getY_coord() < meanY){// sag alt
                    orders.put("3", circle.getKey());
                    currentCircles[2] = circle.getValue();
                }
                else if(circle.getValue().getX_coord() > meanX && circle.getValue().getY_coord() < meanY){// sol alt
                    orders.put("4", circle.getKey());
                    currentCircles[3] = circle.getValue();
                }
            }
            order = orders.get("1") + orders.get("2") + orders.get("3") + orders.get("4");
            return order;
        }
        return null;
    }


    public static void initializColorHashMap(Map<String, List<Integer>> colorHueCodes){
        List<Integer> red = new ArrayList<Integer>();
        red.add(160); //min H value
        red.add(179); //max H value
        red.add(40); // sat value
        red.add(70); //min sat value
        colorHueCodes.put("R", red);

        List<Integer> green = new ArrayList<Integer>();
        green.add(40);
        green.add(80);
        green.add(40);
        green.add(70);
        colorHueCodes.put("G", green);

        List<Integer> blue = new ArrayList<Integer>();
        blue.add(80);
        blue.add(120);
        blue.add(150);
        blue.add(0);
        colorHueCodes.put("B", blue);

        List<Integer> yellow = new ArrayList<Integer>();
        yellow.add(20);
        yellow.add(30);
        yellow.add(100);
        yellow.add(100);
        colorHueCodes.put("Y", yellow);
    }
}