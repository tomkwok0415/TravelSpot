package edu.cuhk.csci3310.travelspot.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

import edu.cuhk.csci3310.travelspot.manager.DatabaseManager;
import edu.cuhk.csci3310.travelspot.manager.NotificationManager;
import edu.cuhk.csci3310.travelspot.ui.map.MapFragment;
import edu.cuhk.csci3310.travelspot.model.Spot;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "GeofenceBReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = new NotificationManager(context);
        DatabaseManager databaseManager = new DatabaseManager(context);

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if (geofencingEvent.hasError()) {
            Log.d(TAG, "onReceive: Error receiving geofence event...");
            return;
        }

        // if current location going in the geofence then send notification
        int transitionType = geofencingEvent.getGeofenceTransition();
        if (transitionType == Geofence.GEOFENCE_TRANSITION_ENTER) {
            List<Geofence> geofenceList = geofencingEvent.getTriggeringGeofences();

            for (Geofence geofence: geofenceList) {
                Log.d(TAG, "Spot " + geofence.getRequestId() + " entering");
                String ID = geofence.getRequestId();
                Spot spot = databaseManager.getSpot(Integer.valueOf(ID));

                notificationManager.sendRemindNotification(spot.getLocation(), spot.getTitle(), MapFragment.class);
            }
        }

    }
}
