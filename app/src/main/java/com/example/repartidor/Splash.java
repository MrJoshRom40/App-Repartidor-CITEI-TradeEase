package com.example.repartidor;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);
        // Ejecutar la transición después de 3 segundos (3000 ms)
        new Handler().postDelayed(() -> {
            // Cambiar a la actividad principal
            Intent intent = new Intent(Splash.this, Inicio.class);
            startActivity(intent);
            finish(); // Cierra la actividad actual para que no vuelva con el botón "Atrás"
        }, 2000); // Duración en milisegundos
    }
}