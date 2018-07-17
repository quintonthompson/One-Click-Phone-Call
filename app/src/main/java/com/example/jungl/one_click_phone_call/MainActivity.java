package com.example.jungl.one_click_phone_call;



import android.net.Uri;
import android.os.Bundle;
import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

public class MainActivity extends AppCompatActivity {

    ImageView i1;
    Button b1;
    TextView t1;
    String pn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        i1 = (ImageView)findViewById(R.id.the_image);
        b1 = (Button)findViewById(R.id.one_click_button);
       // t1 = (TextView) findViewById(R.id.phone_text);

        b1.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                //opens camera on click, but need to make it open with the app
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,0);
                //get_text_from_image(v);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        Bitmap bitmap = (Bitmap)data.getExtras().get("data");
        i1.setImageBitmap(bitmap);



    }

    public void get_text_from_image(View v){

       Bitmap bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.one);
       TextRecognizer TR = new TextRecognizer.Builder(getApplicationContext()).build();

       if(!TR.isOperational()){
           Toast.makeText(getApplicationContext(),"could not open text recognizer.", Toast.LENGTH_SHORT).show();
       }
       else{
           Frame frame = new Frame.Builder().setBitmap(bitmap).build();
           SparseArray<TextBlock> items = TR.detect(frame);

           StringBuilder sb = new StringBuilder();

           for (int i =0; i < items.size(); i++){
               TextBlock tb = items.valueAt(i);
               sb.append(tb.getValue());
           }
           
           pn = sb.toString();
           t1.setText(parsePhoneNumber(pn));

           //call method to call number
           callNumber(parsePhoneNumber(pn));
       }
    }

    //method to parse string to number
    public String parsePhoneNumber(String pn){
        String phNo = pn.replaceAll("[()\\s-]+", "");
        System.out.println(phNo);
        return phNo;
    }
    //method to call number goes here
    public void callNumber(String pn){
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", pn , null)));
    }

}
