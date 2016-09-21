package com.ravendmaster.linearmqttdashboard.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Андрей on 15.05.2016.
 */
public class Autostart extends BroadcastReceiver
{
    public void onReceive(Context context, Intent arg1)
    {
        Intent intent = new Intent(context, MQTTService.class);
        intent.setAction("autostart");
        context.startService(intent);
        Log.d(getClass().getName(), "started");
    }
}