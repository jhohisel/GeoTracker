/*
 * Copyright (c) 2015. Project Mayhem: Jacob Hohisel, Loralyn Solomon, Brian Plocki, Brandon Soto.
 */

/**
 * Project Mayhem: Jacob Hohisel, Loralyn Solomon, Brian Plocki, Brandon Soto.
 */
package edu.uw.ProjectMayhem.controllers;

import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Solo;

/** This test class tests all pre and post variants of the LoginActivity class as well as the buttons. */
public class LoginActivityTest extends ActivityInstrumentationTestCase2<LoginActivity> {
    private Solo solo;

    /** constructs the LoginActivity */
    public LoginActivityTest() {
        super(LoginActivity.class);
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

    /** Tests to make sure email and password is set and longer than 1 character each. */
    public void testARequiredFields() {
        int i;
        solo.unlockScreen();

        solo.clickOnButton("Login");
        boolean textFound = solo.searchText("This field is required");
        assertTrue("Required fields validation failed", textFound);

        //for a pause
        for (i = 0; i < 10000; i++) {

        }

        solo.enterText(0, "t");
        solo.clickOnButton("Login");
        textFound = solo.searchText("This email address is invalid");
        assertTrue("Login email address failed", textFound);

        //for a pause
        for (i = 0; i < 10000; i++) {

        }

        solo.enterText(0, "est@dummy.com");
        solo.enterText(1, "p");
        solo.clickOnButton("Login");
        textFound = solo.searchText("Password must exceed 5 characters");
        assertTrue("Login password failed", textFound);

        //for a pause
        for (i = 0; i < 10000; i++) {

        }
    }

    /** Tests registration button. */
    public void testBRegisterButton() {
        int i;
        solo.clickOnButton("Register");

        boolean textFound = solo.searchText("Registration");
        assertTrue("Register button failed", textFound);

        //for a pause
        for (i = 0; i < 10000; i++) {

        }
    }

    /** Tests Login button. */
    public void testCLogin() {
        int i;
        solo.enterText(0, "loralyn@uw.edu");
        solo.enterText(1, "password");
        solo.clickOnButton("Login");
        //for a pause
        for (i = 0; i < 10000; i++) {

        }
        boolean textFound = solo.searchText("loralyn@uw.edu has signed in!");
        assertTrue("Login failed", textFound);

    }

    /** Tests reset password button. */
    public void testDResetPasswordButton() {
        int i;
        solo.clickOnButton("Forgot Password");

        boolean textFound = solo.searchText("Reset Password");
        assertTrue("Reset password button failed", textFound);

    }

}
