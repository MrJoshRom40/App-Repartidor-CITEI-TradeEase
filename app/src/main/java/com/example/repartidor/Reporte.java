package com.example.repartidor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import Pojo.Conexion;

public class Reporte extends AppCompatActivity {

    EditText numv, cant, coment;
    Button sendreporte;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reporte);

        numv = findViewById(R.id.numeroventa_reporte);
        cant = findViewById(R.id.cantidad_reporte);
        coment = findViewById(R.id.comentarios_reporte);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        sendreporte = findViewById(R.id.Send_reporte);
        sendreporte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.isEditTextEmpty(numv) || MainActivity.isEditTextEmpty(cant)){
                    Toast.makeText(Reporte.this, "Hay que llenar todos los campos requeridos", Toast.LENGTH_SHORT).show();
                } else{
                    String mensaje = "Hola administrador!\nSoy " + MainActivity.sendName() +
                            ", te informo que en el pedido: " + numv.getText().toString() + "\nSe mandaron " + cant.getText().toString() +
                            "\n\n" + coment.getText().toString();
                    sendMensaje(mensaje);
                }

            }
        });
    }

    private void sendMensaje(String mensaje){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        String phoneNumber = "+523314595049";  // Número de teléfono con código de país
        intent.setData(Uri.parse("https://api.whatsapp.com/send?phone=" + phoneNumber + "&text=" + Uri.encode(mensaje)));
        try {
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Debes descargar WhatsApp para poder enviar Reportes", Toast.LENGTH_SHORT).show();
            Intent intent2 = new Intent(Intent.ACTION_VIEW);
            intent2.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.whatsapp"));
            intent2.setPackage("com.android.vending");  // Asegura que solo abra la Play Store
            startActivity(intent2);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {//funcion para importar nuestro menu
        getMenuInflater().inflate(R.menu.menu,menu);//Metodo para acceder a nuestro menu por medio de nuestro xml principal
        //los parametros son en donde se encuentra y el nombre del xml
        return super.onCreateOptionsMenu(menu);//metodo que retorna si es que se llamo al menu y manda a llamar a la funcion de abajo
    }

    private void checkDescanso(){
        Conexion conexion = new Conexion();
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
                                Intent c = new Intent(Reporte.this, Descanso.class);
                                startActivity(c);
                            } else {
                                // Descanso es igual a 1, mostrar mensaje
                                Toast.makeText(Reporte.this, "Ya se ha usado el descanso", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(Reporte.this, "Error al procesar la respuesta", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Reporte.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                    }
                });

        // Agregar la solicitud a la cola
        queue.add(stringRequest);
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        if(item.getItemId()==R.id.Problema){
            Intent c = new Intent(this, Problema.class);//creo mi objeto cambio y lo igualo a un constructor el cual recibe por parametros el contexto y el lugar a donde va
            startActivity(c);
            finish();
        }

        if(item.getItemId()==R.id.Reporte){
            Toast.makeText(this, "Ya estas redactando un Reporte", Toast.LENGTH_SHORT).show();
        }

        if(item.getItemId()==R.id.Descanso){
            checkDescanso();
        }

        if(item.getItemId()==R.id.Salir){
            Toast.makeText(this, "Adios " + MainActivity.sendName(), Toast.LENGTH_SHORT).show();
            Intent c = new Intent(this, MainActivity.class);//creo mi objeto cambio y lo igualo a un constructor el cual recibe por parametros el contexto y el lugar a donde va
            startActivity(c);
            finish();
        }

        return super.onOptionsItemSelected(item);//metodo que retorna la opcion que se selecciono
    }
}