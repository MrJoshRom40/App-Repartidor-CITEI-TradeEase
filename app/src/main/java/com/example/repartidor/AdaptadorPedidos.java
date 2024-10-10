package com.example.repartidor;

import static androidx.core.content.ContextCompat.startActivity;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

import Global.PedidosAsignados;
import Pojo.Pedido;

public class AdaptadorPedidos extends RecyclerView.Adapter<AdaptadorPedidos.PedidoViewHolder> {

    public Context context;
    private static final String CHANNEL_ID = "canal_ejemplo";  // ID del canal de notificación
    private static final int NOTIFICATION_ID = 1;  // ID de la notificación

    @NonNull
    @Override
    public AdaptadorPedidos.PedidoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View v = View.inflate(context, R.layout.pedido_info, null);
        PedidoViewHolder obj = new PedidoViewHolder(v);
        return obj;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull AdaptadorPedidos.PedidoViewHolder pedidoViewHolder, int i){
        final int pos = i;

        pedidoViewHolder.nombre.setText(PedidosAsignados.Pedidos.get(i).getNombrecliente());
        pedidoViewHolder.direccion.setText(PedidosAsignados.Pedidos.get(i).getDireccion());
        pedidoViewHolder.telefono.setText(PedidosAsignados.Pedidos.get(i).getTelefono());
        pedidoViewHolder.numventa.setText(PedidosAsignados.Pedidos.get(i).getNumeroDeVenta());

        pedidoViewHolder.direccion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String direccionPedido = pedidoViewHolder.direccion.getText().toString();
                navigateToLocation(direccionPedido);
                showNotification();
            }
        });

        switch (PedidosAsignados.Pedidos.get(i).getEstadoDelpedido()){
            case "Foraneo":{
                pedidoViewHolder.carta.setCardBackgroundColor(context.getResources().getColor(R.color.morado));
                break;
            }
            case "Pendiente":{
                pedidoViewHolder.carta.setCardBackgroundColor(context.getResources().getColor(R.color.rojo));
                break;
            }
            default:{
                pedidoViewHolder.carta.setCardBackgroundColor(context.getResources().getColor(R.color.azul));
                break;
            }
        }

    }

    @Override
    public int getItemCount() {
        return PedidosAsignados.Pedidos.size();
    }

    public class PedidoViewHolder extends RecyclerView.ViewHolder{
        TextView nombre, direccion, telefono, numventa;
        CardView carta;
        public PedidoViewHolder(@NonNull View itemView){
            super(itemView);
            nombre = itemView.findViewById(R.id.Nombre_pedido);
            direccion = itemView.findViewById(R.id.Direccion_pedido);
            telefono = itemView.findViewById(R.id.Telefono_pedido);
            numventa = itemView.findViewById(R.id.NumVenta_pedido);
            carta = itemView.findViewById(R.id.Pedidocard);
        }
    }

    private void navigateToLocation(String location){
        String uri = "google.navigation:q=" + Uri.encode(location);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps");
        context.startActivity(intent);

    }

    @SuppressLint("MissingPermission")
    private void showNotification() {
        createNotificationChannel(); // Asegúrate de crear el canal de notificaciones

        Intent intent = new Intent(context, Inicio.class); // Intent para abrir Inicio.java
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.citei) // Reemplaza con tu icono de notificación
                .setContentTitle("CITEI TradeEase")
                .setContentText("Tienes Problemas\nHaz clic en mi para regresar. 😊")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setOngoing(true) // Para hacer que la notificación sea persistente
                .setAutoCancel(true); // No se cancela al hacer clic

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Canal Ejemplo";
            String description = "Descripción del canal";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
