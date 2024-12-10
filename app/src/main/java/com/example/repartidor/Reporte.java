package com.example.repartidor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import Pojo.Conexion;

public class Reporte extends AppCompatActivity {

    EditText numv, cant, coment;
    Button sendreporte;
    Toolbar toolbar;
    RadioGroup cantidad;
    String cantidadDe;

    String ubicacion; // Variable para almacenar la ubicación
    private LocationManager locationManager;
    private LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reporte);

        numv = findViewById(R.id.numeroventa_reporte);
        cant = findViewById(R.id.cantidad_reporte);
        coment = findViewById(R.id.comentarios_reporte);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        cantidad = findViewById(R.id.RGCantidad);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Inicializa la ubicación
        ubicacion = "Ubicación no disponible";

        cantidad.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.DeMas) {
                cantidadDe = " de más";
            } else if (checkedId == R.id.DeMenos) {
                cantidadDe = " de menos";
            }
        });

        sendreporte = findViewById(R.id.Send_reporte);
        sendreporte.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(Reporte.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // Solicitar permisos si no están concedidos
                ActivityCompat.requestPermissions(Reporte.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                return;
            }

            if (MainActivity.isEditTextEmpty(numv) || MainActivity.isEditTextEmpty(cant)) {
                Toast.makeText(Reporte.this, "Hay que llenar todos los campos requeridos", Toast.LENGTH_SHORT).show();
            } else {
                String mensaje = "Hola administrador!\nSoy " + MainActivity.sendName() +
                        ", te informo que en el pedido: " + numv.getText().toString() + "\nSe mandaron " + cant.getText().toString() + cantidadDe +
                        "\n\n" + coment.getText().toString() + "\nEstoy en: " + ubicacion;
                sendMensaje(mensaje);
            }
        });

        // Configurar el LocationListener
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                ubicacion = "https://www.google.com/maps?q=" + latitude + "," + longitude;
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(String provider) {}

            @Override
            public void onProviderDisabled(String provider) {}
        };

        // Solicitar actualizaciones de ubicación si los permisos están concedidos
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            iniciarLocationUpdates();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    private void iniciarLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

            // Obtener la última ubicación conocida
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocation != null) {
                double latitude = lastKnownLocation.getLatitude();
                double longitude = lastKnownLocation.getLongitude();
                ubicacion = latitude + "," + longitude;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                iniciarLocationUpdates();
            } else {
                Toast.makeText(this, "Permisos de ubicación denegados", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sendMensaje(String mensaje) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        String phoneNumber = "+523314595049"; // Número de teléfono con código de país
        intent.setData(Uri.parse("https://api.whatsapp.com/send?phone=" + phoneNumber + "&text=" + Uri.encode(mensaje)));
        try {
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Debes descargar WhatsApp para poder enviar Reportes", Toast.LENGTH_SHORT).show();
            Intent intent2 = new Intent(Intent.ACTION_VIEW);
            intent2.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.whatsapp"));
            intent2.setPackage("com.android.vending"); // Asegura que solo abra la Play Store
            startActivity(intent2);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.Problema) {
            Intent c = new Intent(this, Problema.class);
            startActivity(c);
            finish();
        }

        if (item.getItemId() == R.id.Reporte) {
            Toast.makeText(this, "Ya estás redactando un Reporte", Toast.LENGTH_SHORT).show();
        }

        if (item.getItemId() == R.id.Descanso) {
            checkDescanso();
        }

        if (item.getItemId() == R.id.Salir) {
            Toast.makeText(this, "Adiós " + MainActivity.sendName(), Toast.LENGTH_SHORT).show();
            Intent c = new Intent(this, MainActivity.class);
            startActivity(c);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void checkDescanso() {
        Conexion conexion = new Conexion();
        String url = conexion.getURL_BASE() + "descanso.php?Nomina=" + MainActivity.sendNomina();

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        boolean descanso = jsonObject.getBoolean("descanso");

                        if (descanso) {
                            Intent c = new Intent(Reporte.this, Descanso.class);
                            startActivity(c);
                        } else {
                            Toast.makeText(Reporte.this, "Ya se ha usado el descanso", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(Reporte.this, "Error al procesar la respuesta", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(Reporte.this, "Error de conexión", Toast.LENGTH_SHORT).show());

        queue.add(stringRequest);
    }
}