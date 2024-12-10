package com.example.repartidor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Carga_Descarga extends AppCompatActivity {

    private TextView tempo;
    private Button add10;
    private long tempoenmilis = 20 * 1000; // Timer inicial de 20 segundos
    private final long SECOND_TIMER = 10 * 1000; // Cada temporizador adicional de 10 segundos
    private int addedUses = 0; // Contador de usos del botón
    private static final int MAX_ADDED_USES = 2; // Máximo de tiempos adicionales permitidos
    private static final String CHANNEL_ID = "tiempo_terminado_canal";
    String NumVenta;

    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carga_descarga);

        Intent intent = getIntent();
        NumVenta = intent.getStringExtra("NumVenta");

        tempo = findViewById(R.id.temporizador);
        add10 = findViewById(R.id.Add10min);

        // Crear el canal de notificación
        crearCanalNotificacion();

        // Iniciar el temporizador inicial de 20 segundos
        startTimer();

        // Configurar el botón para añadir tiempo
        add10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addedUses < MAX_ADDED_USES) {
                    tempoenmilis += SECOND_TIMER; // Añadir 10 segundos al temporizador
                    addedUses++;
                    restartTimer(); // Reiniciar el temporizador
                    if(addedUses == 2){
                        add10.setVisibility(View.GONE);
                    }
                } else {
                    add10.setEnabled(false); // Deshabilitar el botón
                }
            }
        });
    }

    private void startTimer() {

        countDownTimer = new CountDownTimer(tempoenmilis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if(addedUses == 2){
                    add10.setVisibility(View.GONE);
                }
                tempoenmilis = millisUntilFinished;
                updateTimerText();
            }

            @Override
            public void onFinish() {
                tempo.setText("00:00");
                enviarNotificacionTiempoTerminado();
                if (addedUses < MAX_ADDED_USES) {
                    startSecondTimer(); // Iniciar un nuevo temporizador automáticamente si corresponde
                    addedUses++;
                } else {
                    redirectToAnotherActivity(); // Redirigir al finalizar
                }
            }
        }.start();
    }

    private void startSecondTimer() {
        tempoenmilis = SECOND_TIMER; // Establecer el nuevo tiempo de 10 segundos
        countDownTimer = new CountDownTimer(tempoenmilis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if(addedUses == 2){
                    add10.setVisibility(View.GONE);
                }
                tempoenmilis = millisUntilFinished;
                updateTimerText();
            }

            @Override
            public void onFinish() {
                tempo.setText("00:00");
                enviarNotificacionTiempoTerminado();
                if (addedUses < MAX_ADDED_USES) {
                    startSecondTimer(); // Iniciar un nuevo temporizador automáticamente si corresponde
                    addedUses++;
                } else {
                    redirectToAnotherActivity(); // Redirigir al finalizar
                }
            }
        }.start();
    }

    private void restartTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel(); // Detener el temporizador actual
        }
        startTimer(); // Reiniciar el temporizador
    }

    private void redirectToAnotherActivity() {
        Intent intent = new Intent(Carga_Descarga.this, Inicio.class);
        startActivity(intent);
        finish(); // Cerrar esta actividad
    }

    private void updateTimerText() {
        int seconds = (int) (tempoenmilis / 1000);
        String formato = String.format("00:%02d", seconds);
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
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel(); // Detener el temporizador para evitar fugas de memoria
        }
    }

    private void setPedidoCompletado(){

    }
}
