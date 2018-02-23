package com.mate.specular;

import android.app.Activity;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.WindowManager;

import com.mate.specular.model.Circle;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfPoint3;
import org.opencv.core.Point;
import org.opencv.core.Point3;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CameraActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2, SensorEventListener {

    private static final String TAG = "CameraActivity";
    private CameraBridgeViewBase mOpenCvCameraView;
    public static Map<String, List<Integer>> colorHueCodes = new HashMap<String, List<Integer>>();
    public static Map<String, Circle> circleCoordinates = new HashMap<String, Circle>();
    public static int screenOrien = 0; // o sa dik 1 se sol 2 ise saga yatmis oluo baby

    //sensor icin degiskenler
    private SensorManager sensorManager;
    private float[] lastMagFields = new float[3];;
    private float[] lastAccels = new float[3];
    private float[] rotationMatrix = new float[16];
    private float[] orientation = new float[4];

    List<MatOfPoint2f> points = new ArrayList<>();


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
                    try {
                        mOpenCvCameraView.enableView();
                    } catch (Exception e) {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MatOfPoint2f p = new MatOfPoint2f(new Point(350,350), new Point(700, 350), new Point(700,700), new Point(350,700));
        points.add(p);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_camera);
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.camera_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        initializColorHashMap(colorHueCodes);
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

    }

    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
        sensorManager.unregisterListener(this);

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
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
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
        Mat retMat = drawInfo(image/*detectColor(image)*/, 90,90,90,0,0,300,250);

        //image.release();

        String pointOrders = pointOrder();
        System.out.println(pointOrders + "   HEBELE");
        return retMat;
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                System.arraycopy(event.values, 0, lastAccels, 0, 3);
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                System.arraycopy(event.values, 0, lastMagFields, 0, 3);
                break;
            default:
                return;
        }

        if (SensorManager.getRotationMatrix(rotationMatrix, null, lastAccels, lastMagFields)) {
            SensorManager.getOrientation(rotationMatrix, orientation);

            float xAxis = (float) Math.toDegrees(orientation[1]);
            float yAxis = (float) Math.toDegrees(orientation[2]);

            int orientation = Configuration.ORIENTATION_UNDEFINED;
            if ((yAxis <= 25) && (yAxis >= -25) && (xAxis >= -160)) {
                screenOrien = 0;
                orientation = Configuration.ORIENTATION_PORTRAIT;
            } else if ((yAxis < -25) && (xAxis >= -20)) {
                screenOrien = 1;
                orientation = Configuration.ORIENTATION_LANDSCAPE;
            } else if ((yAxis > 25) && (xAxis >= -20)){
                screenOrien = 2;
                orientation = Configuration.ORIENTATION_LANDSCAPE;
            }
        }
    }
    private Mat warp(Mat input, double alpha, double beta, double gamma, double dx, double dy, double dz, double f)

    {

        System.out.println(input.dump() + "ASDASDASD");
        alpha = (alpha - 90.)*Math.PI/180.;
        beta = (beta - 90.)*Math.PI/180.;
        gamma = (gamma - 90.)*Math.PI/180.;

        // get width and height for ease of use in matrices
        double w = input.size().width;
        double h = input.size().height;

        // Projection 2D -> 3D matrix
        int row = 0, col = 0;
        double data[] = {  1, 0, -w/2,

                0, 1, -h/2,

                0, 0,    0,

                0, 0,    1};
        //allocate Mat before calling put
        Mat A1 = new Mat( 4, 3, CvType.CV_32F );
        A1.put( row, col, data );


        // Rotation matrices around the X, Y, and Z axis
        row = 0;
        col = 0;
        double datarx[] = {  1,          0,           0, 0,

                0, Math.cos(alpha), -Math.sin(alpha), 0,

                0, Math.sin(alpha),  Math.cos(alpha), 0,

                0,          0,           0, 1};
        //allocate Mat before calling put
        Mat RX = new Mat( 4, 4, CvType.CV_32F );
        RX.put( row, col, datarx );

        row = 0;
        col = 0;
        double datary[] = {   Math.cos(beta), 0, -Math.sin(beta), 0,

                0, 1,          0, 0,

                Math.sin(beta), 0,  Math.cos(beta), 0,

                0, 0,          0, 1};
        //allocate Mat before calling put
        Mat RY = new Mat( 4, 4, CvType.CV_32F );
        RY.put( row, col, datary );

        row = 0;
        col = 0;
        double datarz[] = {   Math.cos(gamma), -Math.sin(gamma), 0, 0,

                Math.sin(gamma),  Math.cos(gamma), 0, 0,

                0,          0,           1, 0,

                0,          0,           0, 1};
        //allocate Mat before calling put
        Mat RZ = new Mat( 4, 4, CvType.CV_32F );
        RZ.put( row, col, datarz );

        // Composed rotation matrix with (RX, RY, RZ)

        Mat RM = new Mat( 4, 4, CvType.CV_32F );
        Mat tmp = new Mat( 4, 4, CvType.CV_32F );
        Core.gemm(RX,RY,1,new Mat(),0,tmp);
        Core.gemm(tmp,RZ,1,new Mat(),0,RM);

        System.out.println(RM.dump()+"<<E<EKEKWMQ");

        // Translation matrix
        row = 0;
        col = 0;
        double datat[] = {   1, 0, 0, dx,

                0, 1, 0, dy,

                0, 0, 1, dz,

                0, 0, 0, 1};
        //allocate Mat before calling put
        Mat T = new Mat( 4, 4, CvType.CV_32F );
        T.put( row, col, datat );


        // 3D -> 2D matrix

        row = 0;
        col = 0;
        double datarev[] = {   f, 0, w/2, 0,

                0, f, h/2, 0,

                0, 0,   1, 0};
        //allocate Mat before calling put
        Mat A2 = new Mat( 3, 4, CvType.CV_32F );
        A2.put( row, col, datarev );

        // Final transformation matrix

        Mat trans = new Mat( 3, 3, CvType.CV_32F );
        Mat tmp2 =new Mat( 4, 3, CvType.CV_32F );
        Mat tmp3 = new Mat( 4, 3, CvType.CV_32F );
        Core.gemm(RM,A1,1,new Mat(),0,tmp3);
        Core.gemm(T,tmp3,1,new Mat(),0,tmp2);
        Core.gemm(A2,tmp2,1,new Mat(),0,trans);

        // Apply matrix transformation
        Mat output = new Mat();
        Imgproc.warpPerspective(input, output, trans, input.size());
        System.out.println(output.dump()+ "asdasqweqwe" );
        return output;
    }
    private Mat drawInfo(Mat image, double alpha, double beta, double gamma, double dx, double dy, double dz, double f){
        MatOfPoint2f m = new MatOfPoint2f(warp(points.get(0), alpha, beta, gamma, dx, dy, dz, f));
        MatOfPoint t = new MatOfPoint(m.toArray());

        List<MatOfPoint> draw = new ArrayList<>();
        draw.add(t);

        Imgproc.drawContours(image, draw, -1, new Scalar(96,255,155), -5);

        return image;
    }
    public static String pointOrder(){
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
                }
                else if(circle.getValue().getX_coord() < meanX && circle.getValue().getY_coord() < meanY){ //sag ust
                    orders.put("2", circle.getKey());
                }
                else if(circle.getValue().getX_coord() > meanX && circle.getValue().getY_coord() < meanY){// sag alt
                    orders.put("3", circle.getKey());
                }
                else if(circle.getValue().getX_coord() > meanX && circle.getValue().getY_coord() > meanY){// sol alt
                    orders.put("4", circle.getKey());
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
                }
                else if(circle.getValue().getX_coord() > meanX && circle.getValue().getY_coord() < meanY){// sag ust
                    orders.put("2", circle.getKey());
                }
                else if(circle.getValue().getX_coord() > meanX && circle.getValue().getY_coord() > meanY){// sag alt
                    orders.put("3", circle.getKey());
                }
                else if(circle.getValue().getX_coord() < meanX && circle.getValue().getY_coord() > meanY){// sol alt
                    orders.put("4", circle.getKey());
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
                }
                else if(circle.getValue().getX_coord() < meanX && circle.getValue().getY_coord() > meanY){// sag ust
                    orders.put("2", circle.getKey());
                }
                else if(circle.getValue().getX_coord() < meanX && circle.getValue().getY_coord() < meanY){// sag alt
                    orders.put("3", circle.getKey());
                }
                else if(circle.getValue().getX_coord() > meanX && circle.getValue().getY_coord() < meanY){// sol alt
                    orders.put("4", circle.getKey());
                }
            }
            order = orders.get("1") + orders.get("2") + orders.get("3") + orders.get("4");
            return order;
        }
        return null;
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
        System.out.println(contours.size());
        //Imgproc.drawContours(image, contours, -1, new Scalar(0, 0, 0), 4); //draw contour
        for(int i = 0; i < contours.size(); i++){
            Circle temp = new Circle(0,0,0);
            float[] radius = new float[1];
            Point center = new Point();
            Imgproc.minEnclosingCircle(new MatOfPoint2f(contours.get(i).toArray()), center, radius);
            if(Math.round(radius[0]) > 5) {
                //Imgproc.circle(image, center, Math.round(radius[0]), new Scalar(255, 255, 255));
                temp.setX_coord((int) center.x);
                temp.setY_coord((int) center.y);
                temp.setRadius(Math.round(radius[0]));
                circles.add(temp);
                //Imgproc.drawContours(image, contours, -1, new Scalar(255, 0, 0), 2); //draw contour
            }
        }
        return circles;
    }

    public Mat detectColor(Mat image){
        //Log.i(TAG, "detectColor");
        Mat hsvImage = new Mat();
        Imgproc.GaussianBlur(image, image, new Size(3,3), 2, 2);
        Imgproc.cvtColor(image, hsvImage, Imgproc.COLOR_RGB2HSV);
        Mat mask =  new Mat();
        List<Circle> houghCircleList = houghTransformCircle(image);

        for(Map.Entry<String, List<Integer>> hValue : colorHueCodes.entrySet()){
            String colorCode = hValue.getKey();
            List<Integer> thresholdValues = hValue.getValue();
            mask = thresholdHue(hsvImage, thresholdValues.get(0), thresholdValues.get(1), thresholdValues.get(2), thresholdValues.get(3));
            List<Circle> hsvCircles = hsvColorCircleDetect(mask, image);
            Circle tempC = new Circle(0,0,0);
            tempC = findMatchCircle(hsvCircles, houghCircleList);
            circleCoordinates.put(colorCode, tempC);
            //debug amacli ekrana basarken kullanilio
            drawCircle(tempC, image);
        }
        return image;
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
            // circle center
            //Imgproc.circle(image, center, 1, new Scalar(0,100,100), 3, 8, 0 );
            // circle outline
            //int radius = (int) Math.round(c[2]);
            //Imgproc.circle(image, center, radius, new Scalar(255,0,255), 3, 8, 0 );
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
