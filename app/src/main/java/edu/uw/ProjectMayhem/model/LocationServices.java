/*
 * Copyright (c) 2015. Project Mayhem: Jacob Hohisel, Loralyn Solomon, Brian Plocki, Brandon Soto.
 */

/**
 * Project Mayhem: Jacob Hohisel, Loralyn Solomon, Brian Plocki, Brandon Soto.
 */
package edu.uw.ProjectMayhem.model;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;

import java.text.DateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import edu.uw.ProjectMayhem.controllers.LoginActivity;
import edu.uw.ProjectMayhem.data.MovementDBHandler;
import edu.uw.ProjectMayhem.data.MovementData;

/**
 * Creates location information for app.
 */
public class LocationServices extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener

{
    /** simple name of class */
    protected static final String TAG = LocationServices.class.getSimpleName();

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    // Gathers the location on the set interval
    private static final int POLL_INTERVAL = 60000; //60 seconds

    private SharedPreferences prefs;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    protected LocationRequest mLocationRequest;

    /**
     * Represents user's current location.
     */
    protected Location mCurrentLocation;

    /** uid string. */
    private String uid;

    /** The timer to update the location. */
    private Timer locationTimer;

    /**
     * Tracks the status of the location updates request. Value changes when the user presses the
     * Start Updates and Stop Updates buttons.
     */
    protected Boolean mRequestingLocationUpdates = false;

    /** context for this class. */
    private static Context mContext;

    /**
     * Time when the location was updated represented as a String.
     */
    protected String mLastUpdateTime;

    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LocalBinder extends Binder {
        LocationServices getService() {
            return LocationServices.this;
        }
    }

    /** onCreate() initializes LocationServices. */
    @Override
    public void onCreate() {
        super.onCreate();
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                android.os.Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        mContext = getApplicationContext();

        Log.d(TAG, "Location Service starting up!");

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        uid = prefs.getString("uid", "");
        Log.d("MyAccountActivity", "User id is:" + uid);

        // Display a notification to the user
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mContext)
                        .setSmallIcon(android.R.drawable.ic_menu_mylocation)
                        .setContentTitle("GeoTracker")
                        .setContentText("Tracking started");
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(mContext, LoginActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(LoginActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());

        if (getContext() != null) {
            buildGoogleApiClient();
        }
        // Start the location update timer
        locationTimer = new Timer();
        locationTimer.scheduleAtFixedRate(new LocationUpdate(), 0, (prefs.getInt("location_slider_interval", 60) * 1000));
        Log.d("LocationInterval", ("Location timer started with interval: " + prefs.getInt("location_slider_interval", 60) + " seconds"));
    }

    /** {@inheritDoc} */
    @Override
    public void onDestroy() {
        locationTimer.cancel();
        onStop();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    // This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
    private final IBinder mBinder = new LocalBinder();

    /** retrieves the context. */
    public static Context getContext() {
        return mContext;
    }

    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the
     * LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        Log.i(TAG, "Building GoogleApiClient");

        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(com.google.android.gms.location.LocationServices.API)
                .build();

        createLocationRequest();
    }

    /**
     * Sets up the location request. Android has two location request settings:
     * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
     * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml.
     * <p/>
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     * <p/>
     * These settings are appropriate for mapping applications that show real-time location
     * updates.
     */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        // also available:
        //          LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        //          LocationRequest.PRIORITY_LOW_POWER
        //          LocationRequest.PRIORITY_NO_POWER
    }

    /**
     * Requests location updates from the FusedLocationApi.
     */
    protected void startLocationUpdates() {
        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        com.google.android.gms.location.LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    protected void stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.

        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        com.google.android.gms.location.LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    /** onResume() resumes receiving location updates. */
    public void onResume() {
        // Within {@code onPause()}, we pause location updates, but leave the
        // connection to GoogleApiClient intact.  Here, we resume receiving
        // location updates if the user has requested them.

        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    /** action when location services is paused. */
    protected void onPause() {
        // Stop location updates to save battery, but don't disconnect the GoogleApiClient object.
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
    }

    /** action when location services is stopped. */
    protected void onStop() {
        mGoogleApiClient.disconnect();
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "Connected to GoogleApiClient");

        // If the user presses the Start Updates button before GoogleApiClient connects, we set
        // mRequestingLocationUpdates to true (see startUpdatesButtonHandler()). Here, we check
        // the value of mRequestingLocationUpdates and if it is true, we start location updates.
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }

    }

    /**
     * Callback that fires when the location changes.
     */
    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
    }

    /** action when connection is lost. */
    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    /** action when connection fails. */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    /** action when start command is selected. */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        super.onStartCommand(intent, flags, startId);
        Log.i(TAG, "service starting");
        return START_STICKY;
    }

    /** Updates location on a timer. */
    public class LocationUpdate extends TimerTask {

        MovementDBHandler locationDatabase = new MovementDBHandler(getContext());
        private int id;

        /** LocationUpdate() constructor. */
        public LocationUpdate() {
            super();
            mRequestingLocationUpdates = true;
            mGoogleApiClient.connect();
        }

        /** Runs the LocationUpdate features. */
        @Override
        public void run() {

            // If the initial location was never previously requested, we use
            // FusedLocationApi.getLastLocation() to get it. If it was previously requested, we store
            // its value in the Bundle and check for it in onCreate(). We
            // do not request it again unless the user specifically requests location updates by pressing
            // the Start Updates button.
            //
            // Because we cache the value of the initial location in the Bundle, it means that if the
            // user launches the activity,
            // moves to a new location, and then changes the device orientation, the original location
            // is displayed as the activity is re-created.
            if (mCurrentLocation == null) {
                mCurrentLocation = com.google.android.gms.location.LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
            }

            if (mCurrentLocation != null) {
                Log.d(TAG, "Longitude is: " + mCurrentLocation.getLongitude());
                Log.d(TAG, "Latitude is: " + mCurrentLocation.getLatitude());
                locationDatabase.addMovement(new MovementData(mCurrentLocation.getLatitude(),
                        mCurrentLocation.getLongitude(),
                        mCurrentLocation.getSpeed(),
                        mCurrentLocation.getBearing(),
                        uid,
                        (System.currentTimeMillis() / 1000)));
            }

        }
    }

}
