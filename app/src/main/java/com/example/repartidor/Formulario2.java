package com.example.repartidor;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Formulario2 extends AppCompatActivity {

    EditText Sonidos, Golpes, Interior, comentarios;
    ImageView kilometraje, gasolina;
    boolean isKilometraje = false, isGasolina = false;
    String rutaimagen2;
    Button btnEnviar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_formulario2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Sonidos = findViewById(R.id.SonidosNuevos);
        Golpes = findViewById(R.id.GolpesNuevos);
        Interior = findViewById(R.id.Interiores);
        comentarios = findViewById(R.id.Comentarios);
        btnEnviar = findViewById(R.id.btnEnviarF2);

        kilometraje = findViewById(R.id.K2);
        gasolina = findViewById(R.id.G2);

        kilometraje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isKilometraje = true;
                abrirCamara();
            }
        });

        gasolina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isGasolina = true;
                abrirCamara();
            }
        });

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validarCampos()){
                    Toast.makeText(Formulario2.this, "Llene todos los campos del formulario", Toast.LENGTH_SHORT).show();
                } else {
                    enviarDatos();
                }
            }
        });


    }

    private void abrirCamara(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getPackageManager()) != null){
            File imagenarchivo = null;
            try{
                imagenarchivo = crearimagen("foto_");
            } catch (IOException e){
                Log.e("Error", e.toString());
            }


            if(imagenarchivo != null){
                Uri fotouri = FileProvider.getUriForFile(this, "com.example.repartidor.fileprovider", imagenarchivo);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fotouri);
                startActivityForResult(intent, 1);
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            // Carga la imagen desde la ruta en formato Bitmap
            Bitmap imgBitmap = BitmapFactory.decodeFile(rutaimagen2);

            // Comprime la imagen al 50% de su tamaño original
            Bitmap imgBitmapCompressed = Bitmap.createScaledBitmap(
                    imgBitmap,
                    imgBitmap.getWidth() / 2,
                    imgBitmap.getHeight() / 2,
                    true
            );

            // Sobreescribe el archivo con la imagen comprimida
            try (FileOutputStream out = new FileOutputStream(rutaimagen2)) {
                imgBitmapCompressed.compress(Bitmap.CompressFormat.JPEG, 50, out); // Ajusta el nivel de calidad
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.d("Imagen Guardada", "Ruta: " + rutaimagen2);
            Toast.makeText(this, "Imagen comprimida y guardada en: " + rutaimagen2, Toast.LENGTH_LONG).show();
        }
    }

    private File crearimagen(String nombre) throws IOException {
        String name = nombre + "_";
        File directorio = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES); // Directorio público
        File imagen = File.createTempFile(name, ".jpg", directorio);

        rutaimagen2 = imagen.getAbsolutePath();
        return imagen;
    }

    private void enviarDatos() {

    }

    private boolean validarCampos(){
        return Sonidos.getText().toString().isEmpty() || Golpes.getText().toString().isEmpty() || Interior.getText().toString().isEmpty() ||
                comentarios.getText().toString().isEmpty() || !isKilometraje || !isGasolina;
    }
}