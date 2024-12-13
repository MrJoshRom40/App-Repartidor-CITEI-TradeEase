package com.example.repartidor;

import static Global.PedidosAsignados.Pedidos;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Calendar;

import Global.PedidosAsignados;
import Pojo.Pedido;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String CHANNEL_ID = "your_channel_id";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        Calendar calendar = Calendar.getInstance();
        int hora = calendar.get(Calendar.HOUR_OF_DAY);
        int minutos = calendar.get(Calendar.MINUTE);
        if(hora == 9 && minutos < 30){
            notificacionInicio();
        } else{
            for(Pedido p : Pedidos){
                if(p.getEsForaneo().equals("Sí")){
                    return;
                }
                notificacionFinal();
            }
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

    private void notificacionFinal(){
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

    private void notificacionInicio(){
        try {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.citei)
                    .setContentTitle("CITEI TradeEase")
                    .setContentText("Hola mi chambeador, hora de iniciar! 😎")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setOngoing(false);

            // Verificar permisos y mostrar la notificación
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                notificationManager.notify(2, builder.build());
            } else {
                Log.w("MyFirebaseMessagingService", "No se otorgaron permisos para las notificaciones.");
                // Aquí puedes manejar la solicitud de permisos si es necesario
            }
            // Usamos Handler para iniciar la actividad en el hilo principal
            new Handler(Looper.getMainLooper()).post(() -> {
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("Fomulario", "Formulario1P1");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  // Importante para iniciar actividad desde servicio
                startActivity(intent);
            });
        } catch (Exception e) {
            Log.e("MyFirebaseMessagingService", "Error al mostrar la notificación: " + e.getMessage());
        }
    }
}
