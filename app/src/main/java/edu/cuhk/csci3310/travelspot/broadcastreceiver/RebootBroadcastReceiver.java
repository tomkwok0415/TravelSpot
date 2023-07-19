package edu.cuhk.csci3310.travelspot.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.content.ContextCompat;

import edu.cuhk.csci3310.travelspot.service.ForegroundLocationService;


public class RebootBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // start the location tracking service after reboot
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Intent serviceIntent = new Intent(context, ForegroundLocationService.class);
            ContextCompat.startForegroundService(context, serviceIntent);
        }
    }

}
