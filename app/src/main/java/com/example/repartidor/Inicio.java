package com.example.repartidor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import Global.PedidosAsignados;

public class Inicio extends AppCompatActivity {

    RecyclerView rv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        rv = findViewById(R.id.rvpedidos);

        AdaptadorPedidos ap = new AdaptadorPedidos();
        ap.context = this;
        LinearLayoutManager llm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv.setAdapter(ap);
        rv.setLayoutManager(llm);
    }
}