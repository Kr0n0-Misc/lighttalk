package com.startupeuropesummit.lighttalk.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.TextView;

import com.startupeuropesummit.lighttalk.R;

import org.opencv.android.JavaCameraView;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.sql.SQLInvalidAuthorizationSpecException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by dmananes on 04/06/16.
 */
public class ReceiveActivity extends AppCompatActivity implements CvCameraViewListener2 {

    // Used for logging success or failure messages
    private static final String TAG = "ReceiveActivity";

    private static int SQUARE_SIZE = 200;

    private Timer timer;
    private TimerTask timerTask;
    final Handler handler = new Handler();

    private int lastWhites;
    private int actualWhites;

    private boolean isFlashOn;
    private boolean isLine;

    private List<Character> message;

    // Loads camera view of OpenCV for us to use. This lets us see using OpenCV
    private CameraBridgeViewBase mOpenCvCameraView;

    Mat image;
    Mat imageProcessed;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_receive);

        mOpenCvCameraView = (JavaCameraView) findViewById(R.id.show_camera_activity_java_surface_view);

        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);

        mOpenCvCameraView.setCvCameraViewListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }

        //set a new Timer
        timer = new Timer();

        timerTask = new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        checkActualLight();
                    }
                });
            }
        };

        //schedule the timer, after the first DELAY_STARTms the TimerTask will run every FRAME_RATEms
        timer.schedule(timerTask, MainActivity.DELAY_START, MainActivity.FRAME_RATE);

        message = new ArrayList<Character>();
        isFlashOn = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();

        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
        image = new Mat(height, width, CvType.CV_8UC4);
        imageProcessed = new Mat(SQUARE_SIZE, SQUARE_SIZE, CvType.CV_8UC4);
    }

    public void onCameraViewStopped() {
        image.release();
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        image = inputFrame.rgba();

        // Rotate mRgba 90 degrees
        //Core.transpose(image, image);
        //Imgproc.resize(image, image, image.size(), 0,0, 0);
        //Core.flip(image, image, 1);

        // Crop image
        Mat mask = Mat.zeros(image.size(), image.type()); // all black
        int x = (int)((image.size().width/2) - (SQUARE_SIZE/2));
        int y = (int)((image.size().height/2) - (SQUARE_SIZE/2));
        Rect sel = new Rect(x, y, SQUARE_SIZE, SQUARE_SIZE);
        mask.submat(sel).setTo(Scalar.all(255)); // white square
        Core.bitwise_and(image, mask, image);

        // Convert to gray
        Imgproc.cvtColor(image, image, Imgproc.COLOR_RGB2GRAY);

        // Get the thresholded image
        Imgproc.threshold(image, image , MainActivity.THRESHOLD, 255, Imgproc.THRESH_BINARY);

        // Clean
        //Imgproc.erode(image, image, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(20,20)));
        //Imgproc.dilate(image, image, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(20,20)));

        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(5, 5));
        Imgproc.morphologyEx(image, image, Imgproc.MORPH_OPEN, kernel);

        return image; // This function must return
    }

    public void checkActualLight() {
        // now convert to gray
        //Imgproc.cvtColor(image, imageProcessed, Imgproc.COLOR_RGB2GRAY);

        // get the thresholded image
        //Imgproc.threshold(imageProcessed, imageProcessed , MainActivity.THRESHOLD, 255, Imgproc.THRESH_BINARY);

        // Calculate histogram
        List<Mat> matList = new LinkedList<Mat>();
        matList.add(image);
        Mat histogram = new Mat();
        MatOfFloat ranges=new MatOfFloat(0,256);
        Imgproc.calcHist(
                matList,
                new MatOfInt(0),
                new Mat(),
                histogram,
                new MatOfInt(2),
                ranges);

        //int black = (int)(histogram.get(0, 0)[0]);
        lastWhites = actualWhites;
        actualWhites = (int)(histogram.get(1, 0)[0]);

        System.out.println("lastWhites: " + lastWhites + " - actualWhites: " + actualWhites);

        calculateCharacter();
    }

    private void calculateCharacter() {
        //int error = (int)(lastWhites * 0.2);
        //if (error < 1000) error = 1000;
        int error = 10000;

        System.out.println("error: " + error);

        int minLastBorder = lastWhites - error;
        int maxLastBorder = lastWhites + error;

        if (actualWhites < minLastBorder) {
            // From on to off
            if (isLine) {
                message.add(new Character('-'));
            } else {
                message.add(new Character(('.')));
            }

            isFlashOn = false;
            isLine = false;

            // Show message on screen
            showMessage();
        } else if (actualWhites > maxLastBorder) {
            // From off to on
            isFlashOn = true;
        } else {
            // No change
            if (isFlashOn) {
                isLine = true;
            }
        }
    }

    private void showMessage() {
        String messageStr = "";
        for(int i=0; i<message.size(); i++) {
            messageStr += message.get(i);
        }

        TextView textView = (TextView)findViewById(R.id.message);
        if (textView != null) {
            textView.setText(messageStr);
        }
    }

}
