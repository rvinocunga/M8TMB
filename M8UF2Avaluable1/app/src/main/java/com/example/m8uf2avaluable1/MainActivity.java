package com.example.m8uf2avaluable1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements LocationListener{
    private MapView mapa;
    private MapController mapController;
    private Context contexto;
    private LocationManager locationManager;
    private double longUsuari;
    private double latUsuari;

    //json
    private RequestQueue requestQueue;
    private TextView visor;

    // lista
    List<Estacion> elements;

    // Marker del usuario
    private Marker markerUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        this.contexto = this.getApplicationContext();
        Configuration.getInstance().load(contexto, PreferenceManager.getDefaultSharedPreferences(contexto));

        setContentView(R.layout.activity_main);

        this.mapa = this.findViewById(R.id.mapa);
        this.mapController = (MapController) this.mapa.getController();

        //Manager
        this.locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Solicitar permisos
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // codi si tenim permís
            this.iniciaLocalitzacio();
        } else {
            // en cas de no tenir, el demanem
            this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        // Aplicar zoom al mapa
        this.mapController.setZoom(18);

        // Centrar mapa en coordenadas Barcelona
        this.mapController.setCenter(new GeoPoint(41.3887900, 2.1589900));

        // Afegir marcadors al mapa
        this.creaMarcadorSimple();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void creaMarcadorSimple() {
        // Crear marcador usuari
        markerUsuario = new Marker(this.mapa);
        GeoPoint point = new GeoPoint(latUsuari, longUsuari);
        markerUsuario.setPosition(point);
        markerUsuario.setTitle("USUARIO");

        //markerUsuario.setImage(this.getDrawable(R.drawable.torreagbar));

        // añadir marcador al mapa
        this.mapa.getOverlays().add(markerUsuario);
    }

    @SuppressLint("MissingPermission")
    private void iniciaLocalitzacio() {
        this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000l, 0f, this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==1 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            this.iniciaLocalitzacio();
        }else {
            Toast.makeText(this, "L'aplicació no pot funcionar sense aquest permís.", Toast.LENGTH_LONG).show();
        }
    }


public void but_cargarDatos(View view) {
    try {
        String url = "https://opendata-ajuntament.barcelona.cat/data/dataset/bd2462df-6e1e-4e37-8205-a4b8e7313b84/resource/f60e9291-5aaa-417d-9b91-612a9de800aa/download";
        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // limpia el visor
                            visor.setText("");

                            JSONObject data = response.getJSONObject("data");
                            JSONArray stations = data.getJSONArray("stations");

                            // Mostrar los nombres de las estaciones
                            for (int i = 0; i < stations.length(); i++) {
                                JSONObject station = stations.getJSONObject(i);
                                String name = station.getString("name");
                                visor.append(name + "\n");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            visor.setText("Error: JSONException");
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        visor.setText("Error: " + error.getMessage());
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> headers = new HashMap<>();
                        // Agregar tu token al encabezado
                        headers.put("Authorization", "Bearer 182e680bf6c6f1eb864d25058f907bf8c07c974cd94422bd9d2827ec38c8d8af");
                        return headers;
                    }
                };
        this.requestQueue.add(jsonObjectRequest);
    } catch (Exception e) {
        e.printStackTrace();
        visor.setText("Error: " + e.getMessage());
    }
}
    @Override
    public void onLocationChanged(@NonNull Location location) {
        this.longUsuari = location.getLongitude();
        this.latUsuari = location.getLatitude();

        // Actualizar la posición del marcador del usuario
        if (markerUsuario != null) {
            markerUsuario.setPosition(new GeoPoint(latUsuari, longUsuari));
            this.mapa.invalidate(); // Refrescar el mapa
        }
    }
    public void centrarUsuari(View view) {
        this.mapController.animateTo(new GeoPoint(latUsuari, longUsuari));
    }

}