package com.startupeuropesummit.lighttalk.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.startupeuropesummit.lighttalk.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MainActivity extends AppCompatActivity {

    private static String TAG = "MainActivity";

    public static int DELAY_START = 3000;
    public static int FRAME_RATE = 500;

    private Context context;

    private Timer timer;
    private TimerTask timerTask;

    private boolean isFlashOn;
    private String message;
    private int actualPosition;
    private boolean firstTime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.context = this;

        setContentView(R.layout.activity_main);

        /*
		 * First check if device is supporting flashlight or not
		 */
        boolean hasFlash = getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        if (!hasFlash) {
            // device doesn't support flash
            // Show alert message and close the application
            /*AlertDialog alert = new AlertDialog.Builder(MainActivity.this)
                    .create();
            alert.setTitle("Error");
            alert.setMessage("Sorry, your device doesn't support flash light!");
            alert.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // closing the application
                    finish();
                }
            });
            alert.show();*/
            return;
        }
    }

    /**
     * Send message button clicked
     * @param view
     */
    public void sendMessageClicked(View view) {
        // Do something in response to button click
    }

    /**
     * Receive message button clicked
     * @param view
     */
    public void receiveMessageClicked(View view) {
        Intent intent = new Intent(this, ReceiveActivity.class);
        startActivity(intent);
    }

    /**
     * SOS message button clicked
     * @param view
     */
    public void sosMessageClicked(View view) {
        // Do something in response to button click
        sendMessage("...-.");
    }

    private void turnOnFlashLight(){
        try{

            CameraManager manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
            String[] list = manager.getCameraIdList();
            manager.setTorchMode(list[0], true);

            isFlashOn = true;
        }
        catch (CameraAccessException cae){
            Log.e(TAG, cae.getMessage());
            cae.printStackTrace();
        }
    }

    private void turnOffFlashLight(){
        try{
            CameraManager manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
            manager.setTorchMode(manager.getCameraIdList()[0], false);

            isFlashOn = false;
        }
        catch (CameraAccessException cae){
            Log.e(TAG, cae.getMessage());
            cae.printStackTrace();
        }
    }

    private void toggleFlashLight() {
        if (isFlashOn) {
            turnOffFlashLight();
        } else {
            turnOnFlashLight();
        }
    }

    public void sendMessage(String message) {
        //set a new Timer
        timer = new Timer();

        this.message = message;
        this.actualPosition = 0;

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, after the first DELAY_STARTms the TimerTask will run every FRAME_RATEms
        timer.schedule(timerTask, DELAY_START, FRAME_RATE); //
    }

    public void cancelSendMessage() {
        turnOffFlashLight();

        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void checkActualPosition() {
        if (actualPosition >= message.length()) {
            // Message finished
            cancelSendMessage();
        } else {
            char actualCharacter = message.charAt(actualPosition);

            if (isFlashOn) {
                if (actualCharacter == '-') {
                    if (firstTime) {
                        firstTime = false;
                    } else {
                        firstTime = true;
                        turnOffFlashLight();
                        actualPosition++;
                    }
                } else if (actualCharacter == '.') {
                    turnOffFlashLight();
                    actualPosition++;
                }
            } else {
                Log.d(TAG, "actualCharacter: " + actualCharacter);
                turnOnFlashLight();
            }
        }
    }

    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                checkActualPosition();
            }
        };
    }

}
