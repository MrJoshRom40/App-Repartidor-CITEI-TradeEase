package com.example.repartidor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import Global.PedidosAsignados;
import Pojo.Conexion;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class Problema extends AppCompatActivity {

    EditText problematxt;
    Button sendproblema;
    Toolbar toolbar;

    String ubicacion; // Variable para almacenar la ubicación
    private LocationManager locationManager;
    private LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problema);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        problematxt = findViewById(R.id.Txt_problema);
        sendproblema = findViewById(R.id.Send_problema);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ubicacion = "Ubicación no disponible";

        sendproblema.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(Problema.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // Solicitar permisos si no están concedidos
                ActivityCompat.requestPermissions(Problema.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                return;
            }
            if(MainActivity.isEditTextEmpty(problematxt)){
                new SweetAlertDialog(Problema.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Error")
                        .setContentText("Debes de redactar un problema para enviar un problema")
                        .show();
            } else{
                String mensaje = "Hola administrador!\nSoy " + MainActivity.sendName() +
                        ", te informo que he tenido un problema:\n" + problematxt.getText().toString() + "\nEstoy en: " + ubicacion;

                new SweetAlertDialog(Problema.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Alerta")
                        .setContentText("¿Estas seguro de reportarle el siguiente problema al administrador?\n'" + mensaje + "'")
                        .setConfirmText("Enviar")
                        .setCancelText("Cancelar")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();
                                sendMensaje(mensaje);
                            }
                        })
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();
                            }
                        })
                        .show();
            }
        });

        // Configurar el LocationListener
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                ubicacion = "https://www.google.com/maps?q=" + latitude + "," + longitude;
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(String provider) {}

            @Override
            public void onProviderDisabled(String provider) {}
        };

        // Solicitar actualizaciones de ubicación si los permisos están concedidos
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            iniciarLocationUpdates();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

    }

    private void iniciarLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

            // Obtener la última ubicación conocida
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocation != null) {
                double latitude = lastKnownLocation.getLatitude();
                double longitude = lastKnownLocation.getLongitude();
                ubicacion = "https://www.google.com/maps?q=" + latitude + "," + longitude;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                iniciarLocationUpdates();
            } else {
                Toast.makeText(this, "Permisos de ubicación denegados", Toast.LENGTH_SHORT).show();
            }
        }
    }
    //smn

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
                                Intent c = new Intent(Problema.this, Descanso.class);
                                startActivity(c);
                            } else {
                                // Descanso es igual a 1, mostrar mensaje
                                Toast.makeText(Problema.this, "Ya se ha usado el descanso", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(Problema.this, "Error al procesar la respuesta", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Problema.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                    }
                });

        // Agregar la solicitud a la cola
        queue.add(stringRequest);
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

    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        if(item.getItemId()==R.id.Problema){
            Toast.makeText(this, "Ya estas redactando un Problema", Toast.LENGTH_SHORT).show();
        }

        if(item.getItemId()==R.id.Reporte){
            Intent c = new Intent(this, Reporte.class);//creo mi objeto cambio y lo igualo a un constructor el cual recibe por parametros el contexto y el lugar a donde va
            startActivity(c);
            finish();
        }

        if(item.getItemId()==R.id.Descanso){
            Calendar calendar = Calendar.getInstance();
            int hora = calendar.get(Calendar.HOUR_OF_DAY);
            if(hora < 13 && hora > 15){
                Toast.makeText(this, "Aun no tienes permiso de tomar el descanso", Toast.LENGTH_SHORT).show();
            } else{
                checkDescanso();
            }
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