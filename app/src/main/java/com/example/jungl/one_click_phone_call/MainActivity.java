package com.example.jungl.one_click_phone_call;



import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.ByteArrayOutputStream;

/**
 * @author Benjamin Garrard, Marcos Nino, Quinton Thompson
 * @version 1.0
 */
public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback, Camera.PreviewCallback{


    Button callButton;
    String phoneNumber;
    SurfaceView surface;

    SurfaceHolder surfaceHolder;
    Camera cam;
    View rectangleView;
    TextView phoneNumberText;
    Bitmap imgToCrop;
    TextView phoneNumberText2;

    int[] rectangleCoords = new int[4];

    /**
     * This method is used to create the layout and is responsible
     * for linking the views with logic and other methods.
     *
     * @param savedInstanceState the instance of the activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.setTitle("Pic to Phone Call");

        //###### Finding the Views associated with the MainActivity layout #########
        surface = (SurfaceView)findViewById(R.id.surfaceView2);
        rectangleView = (View)findViewById(R.id.myRectangleView);
        callButton = (Button)findViewById(R.id.callButton);
        phoneNumberText = (TextView)findViewById(R.id.phoneText);
        phoneNumberText2 = (TextView)findViewById(R.id.phoneText2);
        //###### END finding views

        //#### Setting the button to listen to an event and perform logic ###########
        callButton.setOnClickListener(new View.OnClickListener(){
            /**
             * This method is nested, overridden, and is being used anonymously.  There is
             * no need to document it in JavaDoc.
             *
             * This method is used when the call button to get the phone number of the text
             * that is within the rectangle. This method sends the phone number to
             * the phone application if it senses the number to be an adequate phone number and
             * then tells the user it is sending the number.
             */
            @Override
            public void onClick(View v) {

                phoneNumber = getTextFromImage(cropBitmap(imgToCrop));


                if (checkIfPhoneNumber(phoneNumber)){
                    callNumber(phoneNumber);
                    phoneNumberText.setText(phoneNumberText2.getText() + " sent to phone application.");
                }
                else{
                    phoneNumberText.setText("Not a possible number try again.");
                }

            }
        });
        //#### END Button setting

        //#### Requesting permission to use the users camera
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
        //#### END Requesting permission
    }

    /**
     * This method does a minor check to see if the number sent to it could possibly be a phone
     * number.  This check just makes sure that the number can not be less than 10 digits.
     *
     * @param phoneNum the phone number taken from the image.
     * @return the result of the phone number test
     */
    public boolean checkIfPhoneNumber(String phoneNum){
        boolean result;

        if (phoneNum.length() >= 10){
            result = true;
        }
        else{
            result = false;
        }

        return result;
    }

    /**
     * This method is used to crop the incoming images from the surface view that
     * is constantly looking through the camera.  This method gathers the coordinates of
     * the rectangle view and crops the incoming images relative to the rectangle view.
     * This method also rotates the image 90 degrees to make the image upright.
     *
     * @param img the image that should come from the surface view
     * @return the cropped image and rotated image.
     */
    public Bitmap cropBitmap(Bitmap img){

        // Gathering coordinates from the rectangle that is with the layout.xml file.
        rectangleView.getLocationOnScreen(rectangleCoords);
        rectangleCoords[2] = rectangleView.getWidth();
        rectangleCoords[3] = rectangleView.getHeight();

        // Need to rotate the image, for debug, and because the vision api works better
        // when it is oriented normally.
        Matrix matrix = new Matrix();
        matrix.postRotate(90); // The camera is literally 90 degress of by rotation and is neither in landscape or portrait orientation

         // was fiddling around with swapping the coordinates and that seems to be cleaner and more optimized.
        img = Bitmap.createBitmap(img, rectangleCoords[1] - 250 , rectangleCoords[0] - 100,  rectangleCoords[3] - 40 , rectangleCoords[2], matrix, true);


        //DEBUG CODE
//        Drawable d = new BitmapDrawable(getResources(), img);
//        callButton.setBackground(d); // just for debug and needing to see if it is cropping well.  it is ok, but needs to be better.
        //DEBUG CODE

        return img;
    }

    /**
     * This method uses Google's mobile vision API for the Optical Character Recognition (OCR).
     * Once the image has been inferred by the API this method builds the predictions character
     * by character until the string is complete and returns the text it found.
     *
     * @param img the cropped image that should have come from the cropBitmap method
     * @return the text that was predicted
     */
    public String getTextFromImage(Bitmap img){

       TextRecognizer TR = new TextRecognizer.Builder(getApplicationContext()).build();

       Frame frame = new Frame.Builder().setBitmap(img).build();
       SparseArray<TextBlock> items = TR.detect(frame);

       StringBuilder sb = new StringBuilder();

       for (int i =0; i < items.size(); i++){
           TextBlock tb = items.valueAt(i);
           sb.append(tb.getValue());
       }

       phoneNumber = sb.toString();

        return phoneNumber;
    }

    /**
     * This method removes the majority of characters that are not digits and returns what
     * is left.
     *
     * @param pn the text that should be been predicted by using OCR
     * @return the parse text with only characters.
     */
    public String parsePhoneNumber(String pn){
        String phNo = pn.replaceAll("[()\\s-]+", "");

        phNo = phNo.replaceAll("[^\\d.]", "");
        return phNo;
    }


    /**
     * This method sends the phone number that has been predicted and parsed over to the phone
     * application.
     *
     * @param pn the predicted and parsed phone number using OCR
     */
    public void callNumber(String pn){
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", pn , null)));
    }

    /**
     * This method is a created from the Surface.Callback interface.
     * This method is called once when the surface view is created.
     * This method opens the camera, sets its parameters, and begins the
     * camera preview display that the user will be viewing on the layout.
     *
     * @param holder that is used to manipulate the surface view
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // starts the camera
        cam = Camera.open();

        //Rotates the camera preview to be upright in portrait mode.
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


    /**
     * This method is required for requesting permission to use the camera.  This method
     * is from the application itself.
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
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
     * This method is overridden from the Preview.callBack interface.
     *
     * This method is what is gathers the images from the camera preview and
     * this is where the images are converted to bitmaps, cropped, analyzed for characters and
     * set.
     *
     * @param data the image data from the camera
     * @param camera the hardware camera
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
        imgToCrop = img;

        // For a different version of the app uncomment the below
        img = cropBitmap(img);

        phoneNumber = getTextFromImage(img);
        phoneNumberText2.setText(parsePhoneNumber(phoneNumber));

    }

    /**
     * Overridden method from SurfaceHolder.CallBack interface.  This method is unused.
     * */
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    /**
     * Overridden method from SurfaceHolder.CallBack interface.  This method is unused.
     * */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
