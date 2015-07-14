package edu.uw.ProjectMayhem.controllers;
/*
 * Copyright (c) 2015. Project Mayhem: Jacob Hohisel, Loralyn Solomon, Brian Plocki, Brandon Soto.
 */

/**
 * Project Mayhem: Jacob Hohisel, Loralyn Solomon, Brian Plocki, Brandon Soto.
 */

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import edu.uw.ProjectMayhem.R;

/** RegistrationActivity generates the registration features of app. */
public class RegistrationActivity extends ActionBarActivity {

    /** Where user types in email. */
    private EditText mEmailText;

    /** Where user types in password. */
    private EditText mPasswordText;

    /** Password confirmation. */
    private EditText mConfirmPasswordText;

    /** Stores all possible security questions. */
    private Spinner mSecuritySpinner;

    /** Where user types in answer to security question. */
    private EditText mAnswerText;

    /** onCreate() generates features of RegistrationActivity. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mEmailText = (EditText) findViewById(R.id.email);
        mPasswordText = (EditText) findViewById(R.id.password);
        mConfirmPasswordText = (EditText) findViewById(R.id.password_confirm);
        mAnswerText = (EditText) findViewById(R.id.answer_field);

        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.security_questions, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);

        mSecuritySpinner = (Spinner) findViewById(R.id.spinner);
        mSecuritySpinner.setAdapter(adapter);

        final Button mRegisterButton = (Button) findViewById(R.id.email_register_button);
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Attempts to register the user and then switch to login screen.
             */
            @Override
            public void onClick(final View v) {

                final View theView = v;

                if (!isEmailValid(mEmailText.getText().toString())) {

                    mEmailText.setError("Not a valid email.");
                    mEmailText.requestFocus();

                } else if (!isPasswordValid(mPasswordText.getText().toString())) {

                    mPasswordText.setError("The password is too short.");
                    mPasswordText.requestFocus();

                } else if (!mPasswordText.getText().toString().equals(mConfirmPasswordText.getText().toString())) {

                    mConfirmPasswordText.setError("Passwords don't match!");
                    mConfirmPasswordText.requestFocus();

                } else if (!isAnswerValid(mAnswerText.getText().toString())) {

                    mAnswerText.setError("Please enter an answer.");
                    mAnswerText.requestFocus();

                } else {

                    // Display agreement, and only register to server if agreed, else exit

                    AgreementWebTask agreeTask = new AgreementWebTask();
                    agreeTask.execute();

                    String result = "";
                    StringBuffer newResult = new StringBuffer();

                    try {
                        result = agreeTask.get();
                    } catch (Exception e) {
                        System.out.println("Failed to get result from user agreement web task");
                    }

                    System.out.println("Response: " + result);

                    if (result != null) {

                        newResult = new StringBuffer(result);
                        result = newResult.substring(15, newResult.length() - 3);
                        System.out.println(result);

                    }

                    AlertDialog.Builder agreement = new AlertDialog.Builder(RegistrationActivity.this);
                    agreement
                            .setTitle("User Agreement")
                            .setMessage(Html.fromHtml(result))
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton("I Agree", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface agreement, int which) {
                                    // Register to server
                                    RegisterWebTask task = new RegisterWebTask();
                                    task.execute();

                                    String response = "";

                                    try {
                                        response = task.get();
                                    } catch (Exception e) {
                                        System.out.println("Something bad happened");
                                    }

                                    System.out.println("Response: " + response);

                                    if (response != null) {
                                        try {

                                            JSONObject o = new JSONObject(response);

                                            System.out.println("Response: " + o.get("result"));

                                            if (o.get("result").equals("success")) {
                                                Toast.makeText(RegistrationActivity.this,
                                                        ("Registration successful!" + o.get("message")), Toast.LENGTH_LONG).show();
                                                login(theView);
                                            } else {
                                                //showProgress(false);
                                                Toast.makeText(RegistrationActivity.this, o.get("error").toString(), Toast.LENGTH_LONG).show();
                                                System.out.println("Error: " + o.get("error"));
                                            }

                                        } catch (JSONException e) {
                                            System.out.println("JSON Exception " + e);
                                        }

                                    }

                                }
                            })
                            .setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface agreement, int which) {
                                   // System.exit(0);
                                    login(v);
                                }
                            })
                            .show();
                }
            }
        });

        final Button mSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Switches to login screen.
             */
            @Override
            public void onClick(View view) {
                login(view);
            }
        });

    }

    /**
     * Goes to login page.
     */
    private void login(View view) {
        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

    /** {@inheritDoc} */
    private boolean isPasswordValid(String password) {
        return password.length() > 5;
    }

    /** {@inheritDoc} */
    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isAnswerValid(String answer) {
        return answer.length() > 0;
    }

    /** {@inheritDoc} */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_default, menu);
        return true;
    }

    /** {@inheritDoc} */
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

    /**
     * Destorys this activity and saves user data.
     */
    @Override
    protected void onDestroy() {
        Log.d("registrationActivity", "ON DESTROY CALLED!");
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        final SharedPreferences.Editor editor = prefs.edit();
        editor.putString("email", mEmailText.getText().toString());
        editor.putString("password", mPasswordText.getText().toString());
        editor.putString("answer", mAnswerText.getText().toString());
        editor.putString("security", mSecuritySpinner.getSelectedItem().toString());
        editor.apply();

        super.onDestroy();
    }

    /**
     * Registers the new user to the web server.
     */
    private class RegisterWebTask extends AsyncTask<String, Void, String> {

        /** Register URL */
        private String webURL = "http://450.atwebpages.com/adduser.php";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... urls) {

            String encodedQuestion = mSecuritySpinner.getSelectedItem().toString().replaceAll(" ", "%20");
            String encodedAnswer = mAnswerText.getText().toString().replaceAll(" ", "%20");
            String result = "";

            HttpURLConnection connection;
            URL url;

            String parameters = ("?email=" + mEmailText.getText().toString()
                                + "&password=" + mPasswordText.getText().toString())
                                + "&question=" + encodedQuestion
                                + "&answer=" + encodedAnswer;
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
                System.err.println("Something bad happened in sending HTTP GET request");
            }

            return result;
        }
    }

    /**
     * Registers the new user to the web server.
     */
    private class AgreementWebTask extends AsyncTask<String, Void, String> {

        /** Register URL */
        private String webURL = "http://450.atwebpages.com/agreement.php";

        /** {@inheritDoc} */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /** {@inheritDoc} */
        @Override
        protected String doInBackground(String... urls) {

            String result = "";

            HttpURLConnection connection;
            URL url;

            try {
                url = new URL(webURL);
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setRequestMethod("GET");

                InputStreamReader isr = new InputStreamReader(connection.getInputStream());
                BufferedReader reader = new BufferedReader(isr);

                String line = "";

                while((line = reader.readLine()) != null) {
                    result += line;
                }


                isr.close();
                reader.close();

            } catch (IOException e) {
                System.err.println("Something bad happened in sending HTTP GET request");
            }

            return result;
        }
    }
}
