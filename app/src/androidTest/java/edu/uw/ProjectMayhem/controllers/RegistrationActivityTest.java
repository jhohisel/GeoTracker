/*
 * Copyright (c) 2015. Project Mayhem: Jacob Hohisel, Loralyn Solomon, Brian Plocki, Brandon Soto.
 */

/**
 * Project Mayhem: Jacob Hohisel, Loralyn Solomon, Brian Plocki, Brandon Soto.
 */
package edu.uw.ProjectMayhem.controllers;

import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Solo;

/** This test class tests the pre and post conditions for the RegistrationActivity fields and buttons. */
public class RegistrationActivityTest extends ActivityInstrumentationTestCase2<RegistrationActivity> {
    private Solo solo;

    /** constructs the RegistrationActivity. */
    public RegistrationActivityTest() {
        super(RegistrationActivity.class);
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

    /** Tests the required fields of the RegistrationActivity. */
    public void testARequiredFields() {
        int i;
        solo.unlockScreen();

        solo.clickOnButton("Register");
        boolean textFound = solo.searchText("Not a valid email.");
        assertTrue("Required fields validation failed", textFound);

        //for a pause
        for (i = 0; i < 10000; i++) {

        }

        solo.enterText(0, "t");
        solo.clickOnButton("Register");
        textFound = solo.searchText("Not a valid email.");
        assertTrue("Registration email address failed", textFound);

        //for a pause
        for (i = 0; i < 10000; i++) {

        }

        solo.enterText(0, "est@dummy.com");
        solo.enterText(1, "p");
        solo.clickOnButton("Register");
        textFound = solo.searchText("The password is too short.");
        assertTrue("Registration password failed", textFound);

        //for a pause
        for (i = 0; i < 10000; i++) {

        }

        solo.enterText(1, "assword");
        solo.enterText(2, "p");
        solo.clickOnButton("Register");
        textFound = solo.searchText("Passwords don't match!");
        assertTrue("Registration password confirmation failed", textFound);

        //for a pause
        for (i = 0; i < 10000; i++) {

        }

        solo.enterText(2, "assword");
        solo.enterText(3, "security answer");
        solo.clickOnButton("Register");

        //for a pause
        for (i = 0; i < 10000; i++) {

        }

        solo.clickOnButton("Decline");
        solo.assertCurrentActivity("Agreement failed", LoginActivity.class);

        //for a pause
        for (i = 0; i < 30000; i++) {

        }

    }

    /** Test the accept button. */
    public void testBAcceptButton() {
        int i;
        solo.enterText(0, "test@dummy.com");
        solo.enterText(1, "password");
        solo.enterText(2, "password");
        solo.enterText(3, "security answer");
        solo.clickOnButton("Register");

        //for a pause
        for (i = 0; i < 10000; i++) {

        }

        solo.clickOnButton("I Agree");
        solo.assertCurrentActivity("Agreement failed", LoginActivity.class);
    }

    /** Tests the orientation of the app. */
    public void testCOrientation() {
        int i;
        solo.clickOnButton("Register");
        solo.enterText(0, "test@dummy.com");
        solo.enterText(1, "password");
        solo.enterText(2, "password");
        solo.enterText(3, "security answer");

        solo.setActivityOrientation(Solo.LANDSCAPE);
        boolean textFound = solo.searchText("test@dummy.com");
        assertTrue("Orientation change failed", textFound);

        //for a pause
        for (i = 0; i < 10000; i++) {

        }

        solo.setActivityOrientation(Solo.PORTRAIT);
        textFound = solo.searchText("test@dummy.com");
        assertTrue("Orientation change failed", textFound);

        //for a pause
        for (i = 0; i < 10000; i++) {

        }
    }

    /** Test Login button. */
    public void testDLoginButton(){
        int i;

        //for a pause
        for (i = 0; i < 10000; i++) {

        }
        solo.clickOnButton("Login");

        //for a pause
        for (i = 0; i < 10000; i++) {

        }

        solo.assertCurrentActivity("Login button failed", LoginActivity.class);

    }
 }
