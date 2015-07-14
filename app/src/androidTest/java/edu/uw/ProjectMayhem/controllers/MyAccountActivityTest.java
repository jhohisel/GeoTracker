/*
 * Copyright (c) 2015. Project Mayhem: Jacob Hohisel, Loralyn Solomon, Brian Plocki, Brandon Soto.
 */

/**
 * Project Mayhem: Jacob Hohisel, Loralyn Solomon, Brian Plocki, Brandon Soto.
 */
package edu.uw.ProjectMayhem.controllers;

import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Solo;

/** This test class tests all pre and post variants of the MyAccountActivity class as well as the buttons. */
public class MyAccountActivityTest extends ActivityInstrumentationTestCase2<MyAccountActivity> {
    private Solo solo;

    /** constructs the MyAccountActivity */
    public MyAccountActivityTest() {
        super(MyAccountActivity.class);
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

    /** Tests for the date range. */
    public void testARange() {
        int i;
        solo.unlockScreen();

        solo.clickOnButton(0);
        solo.setDatePicker(0, 2015, 4, 20);
        solo.clickOnButton("OK");
        boolean textFound = solo.searchText("05-20-2015");
        assertTrue("Start date failed!", textFound);

        //for a pause
        for (i = 0; i < 10000; i++) {

        }

        solo.clickOnButton(1);
        solo.setDatePicker(0, 2015, 5, 2);
        solo.clickOnButton("OK");
        textFound = solo.searchText("06-02-2015");
        assertTrue("End date failed!", textFound);

        //for a pause
        for (i = 0; i < 10000; i++) {

        }
        solo.clickOnButton(2);

        //for a pause
        for (i = 0; i < 10000; i++) {

        }

        solo.assertCurrentActivity("MyTrajectory button failed", MyTrajectoryActivity.class);
    }


}
