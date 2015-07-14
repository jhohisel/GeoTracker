package edu.uw.ProjectMayhem.data;
/*
 * Copyright (c) 2015. Project Mayhem: Jacob Hohisel, Loralyn Solomon, Brian Plocki, Brandon Soto.
 */

/**
 * Project Mayhem: Jacob Hohisel, Loralyn Solomon, Brian Plocki, Brandon Soto.
 */
public class MovementData {

    /** latitude data. */
    private double mLatitude;

    /** longitude data. */
    private double mLongitude;

    /** speed data. */
    private double mSpeed;

    /** heading data. */
    private double mHeading;

    /** user id string. */
    private String mUserId;

    /** time stamp data. */
    private long mTimeStamp;

    /** Generates movement data */
    public MovementData() {}

    /** MovementData() constructor. */
    public MovementData(double latitude, double longitude, double speed, double heading, String userId, long timeStamp) {
        mLatitude = latitude;
        mLongitude = longitude;
        mSpeed = speed;
        mHeading = heading;
        mUserId = userId;
        mTimeStamp = timeStamp;
    }

    /** retrieves latitude. */
    public double getLatitude() {
        return mLatitude;
    }

    /** sets latitude. */
    public void setLatitude(double mLatitude) {
        this.mLatitude = mLatitude;
    }

    /** retrieves longitude. */
    public double getLongitude() {
        return mLongitude;
    }

    /** sets longitude. */
    public void setLongitude(double mLongitude) {
        this.mLongitude = mLongitude;
    }

    /** retrieves speed. */
    public double getSpeed() {
        return mSpeed;
    }

    /** sets speed. */
    public void setSpeed(double mSpeed) {
        this.mSpeed = mSpeed;
    }

    /** retrieves heading. */
    public double getHeading() {
        return mHeading;
    }

    /** sets heading. */
    public void setHeading(double mHeading) {
        this.mHeading = mHeading;
    }

    /** retrieves user id. */
    public String getUserId() {
        return mUserId;
    }

    /** sets user id. */
    public void setUserId(String mUserId) {
        this.mUserId = mUserId;
    }

    /** retrieves time stamp. */
    public long getTimeStamp() {
        return mTimeStamp;
    }

    /** sets time stamp. */
    public void setTimeStamp(long mTimeStamp) {
        this.mTimeStamp = mTimeStamp;
    }
}
