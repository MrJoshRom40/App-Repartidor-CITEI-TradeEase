package com.example.repartidor;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class DialogBack extends AppCompatActivity {

    private UbcationLocater ubcationLocater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new SweetAlertDialog(this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                .setCustomImage(R.drawable.advertencia)
                .setTitleText("Advertencia repartidor")
                .setContentText("Tu ruta ha sido cancelada por el administrador, debes de regresar a la empresa")
                .setConfirmText("Entendido, regresemos")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        ubcationLocater = new UbcationLocater("-103.41672213391696", "20.664332808268846", DialogBack.this, MainActivity.class, "Formulario2", false);
                        ubcationLocater.startTracking();
                        String uri = "google.navigation:q=" + Uri.encode("20.664332808268846,-103.41672213391696");
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        intent.setPackage("com.google.android.apps.maps");
                        startActivity(intent);
                        finish();
                    }
                })
                .show();
    }
}