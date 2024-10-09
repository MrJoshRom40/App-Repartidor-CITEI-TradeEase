package com.example.repartidor;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.Calendar;

/**
 * Clase de la notificacion persistente de las 6:30pm f
 */

public class NotificationWorker extends Service {

    public static final String CHANNEL_ID = "persistent_notification_channel";
    public static final int NOTIFICATION_ID = 1;

    // ...

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent1 = new Intent(this, NotificationWorker.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent1, PendingIntent.FLAG_IMMUTABLE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 18); // 6 PM
        calendar.set(Calendar.MINUTE, 30);
        calendar.set(Calendar.SECOND, 0);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        showNotification();
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void showNotification() {
        Context context = getApplicationContext();

        // Crear el canal de notificación (para Android 8.0 y superior)
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Notificación Persistente",
                NotificationManager.IMPORTANCE_HIGH
        );
        NotificationManager manager = context.getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);

        // Crear intents para las acciones de los botones
        Intent option1Intent = new Intent(context, Regresar.class);
        PendingIntent option1PendingIntent = PendingIntent.getActivity(context, 0, option1Intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent option2Intent = new Intent(context, Continuar.class);
        PendingIntent option2PendingIntent = PendingIntent.getActivity(context, 1, option2Intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Crear la notificación
        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("CITEI TradeEase")
                .setContentText("Oye mi \nchambeador ya\nson las 6:30 ;)")
                .setSmallIcon(R.drawable.citei)
                .setOngoing(true)  // Esto la hace persistente
                .addAction(0, "Regresar", option1PendingIntent)  // Botón de Opción 1
                .addAction(0, "Continuar", option2PendingIntent)  // Botón de Opción 2
                .build();

        // Mostrar la notificación
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }
}

