package com.example.repartidor;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import Global.PedidosAsignados;
import Pojo.Conexion;
import Pojo.Pedido;

public class Continuar extends BroadcastReceiver {

    private static final String CHANNEL_ID = "CITEI_Channel"; // Canal para la notificación
    private static final int NOTIFICATION_ID = 2; // ID único para la notificación
    private UbcationLocater ubcationLocater;
    private Conexion conexion = new Conexion();

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancel(1); // Cancela la notificación con ID 1
        if(PedidosAsignados.Pedidos.isEmpty()){
            Toast.makeText(context, "Ya has terminado con tus pedidos es hora de regresar", Toast.LENGTH_SHORT).show();
            goToCITEI(context);
        } else {
            for(int i = 1; i < PedidosAsignados.Pedidos.size(); i++){
                Pedido p = PedidosAsignados.Pedidos.get(i);
                actualizarEstadoPedido(context, p.getNumeroDeVenta());
            }
        }
        PedidosAsignados.Pedidos.clear();
        String msg = "Hola administrador!\nSoy " + MainActivity.sendName() + ", Te informo que voy a continuar con mi ruta 👍";
        sendMensaje(context, msg);
        String ultimoPedido = PedidosAsignados.Pedidos.get(0).getDireccion();
        ubcationLocater = new UbcationLocater(PedidosAsignados.Pedidos.get(0).getLongitudPedido(),PedidosAsignados.Pedidos.get(0).getLatitudPedido(),context,Carga_Descarga.class, PedidosAsignados.Pedidos.get(0).getNumeroDeVenta(),true);
        ubcationLocater.startTracking();
        showNotification(ultimoPedido, context);
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

    private void showNotification(String ultimopedido, Context context) {
        // Crear el canal de notificación (para Android 8+)
        createNotificationChannel(context);

        // Intent para abrir Google Maps con la dirección
        Uri gmmIntentUri =  Uri.parse("google.navigation:q=" + ultimopedido);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        // PendingIntent para manejar el clic en la notificación
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                mapIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Construir la notificación
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.citei) // Icono de la notificación
                .setContentTitle("Notificación para Continuar")
                .setContentText("Haz clic para dirigirnos al ultimo pedido.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent) // Asociar el PendingIntent
                .setAutoCancel(true); // Desaparece al hacer clic

        // Mostrar la notificación
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Canal CITEI";
            String description = "Canal para notificaciones de regreso";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void actualizarEstadoPedido(Context context, String numVenta) {
        // URL del archivo PHP en tu servidor
        String url = conexion.getURL_BASE() + "pedidosP.php?NumVenta=" + numVenta;

        // Crear una instancia de RequestQueue
        RequestQueue queue = Volley.newRequestQueue(context);

        // Crear la solicitud StringRequest
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("Respuesta del servidor: " + response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Manejar errores
                        System.out.println("Error: " + error.getMessage());
                    }
                }
        );

        // Agregar la solicitud a la cola
        queue.add(stringRequest);
    }

    private void goToCITEI(Context context) {
        ubcationLocater = new UbcationLocater("-103.41672251159841","20.66433005362719",context, MainActivity.class, "Formulario2",true);
        ubcationLocater.startTracking();

        String uri = "google.navigation:q=" + Uri.encode("Diplomáticos 4716, Jardines de Guadalupe, 45030 Zapopan, Jal.");
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Añadir el flag necesario
        context.startActivity(intent);
    }


}

