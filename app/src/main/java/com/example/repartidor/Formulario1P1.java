package com.example.repartidor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

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
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import Pojo.Conexion;
import Pojo.Respuesta;

public class Formulario1P1 extends AppCompatActivity {

    Spinner placas;
    Button send;

    boolean isKilometraje = false, isGasolina = false;

    String rutaimagen;
    RadioGroup anticongelante, motor, transmision, frenos, hidraulica, llantas;
    ImageView Kilo, Gasolina;
    public Respuesta respuesta = new Respuesta();

    private Conexion conexion = new Conexion();

    // Obtener la fecha actual
    Date date = new Date();
    String formattedDate;
    // Definir el formato de la fecha
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario1_p1);
        placas = findViewById(R.id.PlacasVehiculo);

        setPlacas();

        new CountDownTimer(20 * 60 * 1000, 1000) { // 20 minutos en milisegundos, actualización cada 1 segundo

            @Override
            public void onTick(long millisUntilFinished) {
                // Opcional: actualizar UI o mostrar tiempo restante si lo deseas
            }

            @Override
            public void onFinish() {
                // Muestra el mensaje Toast cuando se acaba el tiempo
                Toast.makeText(Formulario1P1.this, "Tiempo agotado", Toast.LENGTH_SHORT).show();

                // Redirige a la otra Activity
                Intent intent = new Intent(Formulario1P1.this, Inicio.class);
                startActivity(intent);
                Toast.makeText(Formulario1P1.this, "Debes de llenar el formulario rapido, le voy a decir al administrador >:(", Toast.LENGTH_SHORT).show();
                finish();
            }
        }.start();

        // Inicializar los RadioGroups
        anticongelante = findViewById(R.id.Anticongelante);
        motor = findViewById(R.id.AceiteMotor);
        transmision = findViewById(R.id.AceiteTransmision);
        frenos = findViewById(R.id.Frenos);
        hidraulica = findViewById(R.id.DHidraulica);
        llantas = findViewById(R.id.llanta);

        send = findViewById(R.id.btnEnviarF1);

        Kilo = findViewById(R.id.K1);
        Gasolina = findViewById(R.id.G1);
        formattedDate = dateFormat.format(date);

        placas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                respuesta.setPlaca(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(Formulario1P1.this, "Selecciona una placa porfavor", Toast.LENGTH_SHORT).show();
            }
        });

        anticongelante.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.AnticongelanteA){
                    respuesta.setAnticongelante("Al nivel");
                } else if(checkedId == R.id.AnticongelanteB){
                    respuesta.setAnticongelante("Bajo del nivel");
                }
            }
        });

        motor.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.AceiteMotorA){
                    respuesta.setAceiteMotor("Al nivel");
                } else if(checkedId == R.id.AceiteMotorB){
                    respuesta.setAceiteMotor("Bajo del nivel");
                }
            }
        });

        transmision.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.AceiteTransmisionA){
                    respuesta.setAceiteTransmision("Al nivel");
                } else if(checkedId == R.id.AceiteTransmisionB){
                    respuesta.setAceiteTransmision("Bajo del nivel");
                }
            }
        });

        frenos.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.FrenosA){
                    respuesta.setFrenos("Al nivel");
                } else if(checkedId == R.id.FrenosB){
                    respuesta.setFrenos("Bajo del nivel");
                }
            }
        });

        hidraulica.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.DHidraulicaA){
                    respuesta.setDireccionHidraulica("Al nivel");
                } else if(checkedId == R.id.DHidraulicaB){
                    respuesta.setDireccionHidraulica("Bajo del nivel");
                }
            }
        });

        llantas.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.llantaA){
                    respuesta.setNivelLlantas("Buena");
                } else if(checkedId == R.id.llantaR){
                    respuesta.setNivelLlantas("Regular");
                } else if(checkedId == R.id.llantaB){
                    respuesta.setNivelLlantas("Mala");
                }
            }
        });

        Kilo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isKilometraje = true;
                abrirCamara(1);
            }
        });

        Gasolina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isGasolina = true;
                abrirCamara(2);
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validarCampos()){
                    Toast.makeText(Formulario1P1.this, "Llene todos los campos del formulario", Toast.LENGTH_SHORT).show();
                } else {
                    enviarDatos();
                }
            }
        });



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
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(Formulario1P1.this, R.layout.spinner_layout, placasList);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            placas.setAdapter(adapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(Formulario1P1.this, "Error al procesar las placas", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(Formulario1P1.this, "Error en la solicitud", Toast.LENGTH_SHORT).show();
            }
        });
        // Agregar la solicitud a la cola de Volley
        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void abrirCamara(int tipo){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getPackageManager()) == null){
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            // Carga la imagen desde la ruta en formato Bitmap
            Bitmap imgBitmap = BitmapFactory.decodeFile(rutaimagen);

            // Comprime la imagen al 50% de su tamaño original
            Bitmap imgBitmapCompressed = Bitmap.createScaledBitmap(
                    imgBitmap,
                    imgBitmap.getWidth() / 2,
                    imgBitmap.getHeight() / 2,
                    true
            );

            // Sobreescribe el archivo con la imagen comprimida
            try (FileOutputStream out = new FileOutputStream(rutaimagen)) {
                imgBitmapCompressed.compress(Bitmap.CompressFormat.JPEG, 50, out); // Ajusta el nivel de calidad
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.d("Imagen Guardada", "Ruta: " + rutaimagen);
            Toast.makeText(this, "Imagen comprimida y guardada exitosamente", Toast.LENGTH_LONG).show();
        }
    }

    private File crearimagen(String nombre, int tipo) throws IOException {
        String name = nombre + "_formulario-matutino" + MainActivity.sendName();
        if(tipo == 1){
            respuesta.setKilometraje(name);
        } else {
            respuesta.setNivelGasolina(name);
        }
        File directorio = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES); // Directorio público
        File imagen = File.createTempFile(name, ".jpg", directorio);

        rutaimagen = imagen.getAbsolutePath();
        return imagen;
    }

    private void enviarDatos() {
        String url = conexion.getURL_BASE() + "formulario1.php?placas=" + respuesta.getPlaca() + "&antic=" + respuesta.getAnticongelante() + "&acem=" + respuesta.getAceiteMotor() +
                "&acet=" + respuesta.getAceiteTransmision() + "&frenos=" + respuesta.getFrenos() + "&direccion=" + respuesta.getDireccionHidraulica() + "&kilo=" + respuesta.getKilometraje() + "&gas=" + respuesta.getNivelGasolina() +
                "&fecha=" + formattedDate + "&duracion=" + 15 + "&repartidor=" + 1234 + "&llantas=" + respuesta.getNivelLlantas();

        // Crear la solicitud GET con la URL completa
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Maneja la respuesta del servidor aquí
                        Toast.makeText(Formulario1P1.this, "Respuesta: " + response, Toast.LENGTH_LONG).show();
                        Log.d("Respuesta", response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Maneja los errores de la solicitud
                        Toast.makeText(Formulario1P1.this, "Error en la solicitud: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        // Agregar la solicitud a la cola de solicitudes de Volley
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private boolean validarCampos(){
        return anticongelante.getCheckedRadioButtonId() == -1 || motor.getCheckedRadioButtonId() == -1 || transmision.getCheckedRadioButtonId() == -1
                || frenos.getCheckedRadioButtonId() == -1 || hidraulica.getCheckedRadioButtonId() == -1 || llantas.getCheckedRadioButtonId() == -1 ||
                !isKilometraje || !isGasolina;
    }
}
