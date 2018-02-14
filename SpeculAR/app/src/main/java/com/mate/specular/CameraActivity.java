package com.mate.specular;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.WindowManager;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CameraActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2{

    private static final String TAG = "CameraActivity";
    private CameraBridgeViewBase mOpenCvCameraView;
    public static final Map<String, List<Integer>> colorHueCodes = new HashMap<String, List<Integer>>();
    //TODO hashmap initialize edilecek

    static {
        if (!OpenCVLoader.initDebug())
            Log.d("ERROR", "Unable to load OpenCV");
        else
            Log.d("SUCCESS", "OpenCV loaded");
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                    try {
                        initializeOpenCVDependencies();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    private void initializeOpenCVDependencies() throws IOException {
        mOpenCvCameraView.enableView();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_camera);
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.camera_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);

    }

    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        Mat image = inputFrame.rgba();
        Mat retMat = detectColor(image, "B");
        return retMat;
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

    public static List<Circle> hsvColorDetect(Mat mask, Mat image){
        List<Circle> circles = new ArrayList<Circle>();
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(mask, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
        System.out.println(contours.size());
        //Imgproc.drawContours(image, contours, -1, new Scalar(0, 0, 0), 4); //draw contour
        for(int i = 0; i < contours.size(); i++){
            Circle temp = new Circle(0,0,0);
            float[] radius = new float[1];
            Point center = new Point();
            Imgproc.minEnclosingCircle(new MatOfPoint2f(contours.get(i).toArray()), center, radius);
            if(Math.round(radius[0]) > 5) {
                //Imgproc.circle(image, center, Math.round(radius[0]), new Scalar(255, 255, 255));
                temp.x  = (int) center.x;
                temp.y = (int) center.y;
                temp.radius = Math.round(radius[0]);
                circles.add(temp);
                //Imgproc.drawContours(image, contours, -1, new Scalar(255, 0, 0), 2); //draw contour
            }
        }
        return circles;
    }

    public Mat detectColor(Mat image, String color){ //resimi ve detect edilecek color i string olarak alio simdilik
        //Log.i(TAG, "detectColor");
        Mat hsvImage = new Mat();
        Imgproc.GaussianBlur(image, image, new Size(3,3), 2, 2);
        Imgproc.cvtColor(image, hsvImage, Imgproc.COLOR_RGB2HSV);
        Mat mask =  new Mat();
        List<Circle> houghCircleList = houghTransformCircle(image);

        if(color.equalsIgnoreCase("R")){ //Red filtering
            mask = thresholdHue(hsvImage,160,179,100,100);
            List<Circle> hsvCircles = hsvColorDetect(mask, image);
            Circle redC = new Circle(0,0,0);
            redC = findMatchCircle(hsvCircles, houghCircleList);
            Point center = new Point(redC.x, redC.y);
            Imgproc.circle(image, center, redC.radius, new Scalar(127, 255, 212), 3);
            return image;

        }
        else if(color.equalsIgnoreCase("G")){ //Green
            mask =  thresholdHue(hsvImage,40,80,100,100);
            List<Circle> hsvCircles = hsvColorDetect(mask, image);
            Circle greenC = new Circle(0,0,0);
            greenC = findMatchCircle(hsvCircles, houghCircleList);
            Point center = new Point(greenC.x, greenC.y);
            Imgproc.circle(image, center, greenC.radius, new Scalar(127, 255, 212), 3);

            return image;
        }
        else if(color.equalsIgnoreCase("B")){ //Blue
            mask =  thresholdHue(hsvImage,80,120,150,0);
            List<Circle> hsvCircles = hsvColorDetect(mask, image);
            Circle blueC = new Circle(0,0,0);
            blueC = findMatchCircle(hsvCircles, houghCircleList);
            Point center = new Point(blueC.x, blueC.y);
            Imgproc.circle(image, center, blueC.radius, new Scalar(127, 255, 212), 3);

            return image;
        }
        else if(color.equalsIgnoreCase("Y")){ //Yellow
            mask =  thresholdHue(hsvImage,20,30,100,100);
            List<Circle> hsvCircles = hsvColorDetect(mask, image);
            Circle yellowC = new Circle(0,0,0);
            yellowC = findMatchCircle(hsvCircles, houghCircleList);
            Point center = new Point(yellowC.x, yellowC.y);
            Imgproc.circle(image, center, yellowC.radius, new Scalar(127, 255, 212), 3);

            return image;
        }
        return mask;
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
            // circle center
            Imgproc.circle(image, center, 1, new Scalar(0,100,100), 3, 8, 0 );
            // circle outline
            int radius = (int) Math.round(c[2]);
            //Imgproc.circle(image, center, radius, new Scalar(255,0,255), 3, 8, 0 );
            temp.x = (int) Math.round(c[0]);
            temp.y = (int) Math.round(c[1]);
            temp.radius = (int) Math.round(c[2]);
            houghPointList.add(temp);
        }
        return houghPointList;
    }

    public static Circle findMatchCircle(List<Circle> hsvCircles, List<Circle> hCircles){
        int tolerance = 10;
        int toleranceRadius = 5;
        Circle matchedCircle = new Circle(0,0,0);
        for (Circle hofCircle : hCircles){
            for(Circle hsvCir : hsvCircles){
                if((hsvCir.x - tolerance) < hofCircle.x && hofCircle.x < (hsvCir.x + tolerance)  && (hsvCir.y - tolerance) < hofCircle.y && hofCircle.y < (hsvCir.y + tolerance) && (hsvCir.radius - toleranceRadius) < hofCircle.radius && hofCircle.radius < (hsvCir.radius + toleranceRadius)){
                    matchedCircle.x = hsvCir.x;
                    matchedCircle.y = hsvCir.y;
                    matchedCircle.radius = hsvCir.radius;
                }
            }
        }
        return matchedCircle;
    }

}
