/*
 * Copyright (c) 2015. Project Mayhem: Jacob Hohisel, Loralyn Solomon, Brian Plocki, Brandon Soto.
 */

/**
 * Project Mayhem: Jacob Hohisel, Loralyn Solomon, Brian Plocki, Brandon Soto
 */
package edu.uw.ProjectMayhem.controllers;

import android.app.DatePickerDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import edu.uw.ProjectMayhem.R;
import edu.uw.ProjectMayhem.data.MovementDBHandler;
import edu.uw.ProjectMayhem.data.MovementData;
import edu.uw.ProjectMayhem.model.LocationServices;

/**
 * Displays the user's account information.
 */
public class MyAccountActivity extends ActionBarActivity implements View.OnClickListener {
    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("MM-dd-yyyy", Locale.US);

    private TextView mStartDateText;
    private TextView mEndDateText;
    private Button mStartButton;
    private Button mEndButton;
    private DatePickerDialog mStartDateDialog;
    private DatePickerDialog mEndDateDialog;
    private Calendar mStartCalendar;
    private Calendar mEndCalendar;

    private Intent locationServiceIntent;

    /**
     * onCreate() generates MyAccountActivity
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        registerReceiver(new NetworkBroadcastReceiver(), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isTracking = prefs.getBoolean("tracking", true);

        if (isTracking) {
            // Start the service
            locationServiceIntent = new Intent(this, LocationServices.class);
            startService(locationServiceIntent);
        }

        ComponentName receiver = new ComponentName(this, LocationBroadcastReceiver.class);
        PackageManager pm = this.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        final String uid = prefs.getString("uid", "");
        Log.d("MyAccountActivity", "User id is:" + uid);

        mStartCalendar = Calendar.getInstance();
        mEndCalendar = Calendar.getInstance();

        mStartDateText = (TextView) findViewById(R.id.start_date_textview);
        mEndDateText = (TextView) findViewById(R.id.end_date_textview);

        mStartButton = (Button) findViewById(R.id.start_date_button);
        mEndButton = (Button) findViewById(R.id.end_date_button);

        Button mTrajectoryButton = (Button) findViewById(R.id.trajectory_button);
        mTrajectoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myTrajectory(view);
            }
        });

        setupDateDialogs();
    }

    private void setupDateDialogs() {
        final Calendar initial_calendar = Calendar.getInstance();
        final String start = getString(R.string.start_date);
        final String end = getString(R.string.end_date);


        mStartDateDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mStartCalendar.set(year, monthOfYear, dayOfMonth);
                mStartDateText.setText(start + ": " + DATE_FORMATTER.format(mStartCalendar.getTime()));
            }
        }, initial_calendar.get(Calendar.YEAR), initial_calendar.get(Calendar.MONTH),
                initial_calendar.get(Calendar.DAY_OF_MONTH));

        mEndDateDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mEndCalendar.set(year, monthOfYear, dayOfMonth);
                mEndDateText.setText(end + ": " + DATE_FORMATTER.format(mEndCalendar.getTime()));
            }
        }, initial_calendar.get(Calendar.YEAR), initial_calendar.get(Calendar.MONTH),
                initial_calendar.get(Calendar.DAY_OF_MONTH));
    }


    /**
     * Transitions to the trajectory screen.
     */
    private void myTrajectory(View view) {
        final Intent trajectoryIntent = new Intent(this, MyTrajectoryActivity.class);
        trajectoryIntent.putExtra("Start Date", mStartCalendar.getTime());
        trajectoryIntent.putExtra("End Date", mEndCalendar.getTime());

        startActivity(trajectoryIntent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_my_account, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_settings:
                showSettings();
                return true;
            case R.id.action_logout:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /** Displays the settings activity. */
    private void showSettings() {
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        startActivity(settingsIntent);
    }

    /** Logs out the user and stops tracking. */
    private void logout() {

        // Stop tracking
        stopService(locationServiceIntent);
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor spe = prefs.edit();
        spe.putBoolean("tracking", false);
        spe.apply();

        // Go to the login page
        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

    @Override
    public void onClick(View v) {
        if (v == mStartButton) {
            mStartDateDialog.show();
        } else if (v == mEndButton) {
            mEndDateDialog.show();
        }
    }
}
