package com.example.repartidor;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.Calendar;

/**
 * Clase para la notificación persistente a las 6:30 PM
 */
public class NotificationWorker extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        showNotification(context);
    }

    private void showNotification(Context context) {
        String channelId = "default_channel_id";
        String channelName = "Default Channel";

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        // Crear intents para las acciones de los botones
        Intent option1Intent = new Intent(context, Regresar.class);
        PendingIntent option1PendingIntent = PendingIntent.getActivity(context, 0, option1Intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent option2Intent = new Intent(context, Continuar.class);
        PendingIntent option2PendingIntent = PendingIntent.getActivity(context, 1, option2Intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Crear la notificación
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setContentTitle("CITEI TradeEase")
                .setContentText("Oye mi \nchambeador ya\nson las 6:30 ;)")
                .setSmallIcon(R.drawable.citei)
                .setOngoing(true)  // Esto la hace persistente
                .addAction(0, "Regresar", option1PendingIntent)  // Botón de Opción 1
                .addAction(0, "Continuar", option2PendingIntent); // Botón de Opción 2


        // Mostrar la notificación
        notificationManager.notify(1, builder.build());
    }
}