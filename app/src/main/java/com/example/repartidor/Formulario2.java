package com.example.repartidor;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import Pojo.Conexion;

public class Formulario2 extends AppCompatActivity {

    EditText Sonidos, Golpes, Interior, comentarios;
    ImageView kilometraje, gasolina;
    boolean isKilometraje = false, isGasolina = false;
    String rutaimagen2;
    Button btnEnviar;
    Spinner placas;
    private Conexion conexion = new Conexion();

    Date date = new Date();
    String formattedDate;
    // Definir el formato de la fecha
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");


    String placa, sonidos, golpe, interior, comentario, kilo, gas;

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

        new CountDownTimer(20 * 60 * 1000, 1000) { // 20 minutos en milisegundos, actualización cada 1 segundo

            @Override
            public void onTick(long millisUntilFinished) {
                // Opcional: actualizar UI o mostrar tiempo restante si lo deseas
            }

            @Override
            public void onFinish() {
                // Muestra el mensaje Toast cuando se acaba el tiempo
                Toast.makeText(Formulario2.this, "Tiempo agotado", Toast.LENGTH_SHORT).show();

                // Redirige a la otra Activity
                Intent intent = new Intent(Formulario2.this, Inicio.class);
                startActivity(intent);
                Toast.makeText(Formulario2.this, "Debes de llenar el formulario rapido, le voy a decir al administrador >:(", Toast.LENGTH_SHORT).show();
                finish();
            }
        }.start();

        placas = findViewById(R.id.PlacasVehiculoF2);
        Sonidos = findViewById(R.id.SonidosNuevos);
        Golpes = findViewById(R.id.GolpesNuevos);
        Interior = findViewById(R.id.Interiores);
        comentarios = findViewById(R.id.Comentarios);
        btnEnviar = findViewById(R.id.btnEnviarF2);

        setPlacas();

        kilometraje = findViewById(R.id.K2);
        gasolina = findViewById(R.id.G2);
        formattedDate = dateFormat.format(date);

        kilometraje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isKilometraje = true;
                abrirCamara(1);
            }
        });

        gasolina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isGasolina = true;
                abrirCamara(2);
            }
        });

        placas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                placa = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(Formulario2.this, "Selecciona una placa porfavor", Toast.LENGTH_SHORT).show();
            }
        });

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validarCampos()){
                    Toast.makeText(Formulario2.this, "Llene todos los campos del formulario", Toast.LENGTH_SHORT).show();
                } else {
                    sonidos = Sonidos.getText().toString();
                    golpe = Golpes.getText().toString();
                    interior = Interior.getText().toString();
                    comentario = comentarios.getText().toString();
                    enviarDatos();
                }
            }
        });


    }

    private void abrirCamara(int tipo){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getPackageManager()) != null){
            File imagenarchivo = null;
            try{
                imagenarchivo = crearimagen("foto_", tipo);
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

    private void setPlacas() {
        String url = conexion.getURL_BASE() + "getplacas.php"; // URL de tu archivo PHP

        // Crear la solicitud GET para obtener las placas
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            // Parsear la respuesta JSON
                            JSONArray placasArray = new JSONArray(response);

                            // Crear un array para almacenar las placas
                            String[] placasList = new String[placasArray.length()];

                            // Llenar el array con las placas obtenidas
                            for (int i = 0; i < placasArray.length(); i++) {
                                JSONObject placaObject = placasArray.getJSONObject(i);
                                String placa = placaObject.getString("Placa");
                                placasList[i] = placa;
                            }

                            // Crear un adaptador de Array y asignarlo al Spinner
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(Formulario2.this, R.layout.spinner_layout, placasList);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            placas.setAdapter(adapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(Formulario2.this, "Error al procesar las placas", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(Formulario2.this, "Error en la solicitud", Toast.LENGTH_SHORT).show();
            }
        });
        // Agregar la solicitud a la cola de Volley
        Volley.newRequestQueue(this).add(stringRequest);
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

    private File crearimagen(String nombre, int tipo) throws IOException {
        String name = nombre + "_";
        if(tipo == 1){
            kilo = name;
        } else {
            gas = name;
        }
        File directorio = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES); // Directorio público
        File imagen = File.createTempFile(name, ".jpg", directorio);

        rutaimagen2 = imagen.getAbsolutePath();
        return imagen;
    }

    private void enviarDatos() {
        String url = conexion.getURL_BASE() + "formulario2.php?placas=" + placa + "&sonidos=" + sonidos + "&golpes=" + golpe +
                "&interiores=" + interior + "&kilometraje=" + kilo + "&gas=" + gas + "&comentarios=" + comentario + "&fecha=" + formattedDate + "&duracion=" + 15 + "&repartidor=" + MainActivity.sendNomina();

        // Crear la solicitud GET con la URL completa
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Maneja la respuesta del servidor aquí
                        Toast.makeText(Formulario2.this, "Respuesta: " + response, Toast.LENGTH_LONG).show();
                        Log.d("Respuesta", response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Maneja los errores de la solicitud
                        Toast.makeText(Formulario2.this, "Error en la solicitud: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        // Agregar la solicitud a la cola de solicitudes de Volley
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private boolean validarCampos(){
        return Sonidos.getText().toString().isEmpty() || Golpes.getText().toString().isEmpty() || Interior.getText().toString().isEmpty() ||
                comentarios.getText().toString().isEmpty() || !isKilometraje || !isGasolina;
    }
}