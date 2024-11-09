package com.example.repartidor;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String CHANNEL_ID = "your_channel_id";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        try {
            // Crear los intents para las acciones
            Intent regresar = new Intent(this, Regresar.class);
            PendingIntent action1PendingIntent = PendingIntent.getBroadcast(
                    this, 0, regresar, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            Intent continuar = new Intent(this, Continuar.class);
            PendingIntent action2PendingIntent = PendingIntent.getBroadcast(
                    this, 0, continuar, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            // Construir la notificación
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.citei)
                    .setContentTitle("CITEI TradeEase")
                    .setContentText("Hola mi chambeador, ya son las 6:30 pm 😎")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setOngoing(true)
                    .addAction(0, "Regresar", action1PendingIntent)
                    .addAction(0, "Continuar", action2PendingIntent);

            // Verificar permisos y mostrar la notificación
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                notificationManager.notify(1, builder.build());
            } else {
                Log.w("MyFirebaseMessagingService", "No se otorgaron permisos para las notificaciones.");
                // Aquí puedes manejar la solicitud de permisos si es necesario
            }
        } catch (Exception e) {
            Log.e("MyFirebaseMessagingService", "Error al mostrar la notificación: " + e.getMessage());
        }
    }

    // Método para crear el canal de notificación
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Notificaciones de CITEI";
            String description = "Canal para notificaciones de CITEI";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}
