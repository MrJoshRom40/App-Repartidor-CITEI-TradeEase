package com.example.repartidor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

import Global.PedidosAsignados;
import Pojo.Pedido;

public class AdaptadorPedidos extends RecyclerView.Adapter<AdaptadorPedidos.PedidoViewHolder> {

    public Context context;

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


}
