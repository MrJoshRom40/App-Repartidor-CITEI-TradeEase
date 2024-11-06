package com.example.repartidor;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        // Aquí puedes enviar el nuevo token a tu servidor
        sendTokenToServer(token);
    }

    private void sendTokenToServer(String token) {
        // Implementa el código necesario para enviar el token a tu servidor.
        // Esto es importante para que puedas asociar el dispositivo con el usuario y enviarle notificaciones.
        Log.d("FCM Token", "Token actualizado: " + token);
    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Verifica si el mensaje contiene datos.
        if (!remoteMessage.getData().isEmpty()) {
            // Extrae los datos y muestra la notificación.
            sendNotification(remoteMessage.getData().get("title"), remoteMessage.getData().get("body"));
        }
    }

    private void sendNotification(String title, String messageBody) {
        // Acción 1: Aceptar
        Intent acceptIntent = new Intent(this, Regresar.class);
        PendingIntent acceptPendingIntent = PendingIntent.getBroadcast(this, 0, acceptIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Acción 2: Rechazar
        Intent rejectIntent = new Intent(this, Continuar.class);
        PendingIntent rejectPendingIntent = PendingIntent.getBroadcast(this, 1, rejectIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Crear el canal de notificación (requerido para Android 8.0+)
        String channelId = "default_channel";
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Default Channel", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        // Construir la notificación
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.citei)
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setOngoing(true)
                        .setAutoCancel(false)
                        .addAction(0, "Regresar", acceptPendingIntent)  // Acción Aceptar
                        .addAction(0, "Continuar", rejectPendingIntent)  // Acción Rechazar
                        .setPriority(NotificationCompat.PRIORITY_HIGH);

        notificationManager.notify(0, notificationBuilder.build());
    }
}
