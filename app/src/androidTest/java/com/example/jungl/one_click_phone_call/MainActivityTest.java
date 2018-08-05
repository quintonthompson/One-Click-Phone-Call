package com.example.jungl.one_click_phone_call;


import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;


import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * @author Benjamin Garrard, Marcos Nino, Quinton Thompson
 * @version 1.0
 */

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    /**
     *This rule is set to test the activity of the app.
     */
    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    private MainActivity mActivity = null;

    /**
     * This sets up the Main Activity of the app
     */
    @Before
    public void setUp() {

        mActivity = mActivityTestRule.getActivity();


    }

    /**
     * This tests the UI of the Main Activity.
     * It verifies that the views are shown.
     */
    @Test
    public void testLaunch(){
       View surface = mActivity.findViewById(R.id.surfaceView2);
       View rectangleView = mActivity.findViewById(R.id.myRectangleView);
       View callButton = mActivity.findViewById(R.id.callButton);
       View phoneNumberText = mActivity.findViewById(R.id.phoneText);
       View phoneNumberText2 = mActivity.findViewById(R.id.phoneText2);

       assertNotNull(surface);
       assertNotNull(rectangleView);
       assertNotNull(callButton);
       assertNotNull(phoneNumberText);
       assertNotNull(phoneNumberText2);

    }

    /**
     * This method tests that the application
     * can access the phone application.
     */
    @Test
    public void callNumber(){
        String pn = "1234567890";
        mActivity.callNumber(pn);
    }

    /**
     *This method clears the MainActivity thus completing
     * the test
     */
    @After
    public void tearDown() {
        mActivity = null;
    }

}