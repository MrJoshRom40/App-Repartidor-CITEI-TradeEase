package com.example.repartidor;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Descanso extends AppCompatActivity {

    private TextView temp;
    private Button endt;
    private CountDownTimer countDownTimer;
    private long tempoenmilis = 1 * 60 * 175;
    private Vibrator vibrator;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_descanso);
        temp = findViewById(R.id.regresiva);
        endt = findViewById(R.id.Terminar_descanso);

        startTimmer();

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        // Inicializar el MediaPlayer con un sonido
        mediaPlayer = MediaPlayer.create(this, R.raw.victory);

        endt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopTimmer();
            }
        });

        // Listener para cuando el audio termina
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Toast.makeText(Descanso.this, "Vamos a continuar!", Toast.LENGTH_SHORT).show();
                // Iniciar nueva Activity al terminar el audio
                Intent intent = new Intent(Descanso.this, Inicio.class);
                startActivity(intent);
                // Puedes añadir un efecto de transición si lo deseas
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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
                temp.setText("00:00");
                Intent intent = new Intent(Descanso.this, Splash.class);
                startActivity(intent);
                finish();
            }
        }.start();
    }

    private void stopTimmer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            Toast.makeText(this, "Vamos a continuar!", Toast.LENGTH_SHORT).show();
            Intent c = new Intent(this, Splash.class);//creo mi objeto cambio y lo igualo a un constructor el cual recibe por parametros el contexto y el lugar a donde va
            startActivity(c);
            finish();
        }
    }

    private void updateTimmerText(boolean add) {
        int minutes = (int) (tempoenmilis / 1000) / 60;
        int seconds = (int) (tempoenmilis / 1000) % 60;

        String formato = String.format("%02d:%02d", minutes, seconds);
        temp.setText(formato);
    }
}