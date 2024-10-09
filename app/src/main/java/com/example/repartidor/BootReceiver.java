package com.example.repartidor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            // Volver a programar las notificaciones
            new MainActivity().scheduleNotification(); // Llama a tu método para programar
        }
    }
}
