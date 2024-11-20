package com.example.repartidor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.CountDownTimer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Carga_Descarga extends AppCompatActivity {

    private TextView tempo;
    private Button add10;
    private Button end;
    private CountDownTimer countDownTimer;
    private long tempoenmilis = 20 * 60 * 1000;
    private int uses = 0;
    private static final String CHANNEL_ID = "tiempo_terminado_canal";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carga_descarga);

        tempo = findViewById(R.id.temporizador);
        add10 = findViewById(R.id.Add10min);
        end = findViewById(R.id.Terminar_temporizador);

        // Crear el canal de notificación
        crearCanalNotificacion();

        startTimmer();

        add10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uses < 2) {
                    tempoenmilis += 10 * 60 * 1000;
                    uses++;
                    restartTimmer();  // Reiniciar el temporizador con el nuevo tiempo
                } else {
                    Toast.makeText(Carga_Descarga.this, "Ya estás en el límite de uso", Toast.LENGTH_SHORT).show();
                }
            }
        });

        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopTimmer();
                Intent intent = new Intent(Carga_Descarga.this, Inicio.class);
                startActivity(intent);
                finish();
                Toast.makeText(Carga_Descarga.this, "Continuemos con los pedidos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startTimmer() {
        countDownTimer = new CountDownTimer(tempoenmilis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tempoenmilis = millisUntilFinished;
                updateTimmerText(false);
            }

            @Override
            public void onFinish() {
                tempo.setText("00:00");
                enviarNotificacionTiempoTerminado();  // Enviar notificación cuando el tiempo termine
            }
        }.start();
    }

    private void stopTimmer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    private void restartTimmer() {
        stopTimmer();  // Detener el temporizador actual
        startTimmer(); // Iniciar un nuevo temporizador con el nuevo tiempo
    }

    private void updateTimmerText(boolean add) {
        int minutes = (int) (tempoenmilis / 1000) / 60;
        int seconds = (int) (tempoenmilis / 1000) % 60;

        String formato = String.format("%02d:%02d", minutes, seconds);
        tempo.setText(formato);
    }

    private void enviarNotificacionTiempoTerminado() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Tiempo Terminado")
                .setContentText("El temporizador ha llegado a su fin.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(1, builder.build());
    }

    private void crearCanalNotificacion() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CharSequence nombre = "Canal Tiempo Terminado";
            String descripcion = "Canal para notificaciones cuando el tiempo termina";
            int importancia = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, nombre, importancia);
            channel.setDescription(descripcion);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
