package com.example.jungl.one_click_phone_call;



import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback, Camera.PreviewCallback{


    Button callButton;
    String phoneNumber;
    SurfaceView surface;

    SurfaceHolder surfaceHolder;
    Camera cam;
    Canvas canvas;
    Paint paint;
    View rectangleView;
    TextView phoneNumberText;

    int[] rectangleCoords = new int[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        surface = (SurfaceView)findViewById(R.id.surfaceView2);
        rectangleView = (View)findViewById(R.id.myRectangleView);
        callButton = (Button)findViewById(R.id.callButton);
        phoneNumberText = (TextView)findViewById(R.id.phoneText);


        callButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                //call method to call number
                callNumber(parsePhoneNumber(phoneNumberText.getText().toString()));

            }
        });

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {

            String[] permissionRequested = {Manifest.permission.CAMERA};
            ActivityCompat.requestPermissions(this, permissionRequested, 10);
        }
        else{
            surfaceHolder = surface.getHolder();
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

    }

    public Bitmap cropBitmap(Bitmap imgToCrop){

        /**
         * THE CODE COMMENTED HERE IS OLD AND ONLY FOR REFERENCE
         * THE CODE BELOW THAT SHOULD BE USED.  RESCALING THE IMAGE
         * CAUSES ERRORS IN THE CHARACTER RECOGNITION.
         *
        // needed for rescaling the image.
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        Matrix matrix = new Matrix();
        matrix.postRotate(90); // The camera is literally 90 degress of by rotation and is neither in landscape or portrait orientation


        // the rectangle in the xml has greater pixel height and width than the image
        //
        // Just did a test and that is wrong.  The x and y coordinates are switched.
        //Bitmap img = Bitmap.createScaledBitmap(imgToCrop, width, height, true);

         //* The createBitmap coordinates need to be played with.  x means up and down and y means left and right.
        // * Fiddle with it until the screen is fit with the rectangle.  I did my best, but am tired now.

        //img = Bitmap.createBitmap(img, rectangleCoords[0] + 100,rectangleCoords[1] - 300,  rectangleCoords[3] , rectangleCoords[2] +300 , matrix, true);
        **/
        // Gathering coordinates from the rectangle that is with the layout.xml file.
        rectangleView.getLocationOnScreen(rectangleCoords);
        rectangleCoords[2] = rectangleView.getWidth();
        rectangleCoords[3] = rectangleView.getHeight();



        Matrix matrix = new Matrix();
        matrix.postRotate(90); // The camera is literally 90 degress of by rotation and is neither in landscape or portrait orientation


         // was fiddling around with swapping the coordinates and tDhat seems to be cleaner and more optimized.
        imgToCrop = Bitmap.createBitmap(imgToCrop, rectangleCoords[1]- 350,rectangleCoords[0] ,  rectangleCoords[3] , rectangleCoords[2] +100 , matrix, true);

        Drawable d = new BitmapDrawable(getResources(), imgToCrop);
        callButton.setBackground(d); // just for debug and needing to see if it is cropping well.  it is ok, but needs to be better.

        return imgToCrop;
    }
    public void get_text_from_image(Bitmap img){


       TextRecognizer TR = new TextRecognizer.Builder(getApplicationContext()).build();

       Frame frame = new Frame.Builder().setBitmap(img).build();
       SparseArray<TextBlock> items = TR.detect(frame);

       StringBuilder sb = new StringBuilder();

       for (int i =0; i < items.size(); i++){
           TextBlock tb = items.valueAt(i);
           sb.append(tb.getValue());
       }

       phoneNumber = sb.toString();
       phoneNumberText.setText(parsePhoneNumber(phoneNumber));

    }

    //method to parse string to number
    public String parsePhoneNumber(String pn){
        String phNo = pn.replaceAll("[()\\s-]+", "");
        return phNo;
    }


    //method to call number goes here
    public void callNumber(String pn){
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", pn , null)));
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // starts the camera
        cam = Camera.open();

        //Rotates the camera preview to be upright in potrait mode.
        cam.setDisplayOrientation(90);

        /****************************
         * SETTING PARAMETERS LIKE AUTOFOCUS
         */
        Camera.Parameters params = cam.getParameters();
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        cam.setParameters(params);

        try {
            // The surfaceview needs a holder for the image.
            cam.setPreviewDisplay(holder);
            cam.setPreviewCallback(this);
        }catch (Exception e){
            e.printStackTrace();
        }

        // literally will not show up anything of the camera without this.
        cam.startPreview();

    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 10){
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); //Probably don't need
        }
        else{
            Toast.makeText(this,"Provide permission to use camera please." , Toast.LENGTH_LONG).show();
        }
    }

    /**
     * This method is what is gathers the images from the preview and
     * this is where the images are converted to bitmaps, cropped, analyzed for characters and
     * set.
     * @param data
     * @param camera
     */
    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        Camera.Parameters parameters = camera.getParameters();
        int width = parameters.getPreviewSize().width;
        int height = parameters.getPreviewSize().height;

        YuvImage yuv = new YuvImage(data, parameters.getPreviewFormat(), width, height, null);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        yuv.compressToJpeg(new Rect(0, 0, width, height), 50, out);

        byte[] bytes = out.toByteArray();
        Bitmap img = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        // crops the bitmap, but there are qwerks.  Look at the method
        img = cropBitmap(img);

        get_text_from_image(img);

    }


    /******************
     *
     * The methods below here aren't used but needed to be overridden by the SurfaceHolder.Callback
     * interface.
     * */
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
