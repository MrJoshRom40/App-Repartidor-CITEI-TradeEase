package com.example.repartidor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import Pojo.Respuesta;

public class Formulario1P1 extends AppCompatActivity {

    Spinner placas;
    RadioGroup anticongelante, motor, transmision, frenos, hidraulica, llantas;
    ImageView Kilo, Gasolina;
    public Respuesta respuesta = new Respuesta();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario1_p1);
        placas = findViewById(R.id.PlacasVehiculo);

        setPlacas();

        // Inicializar los RadioGroups
        anticongelante = findViewById(R.id.Anticongelante);
        motor = findViewById(R.id.AceiteMotor);
        transmision = findViewById(R.id.AceiteTransmision);
        frenos = findViewById(R.id.Frenos);
        hidraulica = findViewById(R.id.DHidraulica);
        llantas = findViewById(R.id.llanta);

        Kilo = findViewById(R.id.K1);
        Gasolina = findViewById(R.id.G1);

        anticongelante.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.AnticongelanteA){
                    respuesta.setAnticongelante("Alto");
                } else if(checkedId == R.id.AnticongelanteB){
                    respuesta.setAnticongelante("Bajo");
                }
            }
        });

        motor.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.AceiteMotorA){
                    respuesta.setAceiteMotor("Alto");
                } else if(checkedId == R.id.AceiteMotorB){
                    respuesta.setAceiteMotor("Bajo");
                }
            }
        });

        transmision.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.AceiteTransmisionA){
                    respuesta.setAceiteTransmision("Alto");
                } else if(checkedId == R.id.AceiteTransmisionB){
                    respuesta.setAceiteTransmision("Bajo");
                }
            }
        });

        frenos.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.FrenosA){
                    respuesta.setAceiteTransmision("Alto");
                } else if(checkedId == R.id.FrenosB){
                    respuesta.setAceiteTransmision("Bajo");
                }
            }
        });

        hidraulica.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.DHidraulicaA){
                    respuesta.setAceiteTransmision("Alto");
                } else if(checkedId == R.id.DHidraulicaB){
                    respuesta.setAceiteTransmision("Bajo");
                }
            }
        });

        llantas.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.llantaA){
                    respuesta.setAceiteTransmision("Buena");
                } else if(checkedId == R.id.llantaR){
                    respuesta.setAceiteTransmision("Regular");
                } else if(checkedId == R.id.llantaB){
                    respuesta.setAceiteTransmision("Baja");
                }
            }
        });

        Kilo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, 1);
                //}
            }
        });

        Gasolina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, 2);
                //}
            }
        });



    }

    private void setPlacas() {
        String url = "http://192.168.50.108/Repartidor/getplacas.php"; // URL de tu archivo PHP

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





}
