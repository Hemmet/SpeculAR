package com.mate.specular;

/**
 * Created by ETS on 27.02.2018.
 */
import android.app.Activity;
import android.content.Intent;
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
import com.mate.specular.util.FrameProcess;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import java.util.HashMap;
import java.util.Map;

public class NewContentCameraActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2, SensorEventListener{

    private static final String TAG = "NewContentCameraActiv";
    private CameraBridgeViewBase mOpenCvCameraView;

    public static Map<String, Circle> circleCoordinates = new HashMap<String, Circle>();

    private SensorManager sensorManager;
    private float[] lastMagFields = new float[3];;
    private float[] lastAccels = new float[3];
    private float[] rotationMatrix = new float[16];
    private float[] orientation = new float[4];
    public static int screenOrien = 0; // o sa dik 1 se sol 2 ise saga yatmis oluo baby

    public static FrameProcess frameProcessor = new FrameProcess();

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

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_new_content_camera);
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
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame){
        Mat image = inputFrame.rgba();
        circleCoordinates = frameProcessor.detectColor(image);
        String colorOrder = frameProcessor.pointOrder(screenOrien, circleCoordinates);
        Log.i(TAG, colorOrder+"");
        //TODO color order check islemi degisecek
        if(colorOrder != null) {
            //NEW MODEL CONTENT CREATION EKRANINA GONDERILECEK
            startActivity(new Intent(NewContentCameraActivity.this, PlaceListActivity.class));
            Log.i(TAG, "basarili");
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
}
