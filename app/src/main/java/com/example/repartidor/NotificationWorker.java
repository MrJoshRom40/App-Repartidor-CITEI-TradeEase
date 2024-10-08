package com.example.repartidor;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.Calendar;

/**
 * Clase de la notificacion persistente de las 6:30pm
 */

public class NotificationWorker extends Worker {

    public static final String CHANNEL_ID = "persistent_notification_channel";
    public static final int NOTIFICATION_ID = 1;

    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
            return Result.success();  // No mostrar notificación en fin de semana
        }
        // Crear la notificación
        showNotification();
        return Result.success();
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

