package com.example.repartidor;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationWorker extends BroadcastReceiver {

    private static final String CHANNEL_ID = "canal_ejemplo";  // ID del canal de notificación
    private static final int NOTIFICATION_ID = 1;  // ID de la notificación

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("com.example.repartidor.SHOW_NOTIFICATION".equals(intent.getAction())) {
            // Llamar al método para mostrar la notificación
            crearCanalDeNotificacion(context);
            showNotification(context);
        }
    }

    // Método para crear el canal de notificaciones
    private void crearCanalDeNotificacion(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {  // Solo en Android 8.0 (API 26) o superior
            CharSequence nombre = "Canal Ejemplo";
            String descripcion = "Descripción del canal de ejemplo";
            int importancia = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel canal = new NotificationChannel(CHANNEL_ID, nombre, importancia);
            canal.setDescription(descripcion);

            // Registrar el canal en el sistema
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(canal);
            }
        }
    }

    // Método para mostrar la notificación
    @SuppressLint("MissingPermission")
    private void showNotification(Context context) {
        // Intent para la acción "Regresar"
        Intent regresarIntent = new Intent(context, Regresar.class);
        PendingIntent regresarPendingIntent = PendingIntent.getBroadcast(
                context, 0, regresarIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Intent para la acción "Continuar"
        Intent continuarIntent = new Intent(context, Continuar.class);
        PendingIntent continuarPendingIntent = PendingIntent.getBroadcast(
                context, 1, continuarIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Construir la notificación
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.citei)  // Icono de la notificación
                .setContentTitle("Acción requerida")  // Título de la notificación
                .setContentText("Elige una acción")  // Texto de la notificación
                .setPriority(NotificationCompat.PRIORITY_HIGH)  // Prioridad de la notificación
                .addAction(0, "Regresar", regresarPendingIntent)  // Acción "Regresar"
                .addAction(0, "Continuar", continuarPendingIntent) // Acción "Continuar"
                .setOngoing(true);

        // Mostrar la notificación
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(NOTIFICATION_ID, builder.build());  // Mostrar la notificación con un ID
    }
}
