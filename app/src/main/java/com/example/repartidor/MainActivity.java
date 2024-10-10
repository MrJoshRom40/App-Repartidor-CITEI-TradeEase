package com.example.repartidor;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import Global.PedidosAsignados;
import Pojo.Pedido;

public class MainActivity extends AppCompatActivity {

    private EditText usuario, contra;
    private Button logear;

    public Repartidor repartidor = new Repartidor();

    private static final String CHANNEL_ID = "persistent_notification_channel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        usuario = findViewById(R.id.usr);
        contra = findViewById(R.id.pass);
        logear = findViewById(R.id.login);

        // Programar la notificación al iniciar la actividad
        //scheduleNotification();

        logear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nomina = usuario.getText().toString().trim();
                String clave = contra.getText().toString().trim();
                login(nomina, clave);
            }
        });
    }





    private void login(String nomina, String clave) {
        String url = "http://192.168.0.254/citei/Login.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");

                    if (status.equals("success")) {
                        JSONObject userData = jsonObject.getJSONObject("user_data");
                        String nomina = userData.getString("nomina");
                        String nombre = userData.getString("nombre");

                        repartidor.setNombre(nombre);
                        repartidor.setNomina(nomina);

                        cargar(repartidor.getNomina());

                        Toast.makeText(MainActivity.this, "Bienvenido " + repartidor.getNombre(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, Inicio.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Error al procesar los datos", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Error en la conexión", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Enviar los parámetros a través de POST
                Map<String, String> params = new HashMap<>();
                params.put("nomina", nomina);
                params.put("clave", clave);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void cargar(String nomina) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://192.168.0.254/citei/Pedidos.php?nomina=" + nomina;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Response", response);
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    PedidosAsignados.Pedidos.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        Pedido pedido = new Pedido();
                        pedido.setNombrecliente(obj.getString("NombreCompleto"));
                        pedido.setDireccion(obj.getString("DireccionCompleta"));
                        pedido.setTelefono(obj.getString("Telefono"));
                        pedido.setNumeroDeVenta(obj.getString("NumeroVenta"));
                        pedido.setEstadoDelpedido(obj.getString("EstadoPedido"));
                        PedidosAsignados.Pedidos.add(pedido);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Error al procesar los datos", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        queue.add(stringRequest);
    }
}
