package com.mate.specular;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.opencv.android.OpenCVLoader;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    Button signUpTextButton;
    static {
        if(OpenCVLoader.initDebug()){
            Log.d(TAG, "OpenCV succesfully loaded.");
        }else{
            Log.d(TAG, "OpenCV not loaded.");
        }
    }

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signUpTextButton = (Button) findViewById(R.id.signUpButton);
        signUpTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

    }

    /*public EditText username =  findViewById(R.id.userLoginEmail);
    public EditText password = (EditText) findViewById(R.id.userLoginPassword);
    public void loginButtonAction(View view){
        Log.i("Info", "Login Button Action method started");

        Log.i("Info", "Username: " + username.getText().toString() + " password: " + password.getText().toString());

        //TODO login servisi icin gerekli islem gelecek

    }*/

    public void signupButtonAction(){
        Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
        startActivity(intent);
    }



    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}
