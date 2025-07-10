package com.example.sih;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.content.SharedPreferences;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "GeofenceBroadcastRcvr";

    @Override
    public void onReceive(Context context, Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = GeofenceStatusCodes.getStatusCodeString(geofencingEvent.getErrorCode());
            Log.e(TAG, "Geofencing error: " + errorMessage);
            return;
        }

        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String currentTime = sdf.format(new Date());

        SharedPreferences prefs = context.getSharedPreferences("GeofencePrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        String transitionType = "";
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            editor.putString("checkInTime", currentTime);
            transitionType = "CHECK_IN";
            Log.d(TAG, "Check-in time set: " + currentTime);
        } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            editor.putString("checkOutTime", currentTime);
            transitionType = "CHECK_OUT";
            Log.d(TAG, "Check-out time set: " + currentTime);
        } else {
            Log.e(TAG, "Invalid geofence transition type: " + geofenceTransition);
            return;
        }

        editor.apply();

        // Broadcast to update UI
        Intent broadcastIntent = new Intent("com.example.geofencecheckinout.UPDATE");
        broadcastIntent.putExtra("type", transitionType);
        broadcastIntent.putExtra("time", currentTime);
        context.sendBroadcast(broadcastIntent);

        List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
        String geofenceTransitionDetails = getGeofenceTransitionDetails(geofenceTransition, triggeringGeofences);
        Log.i(TAG, geofenceTransitionDetails);
    }

    private String getGeofenceTransitionDetails(int geofenceTransition, List<Geofence> triggeringGeofences) {
        String transitionString = getTransitionString(geofenceTransition);
        StringBuilder sb = new StringBuilder();
        for (Geofence geofence : triggeringGeofences) {
            sb.append(geofence.getRequestId()).append(", ");
        }
        return transitionString + ": " + sb.toString();
    }

    private String getTransitionString(int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return "Entered geofence";
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return "Exited geofence";
            default:
                return "Unknown transition";
        }
    }
}