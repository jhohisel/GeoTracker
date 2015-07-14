/*
 * Copyright (c) 2015. Project Mayhem: Jacob Hohisel, Loralyn Solomon, Brian Plocki, Brandon Soto.
 */

/**
 * Project Mayhem: Jacob Hohisel, Loralyn Solomon, Brian Plocki, Brandon Soto.
 */
package edu.uw.ProjectMayhem.controllers;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import edu.uw.ProjectMayhem.R;
import edu.uw.ProjectMayhem.model.LocationServices;

/**
 *
 * Class Login
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends ActionBarActivity implements LoaderCallbacks<Cursor> {
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    /** Where the user types in the email address. */
    private AutoCompleteTextView mEmail;

    /** Where the user types in the password. */
    private EditText mPassword;

    /** Shows progress for signing in. */
    private View mProgressView;

    /** log in form view. */
    private View mLoginFormView;

    /** onCreate() initiates the actions of LoginActivity. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set up the login form.
        mEmail = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();

        mPassword = (EditText) findViewById(R.id.password);
        mEmail = (AutoCompleteTextView) findViewById(R.id.email);

        Button mLoginButton = (Button) findViewById(R.id.email_sign_in_button);
        mLoginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        Button mRegisterButton = (Button) findViewById(R.id.register_button);
        mRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                register(view);
            }
        });

        Button mForgotButton = (Button) findViewById(R.id.forgot_button);
        mForgotButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                reset(view);
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        onReceive(this,this.getIntent());
    }

    public void onReceive(Context context, Intent intent) {

        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = this.registerReceiver(null, ifilter);

        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;

        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

// 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage(batteryStatus.toString())
                .setTitle("Power Check");

// 3. Get the AlertDialog from create()
        AlertDialog dialog = builder.create();

        Toast toast = Toast.makeText(this, batteryStatus.toString() + isCharging, Toast.LENGTH_LONG);
        toast.show();


    }

    /** Starts the "reset password" screen. */
    private void reset(View view) {
        Intent resetIntent = new Intent(this, ResetActivity.class);
        startActivity(resetIntent);
    }

    /** Starts the register screen. */
    private void register(View view) {
        Intent registerIntent = new Intent(this, RegistrationActivity.class);
        startActivity(registerIntent);
    }

    /** {@inheritDoc} */
    private void populateAutoComplete() {
        getLoaderManager().initLoader(0, null, this);
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {

        // Stopping any previous services
        Intent location = new Intent(this, LocationServices.class);
        stopService(location);

        Log.d("login", "ATTEMPING LOGIN");
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmail.setError(null);
        mPassword.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPassword.setError(getString(R.string.error_invalid_password));
            focusView = mPassword;
            cancel = true;
        }


        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmail.setError(getString(R.string.error_field_required));
            focusView = mEmail;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmail.setError(getString(R.string.error_invalid_email));
            focusView = mEmail;
            cancel = true;
        }

        if (cancel) {

            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();

        } else {

            // Kick off a background task to
            // perform the user login attempt.
            UserLoginTask task = new UserLoginTask();
            task.execute();
            String response = "";

            try {
                response = task.get();
            } catch (Exception e) {
                System.err.println("Something bad happened");
            }

            System.out.println("response: " + response);

            if (response != null) {
                try {

                    JSONObject o = new JSONObject(response);

                    System.out.println("Response: " + o.get("result"));

                    if(o.get("result").equals("success")) {
                        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                        SharedPreferences.Editor spe = prefs.edit();
                        spe.putString("uid", o.get("userid").toString());
                        spe.apply();
                        doLogin();
                    } else {
                        showProgress(false);
                        Toast.makeText(this, o.get("error").toString(), Toast.LENGTH_LONG).show();
                        System.out.println("Error: " + o.get("error"));
                    }

                } catch (JSONException e) {
                    System.out.println("JSON Exception " + e);
                }

            }
        }
    }

    /** generates the login features. */
    private void doLogin() {
        // If login is successful, switch to MyAccountActivity
        Toast.makeText(this, mEmail.getText().toString()
                + " has signed in!", Toast.LENGTH_SHORT).show();

        Intent accountIntent = new Intent(this, MyAccountActivity.class);
        startActivity(accountIntent);
        finish();
    }

    /** {@inheritDoc} */
    private boolean isEmailValid(String email) {
        return (email.contains("@") && email.contains("."));
    }

    /** {@inheritDoc} */
    private boolean isPasswordValid(String password) {
        return password.length() > 5;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /** {@inheritDoc} */
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    /** {@inheritDoc} */
    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<String>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    /** {@inheritDoc} */
    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    /** {@inheritDoc} */
    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /** {@inheritDoc} */
    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmail.setAdapter(adapter);
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, String> {

        private String webURL = "http://450.atwebpages.com/login.php";

        /** {@inheritDoc} */
        @Override
        protected void onPreExecute() {
            showProgress(true);
        }

        /** {@inheritDoc} */
        @Override
        protected String doInBackground(Void... params) {

            String result = "";

            HttpURLConnection connection;

            URL url = null;
            String response = null;
            String parameters = ("?email=" + mEmail.getText().toString()
                                +"&password=" + mPassword.getText().toString());
            try
            {
                url = new URL(webURL + parameters);
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setRequestMethod("GET");

                InputStreamReader isr = new InputStreamReader(connection.getInputStream());
                BufferedReader reader = new BufferedReader(isr);

                result = reader.readLine();

                isr.close();
                reader.close();

            }
            catch(IOException e)
            {
                System.err.println("Something bad happened");
            }

            return result;
        }

        /** {@inheritDoc} */
        @Override
        protected void onPostExecute(final String result) {
            showProgress(false);
        }

        /** {@inheritDoc} */
        @Override
        protected void onCancelled() {

            mAuthTask = null;
            showProgress(false);

        }
    }
}



