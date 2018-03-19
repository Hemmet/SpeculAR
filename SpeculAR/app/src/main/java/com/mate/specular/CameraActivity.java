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
import com.mate.specular.model.Color;
import com.mate.specular.model.Frame;
import com.mate.specular.util.FrameFinder;
import com.mate.specular.util.FrameProcess;
import com.mate.specular.util.PointProcess;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CameraActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2, SensorEventListener {

    private static final String TAG = "CameraActivity";
    private CameraBridgeViewBase mOpenCvCameraView;
    public static Map<String, Circle> circleCoordinates = new HashMap<String, Circle>();

    private static Frame currentFrame;
    public static int screenOrien = 0; // o sa dik 1 se sol 2 ise saga yatmis oluo baby


    public static FrameProcess frameProcessor = new FrameProcess();

    //sensor icin degiskenler
    private SensorManager sensorManager;
    private float[] lastMagFields = new float[3];
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
        circleCoordinates = frameProcessor.detectColor(image);
        //Debug amaciyla kullanilan for
        for(Map.Entry<String, Circle> circle : circleCoordinates.entrySet()){
            Point center = new Point(circle.getValue().getX_coord(), circle.getValue().getY_coord());
            Imgproc.circle(image, center, (int) circle.getValue().getRadius(), new Scalar(127, 255, 212), 3);
        }
        String pointOrders = frameProcessor.pointOrder(screenOrien, circleCoordinates);

        Log.i(TAG,pointOrders+"");
        //Mat retMat = drawInfo(image/*detectColor(image)*/, 90,90,90,0,0,300,250);

        MatOfPoint2f objectRefs = null;
        MatOfPoint2f curObjects = null;

        if(pointOrders != null) {
            currentFrame = FrameFinder.findFrameWith(stringOrderToColorList(pointOrders));
            if(currentFrame != null){
                Mat homographyMat = PointProcess.fındHomography(currentFrame.getCircles(), FrameProcess.currentCircles);
                objectRefs = PointProcess.createReferenceMatrix(currentFrame);
                curObjects = PointProcess.applyHomography(objectRefs, homographyMat);
            }
        }
        
        return image;
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
    private List<Color> stringOrderToColorList(String order){
        if(order == null){
            return null;
        }
        char[] colorOrder = order.toCharArray();
        List<Color> colorList = new ArrayList<>();

        for (char color : colorOrder){
            if(color == 'R') colorList.add(Color.RED);
            else if(color == 'B') colorList.add(Color.BLUE);
            else if(color == 'Y') colorList.add(Color.GOLD);
            else if(color == 'G') colorList.add(Color.GREEN);
            else colorList.add(Color.BLACK);
        }
        return colorList;
    }

    private Mat warp(Mat input, double alpha, double beta, double gamma, double dx, double dy, double dz, double f){

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

    private void drawCircle (Circle c, Mat image) {
        Point center = new Point(c.getX_coord(), c.getY_coord());
        Imgproc.circle(image, center, (int) c.getRadius(), new Scalar(127, 255, 212), 3);
    }

}
