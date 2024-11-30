package com.example.repartidor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Global.PedidosAsignados;
import Pojo.Conexion;
import Pojo.Pedido;

public class Inicio extends AppCompatActivity {

    RecyclerView rv;
    Toolbar toolbar;

    private Conexion conexion = new Conexion();

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

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {//funcion para importar nuestro menu
        getMenuInflater().inflate(R.menu.menu,menu);//Metodo para acceder a nuestro menu por medio de nuestro xml principal
        //los parametros son en donde se encuentra y el nombre del xml
        return super.onCreateOptionsMenu(menu);//metodo que retorna si es que se llamo al menu y manda a llamar a la funcion de abajo
    }
//smn
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        if(item.getItemId()==R.id.Problema){
            Intent c = new Intent(this, Problema.class);//creo mi objeto cambio y lo igualo a un constructor el cual recibe por parametros el contexto y el lugar a donde va
            startActivity(c);
            PedidosAsignados.Pedidos.clear();
        }

        if(item.getItemId()==R.id.Reporte){
            Intent c = new Intent(this, Reporte.class);//creo mi objeto cambio y lo igualo a un constructor el cual recibe por parametros el contexto y el lugar a donde va
            startActivity(c);
            PedidosAsignados.Pedidos.clear();
        }

        if(item.getItemId()==R.id.Descanso){
            checkDescanso();
            PedidosAsignados.Pedidos.clear();
        }

        if(item.getItemId()==R.id.Salir){
            Toast.makeText(this, "Adios " + MainActivity.sendName(), Toast.LENGTH_SHORT).show();
            Intent c = new Intent(this, MainActivity.class);//creo mi objeto cambio y lo igualo a un constructor el cual recibe por parametros el contexto y el lugar a donde va
            startActivity(c);
            PedidosAsignados.Pedidos.clear();
            finish();
        }

        return super.onOptionsItemSelected(item);//metodo que retorna la opcion que se selecciono
    }



    private void checkDescanso(){
        String url = conexion.getURL_BASE() + "descanso.php?Nomina=" + MainActivity.sendNomina(); // Cambia esta URL según corresponda

        // Crear una instancia de RequestQueue
        RequestQueue queue = Volley.newRequestQueue(this);

        // Crear una solicitud de tipo StringRequest
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            // Parsear la respuesta JSON
                            JSONObject jsonObject = new JSONObject(response);
                            boolean descanso = jsonObject.getBoolean("descanso");

                            // Verificar si descanso es true o false
                            if (descanso) {
                                // Descanso es distinto de 1, iniciar actividad
                                Intent c = new Intent(Inicio.this, Descanso.class);
                                startActivity(c);
                                finish();
                            } else {
                                // Descanso es igual a 1, mostrar mensaje
                                Toast.makeText(Inicio.this, "Ya se ha usado el descanso", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(Inicio.this, "Error al procesar la respuesta", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Inicio.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                    }
                });

        // Agregar la solicitud a la cola
        queue.add(stringRequest);
    }




}