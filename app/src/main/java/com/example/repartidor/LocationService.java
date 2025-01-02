package com.example.repartidor;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

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
import cn.pedant.SweetAlert.SweetAlertDialog;

public class LocationService extends Service {

    // Variables globales para almacenar latitud y longitud
    public static double globalLatitude = 0.0;
    public static double globalLongitude = 0.0;
    private Conexion conexion = new Conexion();

    private LocationManager locationManager;
    private LocationListener locationListener;

    private static final String CHANNEL_ID = "LocationServiceChannel";

    @Override
    public void onCreate() {
        super.onCreate();
        // Configurar el LocationManager
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Crear el listener de ubicación
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                // Actualizar las variables globales
                globalLatitude = location.getLatitude();
                globalLongitude = location.getLongitude();
                setLocalizacion();
                checkroute(MainActivity.sendNomina());
                Log.d("LocationService", "Latitud: " + globalLatitude + ", Longitud: " + globalLongitude);
            }

            @Override
            public void onStatusChanged(String provider, int status, android.os.Bundle extras) {}

            @Override
            public void onProviderEnabled(@NonNull String provider) {}

            @Override
            public void onProviderDisabled(@NonNull String provider) {}
        };

        // Verificar permisos
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e("LocationService", "Permisos de ubicación no concedidos");
            stopSelf(); // Detiene el servicio si no tiene permisos
            return;
        }

        // Solicitar actualizaciones de ubicación
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 180000, 0, locationListener);

        // Iniciar como Foreground Service
        iniciarForegroundService();
    }

    private void iniciarForegroundService() {
        // Crear el canal de notificación para Android 8.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Servicio de Ubicación",
                    NotificationManager.IMPORTANCE_LOW
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }

        // Crear la notificación persistente
        Notification notification = new Notification.Builder(this, CHANNEL_ID)
                .setContentTitle("Servicio de Ubicación Activo")
                .setContentText("Recopilando coordenadas GPS en segundo plano")
                .setSmallIcon(android.R.drawable.ic_menu_mylocation)
                .build();

        // Iniciar el servicio en primer plano
        startForeground(1, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Mantener el servicio ejecutándose hasta que se detenga manualmente
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationManager != null && locationListener != null) {
            locationManager.removeUpdates(locationListener);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Este servicio no soporta binding
        return null;
    }

    private void setLocalizacion(){
        String url = conexion.getURL_BASE() + "setLocalizacion.php?Longitud=" + globalLongitude + "&Latitud=" + globalLatitude + "&Nomina=" + MainActivity.sendNomina();

        RequestQueue queue = Volley.newRequestQueue(this);
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

    private void checkroute(String nomina) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = conexion.getURL_BASE() + "checkRoute.php?nomina=" + nomina;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    if (jsonResponse.getInt("count") == 0) {
                        Intent intent = new Intent(getApplicationContext(), DialogBack.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Requerido para iniciar desde un Service
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
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
