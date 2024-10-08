package com.example.repartidor;

import androidx.appcompat.app.AppCompatActivity;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.os.Bundle;
import android.widget.Toast;

public class Carga_Descarga extends AppCompatActivity {

    private TextView tempo;
    private Button add10;
    private Button end;
    private CountDownTimer countDownTimer;
    private long tempoenmilis = 20 * 60 * 1000;
    private int uses = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carga_descarga);
        tempo = findViewById(R.id.temporizador);
        add10 = findViewById(R.id.Add10min);
        end = findViewById(R.id.Terminar_temporizador);

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
}