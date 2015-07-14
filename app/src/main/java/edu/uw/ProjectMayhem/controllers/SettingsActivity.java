package edu.uw.ProjectMayhem.controllers;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import edu.uw.ProjectMayhem.R;
import edu.uw.ProjectMayhem.model.LocationServices;
import edu.uw.ProjectMayhem.model.UploadService;


public class SettingsActivity extends ActionBarActivity {

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Intent locationServiceIntent;
    private boolean trackingState;

    // Default intervals in seconds
    private static final int DEFAULT_AC_INTERVAL = 60;

    // Shared preferences names for each slider
    private static final String LOCATION_SLIDER = "location_slider";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        editor = prefs.edit();

        locationServiceIntent = new Intent (this, LocationServices.class);
        trackingState = prefs.getBoolean("tracking", true);

        setupTrackingSwitch();

        final TextView mIntervalUnderText = (TextView) findViewById(R.id.interval_under_text);
        SeekBar mIntervalSeekBar = (SeekBar) findViewById(R.id.location_seek_bar);
        setupSlider(10, 60, mIntervalUnderText, mIntervalSeekBar, DEFAULT_AC_INTERVAL, LOCATION_SLIDER);

        Button mDumpDataButton = (Button) findViewById(R.id.dump_data);
        mDumpDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UploadService.manualUpload(SettingsActivity.this);
            }
        });
    }

    /** Handles setup of the tracking enable/disable switch. */
    private void setupTrackingSwitch() {
        final Switch mTrackingSwitch = (Switch) findViewById(R.id.tracking_switch);
        mTrackingSwitch.setChecked(trackingState);
        if (trackingState) {
            mTrackingSwitch.setText(R.string.tracking_on);
        } else {
            mTrackingSwitch.setText(R.string.tracking_off);
        }
        mTrackingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if (isChecked) {
                    editor.putBoolean("tracking", true);
                    editor.apply();
                    startService(locationServiceIntent);
                    mTrackingSwitch.setText(R.string.tracking_on);
                } else {
                    editor.putBoolean("tracking", false);
                    editor.apply();
                    stopService(locationServiceIntent);
                    mTrackingSwitch.setText(R.string.tracking_off);
                }
            }
        });
    }

    private void setupSlider(final int lessOffset, final int greaterOffset,
                             final TextView underText, SeekBar slider,
                             final int defaultInterval, final String prefsName) {

        slider.setProgress(prefs.getInt((prefsName + "_progress"), 4));
        int storedInterval = prefs.getInt((prefsName + "_interval"), defaultInterval);

        if (storedInterval < 60) {
            underText.setText("Interval: " + storedInterval + " seconds");
        } else if (storedInterval == 60) {
            underText.setText("Interval: 1 minute");
        } else if (storedInterval == 3600) {
            underText.setText("Interval: 1 hour");
        } else if (storedInterval > 3600) {
            underText.setText("Interval: " + (storedInterval / 3600) + " hours");
        } else {
            if ((storedInterval / 60) % 10 == 0) {
                underText.setText("Interval: " + (storedInterval / 60) + " minutes");
            } else {
                underText.setText("Interval: " + (storedInterval / 60) + " minutes");
            }
        }

        slider.setMax(8);
        slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            int interval;
            int prog;

            /** {@inheritDoc} */
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                prog = progress;
                if (progress < 4) {
                    interval = defaultInterval - ((4 - progress) * lessOffset);
                } else if (progress == 4) {
                    interval = defaultInterval;
                } else { // progress > 4
                    interval = defaultInterval + ((progress - 4) * greaterOffset);
                }

                if (interval < 60) {
                    underText.setText("Interval: " + interval + " seconds");
                } else if (interval == 60) {
                    underText.setText("Interval: 1 minute");
                } else if (interval == 3600) {
                    underText.setText("Interval: 1 hour");
                } else if (interval > 3600) {
                    underText.setText("Interval: " + (interval / 3600) + " hours");
                } else {
                    if ((interval / 60) % 10 == 0) {
                        underText.setText("Interval: " + (interval / 60) + " minutes");
                    } else {
                        underText.setText("Interval: " + (interval / 60) + " minutes");
                    }
                }
            }

            /** {@inheritDoc} */
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            /** {@inheritDoc} */
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                editor.putInt((prefsName + "_interval"), interval);
                editor.putInt((prefsName + "_progress"), prog);
                editor.apply();

                if (interval < 60) {
                    Toast.makeText(SettingsActivity.this, ("Interval set to " + interval + " seconds."), Toast.LENGTH_SHORT).show();
                } else if (interval == 60) {
                    Toast.makeText(SettingsActivity.this, "Interval set to 1 minute.", Toast.LENGTH_SHORT).show();
                } else if (interval == 3600) {
                    Toast.makeText(SettingsActivity.this, "Interval set to 1 hour.", Toast.LENGTH_SHORT).show();
                } else if (interval > 3600) {
                    Toast.makeText(SettingsActivity.this,
                            ("Interval set to " + (interval / 3600) + " hours."),
                            Toast.LENGTH_SHORT).show();
                } else {
                    if (((interval / 60) % 10) == 0) {
                        Toast.makeText(SettingsActivity.this,
                                ("Interval set to " + (interval / 60) + " minutes."),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SettingsActivity.this,
                                ("Interval set to " + (interval / 60) + " minutes."),
                                Toast.LENGTH_SHORT).show();
                    }
                }

                stopService(locationServiceIntent);
                startService(locationServiceIntent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_default, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_back) {
            // Same effect as pressing the back button (useful for phones that don't have one)
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
