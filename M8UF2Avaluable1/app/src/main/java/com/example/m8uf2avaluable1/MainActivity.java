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
import com.android.volley.toolbox.Volley;

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

        // Inicializar la RequestQueue
        requestQueue = Volley.newRequestQueue(this);

        // cargar datos
        cargarDatosEstaciones();

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


    private void cargarDatosEstaciones() {
        String url = "https://api.tmb.cat/v1/transit/estacions?app_id=c1fb5d9f&app_key=16e6144e43916f5341ba81abdfe90912";

        // Inicializar la lista elements
        List<Estacion> elements = new ArrayList<>();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray stations = response.getJSONArray("features");

                            for (int i = 0; i < stations.length(); i++) {
                                JSONObject station = stations.getJSONObject(i);
                                JSONObject properties = station.getJSONObject("properties");
                                JSONObject geometry = station.getJSONObject("geometry");

                                String id = properties.getString("ID_ESTACIO");
                                String name = properties.getString("NOM_ESTACIO");
                                String address = "ADRECA";
                                String lines = properties.getString("PICTO");
                                JSONArray coordinates = geometry.getJSONArray("coordinates");
                                double longitude = coordinates.getDouble(0);
                                double latitude = coordinates.getDouble(1);

                                Estacion estacion = new Estacion(id, name, latitude, longitude, address, lines);
                                elements.add(estacion);

                                // marcador estacion
                                Marker marker = new Marker(mapa);
                                marker.setPosition(new GeoPoint(latitude, longitude));
                                marker.setTitle(name);
                                String description = "<ul><li> LINEAS: "+ lines +"</li><li> Coordenadas: "+ coordinates +" </li></ul>";
                                marker.setSubDescription(description);
                                mapa.getOverlays().add(marker);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "Error: JSONException", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        this.requestQueue.add(jsonObjectRequest);
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