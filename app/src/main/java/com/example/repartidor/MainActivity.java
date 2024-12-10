package com.example.repartidor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import Global.PedidosAsignados;
import Pojo.Conexion;
import Pojo.Pedido;

public class MainActivity extends AppCompatActivity {

    private EditText usuario, contra;
    private Button logear;
    String formulario = "";

    public static Repartidor repartidor = new Repartidor();

    private Conexion conexion = new Conexion();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent i = getIntent();
        formulario = i.getStringExtra("Fomulario");

        requestPermission();
        getDeviceToken();

        usuario = findViewById(R.id.usr);
        contra = findViewById(R.id.pass);
        logear = findViewById(R.id.login);

        logear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isEditTextEmpty(usuario) || isEditTextEmpty(contra)){
                    Toast.makeText(MainActivity.this, "Hay que llenar todos los campos", Toast.LENGTH_SHORT).show();
                }
                else{
                    String nomina = usuario.getText().toString().trim();
                    String clave = contra.getText().toString().trim();
                    login(nomina, clave);
                }
            }
        });
    }

    private void login(String nomina, String clave) {
        // Crear la URL con los parámetros 'nomina' y 'clave'
        String url = conexion.getURL_BASE() + "login.php?nomina=" + nomina + "&clave=" + clave;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    // Verificar si la respuesta es un JSON válido
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");

                    // Si el login fue exitoso
                    if (status.equals("success")) {
                        JSONObject userData = jsonObject.getJSONObject("user_data");
                        String nomina = userData.getString("nomina");
                        String nombre = userData.getString("nombre");

                        // Establecer los datos del repartidor
                        repartidor.setNombre(nombre);
                        repartidor.setNomina(nomina);

                        // Mostrar mensaje de bienvenida
                        Toast.makeText(MainActivity.this, "Bienvenido " + repartidor.getNombre(), Toast.LENGTH_SHORT).show();

                        // Cargar los pedidos solo después de haber logueado exitosamente
                        cargar(nomina);

                        if ("Formulario1P1".equals(formulario)) {
                            Intent intent = new Intent(MainActivity.this, Formulario1P1.class);
                            startActivity(intent);
                            finish();
                        } else if ("Formulario2".equals(formulario)) {
                            Intent intent = new Intent(MainActivity.this, Formulario2.class);
                            startActivity(intent);
                            finish();
                        } else if (formulario == null || formulario.isEmpty()) {
                            Intent intent = new Intent(MainActivity.this, Splash.class);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        // Si hay un error, mostrar el mensaje
                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Error al procesar los datos", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Si ocurre un error en la conexión
                Toast.makeText(MainActivity.this, "Error en la conexión", Toast.LENGTH_SHORT).show();
            }
        });

        // Crear la cola de solicitudes y añadir la solicitud GET
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void cargar(String nomina) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = conexion.getURL_BASE() + "Pedidos.php?nomina=" + nomina;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Response", response);
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    PedidosAsignados.Pedidos.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        Pedido pedido = new Pedido();
                        pedido.setNombrecliente(obj.getString("NombreCompleto"));
                        pedido.setDireccion(obj.getString("DireccionCompleta"));
                        pedido.setTelefono(obj.getString("Telefono"));
                        pedido.setNumeroDeVenta(obj.getString("NumeroVenta"));
                        pedido.setEstadoDelpedido(obj.getString("EstadoPedido"));
                        pedido.setLongitudPedido(obj.getString("LongitudPedido"));
                        pedido.setLatitudPedido(obj.getString("LatitudPedido"));
                        PedidosAsignados.Pedidos.add(pedido);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Error al procesar los datos", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        queue.add(stringRequest);
    }

    public static String sendName(){
        return repartidor.getNombre();
    }

    public static String sendNomina(){
        return repartidor.getNomina();
    }

    public static boolean isEditTextEmpty(@NonNull EditText editText){
        String txt = editText.getText().toString();
        if(txt.isEmpty())
            return true;
        return false;
    }

    public void requestPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permiso concedido", Toast.LENGTH_SHORT).show();
            } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {
                showPermissionExplanationDialog();
            } else{
                resultLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS);
            }
        } else{
            getDeviceToken();
        }
    }

    private ActivityResultLauncher<String> resultLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), isGranted ->{
                if(isGranted){

                }
                else{

                }
            }
    );

    private void showPermissionExplanationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Permiso Necesario")
                .setMessage("Esta aplicación necesita el permiso de notificaciones para funcionar correctamente.")
                .setPositiveButton("Conceder", (dialog, which) -> resultLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS))
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                .show();
    }

    public void getDeviceToken(){
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if(!task.isSuccessful()){
                    Log.w("TAG", "Fetching FCM registration token failed", task.getException());
                    return;
                }
                String token = task.getResult();
                Log.d("", "Token: " + token);
            }
        });
    }
}
