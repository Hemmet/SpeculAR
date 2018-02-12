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
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
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
        Log.i(TAG, "thresholdHue");
        Mat mask = new Mat();
        List<Mat> channels = new ArrayList<Mat>();

        Core.split(hsvImage, channels);

        /*int targetHueVal = hueVal / 2;
        int shift = targetHueVal - hueVal;
        if (shift < 0) shift += 180;*/

        Mat shiftedHue = channels.get(0);//shiftChannel(channels.get(0), shift, 180);

        List<Mat> newChannels = new ArrayList<Mat>();
        newChannels.add(shiftedHue);
        newChannels.add(channels.get(1));
        newChannels.add(channels.get(2));
        Mat shiftedHSV = new Mat();
        Core.merge(newChannels, shiftedHSV);

        // threshold
        Core.inRange(shiftedHSV, new Scalar(hueValMin, minSat, minValue), new Scalar(hueValMax, 255, 255), mask);
        //Core.inRange(shiftedHSV, new Scalar(targetHueVal - range, minSat, minValue), new Scalar(targetHueVal + range, 255, 255), mask);

        return mask;
    }

    public Mat detectColor(Mat image, String color){ //resimi ve detect edilecek color i string olarak alio simdilik
        //TODO renge gore HUE degerlerini olustur bi yerden cekiver.
        Log.i(TAG, "detectColor");
        Mat hsvImage = new Mat();
        Imgproc.blur(image, image, new Size(3,3));
        Imgproc.cvtColor(image, hsvImage, Imgproc.COLOR_RGB2HSV);
        Mat mask = new Mat();

        if(color.equalsIgnoreCase("R")){ //Red filtering
            mask =  thresholdHue(hsvImage,0,15,100,100);
            //return mask;
            Mat kernel = new Mat();
            kernel = Imgproc.getStructuringElement(Imgproc.CV_HOUGH_STANDARD, new Size(50,50));
            Mat dilateMat = new Mat();
            Imgproc.dilate(mask, dilateMat, kernel);
            List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
            Imgproc.findContours(dilateMat, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
            System.out.println(contours.size());
            for(int i = 0; i < contours.size(); i++) {
                Imgproc.drawContours(image, contours, -1, new Scalar(0, 0, 0), 2); //draw contour
            }
            return image;
        }
        else if(color.equalsIgnoreCase("G")){ //Green
            mask =  thresholdHue(hsvImage,40,80,100,100);
            Mat kernel = new Mat();
            kernel = Imgproc.getStructuringElement(Imgproc.CV_HOUGH_STANDARD, new Size(50,50));
            Mat dilateMat = new Mat();
            Imgproc.dilate(mask, dilateMat, kernel);
            List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
            Imgproc.findContours(dilateMat, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
            System.out.println(contours.size());
            for(int i = 0; i < contours.size(); i++) {
                Imgproc.drawContours(image, contours, -1, new Scalar(0, 0, 0), 2); //draw contour
            }
            return image;
        }
        else if(color.equalsIgnoreCase("B")){ //Blue
            mask =  thresholdHue(hsvImage,80,120,150,0);
            Mat kernel = new Mat();
            kernel = Imgproc.getStructuringElement(Imgproc.CV_HOUGH_STANDARD, new Size(50,50));
            Mat dilateMat = new Mat();
            Imgproc.dilate(mask, dilateMat, kernel);
            List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
            Imgproc.findContours(dilateMat, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
            System.out.println(contours.size());
            for(int i = 0; i < contours.size(); i++) {
                Imgproc.drawContours(image, contours, -1, new Scalar(0, 0, 0), 2); //draw contour
            }
            return image;
        }
        else if(color.equalsIgnoreCase("Y")){ //Yellow
            mask =  thresholdHue(hsvImage,20,30,100,100);
            Mat kernel = new Mat();
            kernel = Imgproc.getStructuringElement(Imgproc.CV_HOUGH_STANDARD, new Size(50,50));
            Mat dilateMat = new Mat();
            Imgproc.dilate(mask, dilateMat, kernel);
            List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
            Imgproc.findContours(dilateMat, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
            System.out.println(contours.size());
            for(int i = 0; i < contours.size(); i++) {
                Imgproc.drawContours(image, contours, -1, new Scalar(0, 0, 0), 2); //draw contour
            }
            return image;
        }
        return mask;
    }

    public List detectCircles(){
        List<MatOfPoint> circleList = new ArrayList<MatOfPoint>();

        return circleList;
    }
}
