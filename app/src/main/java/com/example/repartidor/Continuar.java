package com.example.repartidor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import androidx.core.app.NotificationManagerCompat;

public class Continuar extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Continuamos", Toast.LENGTH_SHORT).show();
        String msg = "Hola administrador!\nSoy " + MainActivity.sendName() + ", Te informo que voy a continuar con mi ruta 👍";
        sendMensaje(context, msg);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancel(1); // Cancela la notificación con ID 1
    }

    private void sendMensaje(Context context, String mensaje){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        String phoneNumber = "+523314595049";  // Número de teléfono con código de país
        intent.setData(Uri.parse("https://api.whatsapp.com/send?phone=" + phoneNumber + "&text=" + Uri.encode(mensaje)));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Agrega esta línea para resolver el error

        try {
            context.startActivity(intent);  // Usa context.startActivity() con la bandera añadida
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Debes descargar WhatsApp para poder enviar Reportes", Toast.LENGTH_SHORT).show();

            Intent intent2 = new Intent(Intent.ACTION_VIEW);
            intent2.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.whatsapp"));
            intent2.setPackage("com.android.vending");  // Asegura que solo abra la Play Store
            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Agrega esta línea aquí también
            context.startActivity(intent2);  // Usa context.startActivity() aquí también con la bandera
        }
    }

}

