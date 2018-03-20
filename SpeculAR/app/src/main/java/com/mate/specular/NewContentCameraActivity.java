package com.mate.specular;

/**
 * Created by ETS on 27.02.2018.
 */
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.mate.specular.model.Circle;
import com.mate.specular.util.FrameProcess;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewContentCameraActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2, SensorEventListener{

    private static final String TAG = "NewContentCameraActiv";
    private CameraBridgeViewBase mOpenCvCameraView;
    private Button captureButton;
    Context activityContext;
    private AlertDialog processWaitDialog;
    Mat image;

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

        activityContext = this;

        captureButton = findViewById(R.id.captureFrameButton);
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Process pop up
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(activityContext, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(activityContext);
                }
                builder.setTitle("Please Wait Processing...")
                .setMessage("This will not take long");

                processWaitDialog = builder.create();
                processWaitDialog.show();

                String colorDensity = preprocessFrame();

                processWaitDialog.dismiss();

                captureButton.setVisibility(View.INVISIBLE);
                String colorOrder = "";//TODO ColorOrderUnique gelen alinacak
                showColorOrderAlert(colorOrder);
            }
        });
    }

    private String preprocessFrame() {
        String densityOrder = frameProcessor.detectColorDensity(image, screenOrien);
        Log.i(TAG, densityOrder + " XXX");
        return frameProcessor.detectColorDensity(image, screenOrien);
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
        image = inputFrame.rgba();
        circleCoordinates = frameProcessor.detectColor(image);
        String colorOrder = frameProcessor.pointOrder(screenOrien, circleCoordinates);
        for(Map.Entry<String, Circle> circle : circleCoordinates.entrySet()){
            Point center = new Point(circle.getValue().getX_coord(), circle.getValue().getY_coord());
            Imgproc.circle(image, center, (int) circle.getValue().getRadius(), new Scalar(127, 255, 212), 3);
        }
        //Log.i(TAG, colorOrder+"");

        //TODO color order check islemi degisecek capture butonunun onClickinde set edilecek
        if(colorOrder != null) {
            Log.i(TAG, "Color order detected successfully");
            Mat downSampledImage =  new Mat();
            Imgproc.pyrDown(image, downSampledImage);

            //Do the image serializable
            byte[] byteArray = matToByteArray(downSampledImage);

            Intent intent = new Intent(NewContentCameraActivity.this, CreateModelActivity.class);
            intent.putExtra("image", byteArray);
            intent.putExtra("circleCoordinates", (Serializable) circleCoordinates);
            intent.putExtra("colorOrder", colorOrder);
            startActivity(intent);
        }
        return image;
    }

    private byte[] matToByteArray(Mat mat) {
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        Bitmap bitmap = Bitmap.createBitmap(mat.cols(),  mat.rows(),Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat, bitmap);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bStream);
        byte[] byteArray = bStream.toByteArray();

        return byteArray;
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

    private void showColorOrderAlert(String colorOrder) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle("Color Order Set")
                .setMessage("Please, set your color: " + colorOrder)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }
}
