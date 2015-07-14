package edu.uw.ProjectMayhem.model;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Represents an asynchronous task to sumbit a location update
 */
public class DataUploadTask extends AsyncTask<Void, Void, String> {

    private String webURL = "http://450.atwebpages.com/logAdd.php";
    private double latitude;
    private double longitude;
    private double speed;
    private double heading;
    private String userid;
    private long timestamp;

    public DataUploadTask(double lat, double lon, double speed, double heading, String uid, long time) {
        super();
        latitude = lat;
        longitude = lon;
        this.speed = speed;
        this.heading = heading;
        userid = uid;
        timestamp = time;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String doInBackground(Void... params) {

        String result = "";

        HttpURLConnection connection;

        URL url;
        String parameters = ("?lat=" + latitude
                + "&lon=" + longitude
                + "&speed=" + speed
                + "&heading=" + heading
                + "&source=" + userid
                + "&timestamp=" + timestamp);
        try {
            Log.d("MyAccountActivity", "Sending url: " + webURL + parameters);
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

        } catch (IOException e) {
            System.err.println("Something bad happened while sending HTTP GET");
        }

        return result;
    }
}