package com.example.jungl.one_click_phone_call;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Benjamin Garrard, Marcos Nino, Quinton Thompson
 * @version 1.0
 */

public class UnitTest {

    MainActivity mainActivity = null;

    /**
     * This sets up the Main Activity
     */
    @Before
    public void setUp() {
        mainActivity = new MainActivity();

    }

    /**
     * This method tests the validity of a phone number.
     * It must be length of 10 and only numbers.
     */
    @Test
    public void checkIfPhoneNumber() {
        String pn1 = "abcdefg";
        String pn2 = "1234";
        String pn3 = "1234567890";

        Boolean pn1Output = mainActivity.checkIfPhoneNumber(pn1);
        Boolean pn2Output = mainActivity.checkIfPhoneNumber(pn2);
        Boolean pn3Output = mainActivity.checkIfPhoneNumber(pn3);

        assertFalse(pn1Output);
        assertFalse(pn2Output);
        assertTrue(pn3Output);

    }

    /**
     *
     *
     */
    @Test
    public void getTextFromImage() {

//        String expected = "(991)721-5235";
//       Bitmap img = BitmapFactory.decodeResource(mainActivity.getApplicationContext().getResources(), R.drawable.one);
//       String output = mainActivity.getTextFromImage(img);
//
//
//        assertEquals(expected,output);

    }

    /**
     * This method tests the parsing of a phone number.
     * Assuming that it is a valid 10 digit number, it
     * removes all symbols from the number.
     */
    @Test
    public void parsePhoneNumber() {
        String number = "(123)456-7890";
        String output;
        String expected = "1234567890";

        output = mainActivity.parsePhoneNumber(number);

        assertEquals(expected,output);
    }

    /**
     *This method clears the MainActivity thus completing
     * the test
     */
    @After
    public void tearDown() {
        mainActivity = null;
    }






}