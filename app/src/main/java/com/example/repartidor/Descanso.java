package com.example.repartidor;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Descanso extends AppCompatActivity {

    private TextView temp;
    private Button endt;
    private CountDownTimer countDownTimer;
    private long tempoenmilis = 60 * 60 * 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_descanso);
        temp = findViewById(R.id.regresiva);
        endt = findViewById(R.id.Terminar_descanso);

        startTimmer();

        endt.setOnClickListener(new View.OnClickListener() {
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
                temp.setText("00:00");
            }
        }.start();
    }

    private void stopTimmer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
    private void updateTimmerText(boolean add) {
        int minutes = (int) (tempoenmilis / 1000) / 60;
        int seconds = (int) (tempoenmilis / 1000) % 60;

        String formato = String.format("%02d:%02d", minutes, seconds);
        temp.setText(formato);
    }
}