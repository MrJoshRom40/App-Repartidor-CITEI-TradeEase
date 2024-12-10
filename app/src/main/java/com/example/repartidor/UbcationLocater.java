package com.example.repartidor;

import android.app.Activity;
import android.content.Context;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

public class UbcationLocater  implements LocationListener {

    private final LocationManager locationManager;
    private final Context context;
    private double longitudDestino;
    private double latitudDestino;
    private static final double radioDestino = 20.0;//establecer un radio de 20 metros para cada destino
    private final Class<?> targetActivity;

    private String NumVenta;

    public UbcationLocater(String longitudDestino, String latitudDestino, Context context, Class<?> targetActivity, String NumVenta) {
        this.longitudDestino = Double.parseDouble(longitudDestino);
        this.latitudDestino = Double.parseDouble(latitudDestino);
        this.context = context;
        this.targetActivity = targetActivity;
        this.NumVenta = NumVenta;

        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    public void startTracking() {
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        // Solicitar actualizaciones de ubicación
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, this);
    }


    @Override
    public void onLocationChanged(@NonNull Location location) {
        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();

        // Verificar si está dentro del radio objetivo
        float[] distance = new float[1];
        Location.distanceBetween(currentLatitude, currentLongitude, latitudDestino, longitudDestino, distance);

        if (distance[0] <= radioDestino) {
            Toast.makeText(context, "¡Llegaste a la ubicación objetivo!", Toast.LENGTH_SHORT).show();

            // Cambiar a otra actividad
            Intent intent = new Intent(context, targetActivity);
            intent.putExtra("NumVenta", NumVenta);
            context.startActivity(intent);

            // Detener actualizaciones de ubicación
            stopTracking();
        }
    }

    public void stopTracking() {
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.removeUpdates(this);
        }
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        Toast.makeText(context, "Por favor, habilita el GPS", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        Toast.makeText(context, "GPS habilitado", Toast.LENGTH_SHORT).show();
    }
}
