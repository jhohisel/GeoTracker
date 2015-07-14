/*
 * Copyright (c) 2015. Project Mayhem: Jacob Hohisel, Loralyn Solomon, Brian Plocki, Brandon Soto.
 */

/**
 * Project Mayhem: Jacob Hohisel, Loralyn Solomon, Brian Plocki, Brandon Soto.
 */
package edu.uw.ProjectMayhem.controllers;

import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Solo;
/** Test class for ResetActivity functionality. */
public class ResetActivityTest extends ActivityInstrumentationTestCase2<ResetActivity> {
    private Solo solo;

    /** constructs the ResetActivity. */
    public ResetActivityTest() {
        super(ResetActivity.class);
    }

    /** sets up conditions prior to each test. */
    @Override
    public void setUp() throws Exception {
        super.setUp();
        solo = new Solo(getInstrumentation(), getActivity());
    }

    /** resets conditions following each test. */
    @Override
    public void tearDown() throws Exception {
        //tearDown() is run after a test case has finished.
        //finishOpenedActivities() will finish all the activities that have been opened during the test execution.
        solo.finishOpenedActivities();
    }

    /** Tests non-email of the ResetActivity. */
    public void testANonEmail() {
        int i;
        solo.unlockScreen();


        //for a pause
        for (i = 0; i < 10000; i++) {

        }

        solo.enterText(0, "t");
        solo.clickOnButton("Reset Password");
        boolean textFound = solo.searchText("Please enter a valid email.");
        assertTrue("", textFound);

        //for a pause
        for (i = 0; i < 10000; i++) {

        }

        solo.enterText(0, "est@dummy.com");
        solo.clickOnButton("Reset Password");
        textFound = solo.searchText("Email not found.");
        assertTrue("", textFound);

        //for a pause
        for (i = 0; i < 10000; i++) {

        }
    }

    /** Tests registered email of the ResetActivity. */
    public void testBEmail() {
        int i;
        solo.unlockScreen();


        //for a pause
        for (i = 0; i < 10000; i++) {

        }

        solo.enterText(0, "loralyn@uw.edu");
         solo.clickOnButton("Reset Password");

         //for a pause
         for (i = 0; i < 10000; i++) {

         }
         boolean textFound = solo.searchText("An email with instructions");
         assertTrue("Reset password failed", textFound);
    }

}
