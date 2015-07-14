package edu.uw.ProjectMayhem.controllers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import edu.uw.ProjectMayhem.model.UploadService;

/**
 * Created by Brian on 6/1/2015.
 */
public class NetworkBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Intent upload = new Intent(context, UploadService.class);

        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnected();

        if (isConnected) {
            context.startService(upload);
            //////// debugging stuff //////////////////////////////////////////////////////////////////
            Log.d("*********", "NetworkBroadcastReceiver onReceive called!");
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
                            .setContentTitle("GeoTracker")
                            .setContentText("--Network Service connected!");
            NotificationManager mNotificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(2, mBuilder.build());
            ///////////////////////////////////////////////////////////////////////////////////////////
        } else {
            context.stopService(upload);
            //////// debugging stuff //////////////////////////////////////////////////////////////////
            Log.d("*********", "NetworkBroadcastReceiver onReceive called!");
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
                            .setContentTitle("GeoTracker")
                            .setContentText("--Network Service not connected!");
            NotificationManager mNotificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(3, mBuilder.build());
            ///////////////////////////////////////////////////////////////////////////////////////////

        }
    }
}
