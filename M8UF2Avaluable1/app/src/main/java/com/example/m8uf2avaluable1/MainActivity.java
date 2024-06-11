package com.example.m8uf2avaluable1;

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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements LocationListener {
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
    List<Estacion> estaciones = new ArrayList<>();

    // Marker del usuario
    private Marker markerUsuario;
    //Variables para el RecyclerView
    private RecyclerView recyclerViewEstaciones;
    private EstacionAdapter estacionAdapter;
    private static final Map<String, String> coloresLineas = new HashMap<>();

    static {
        // Inicializar el diccionario de colores para cada línea
        coloresLineas.put("L1", "#FF0000"); // rojo
        coloresLineas.put("L2", "#7D2181"); // morado
        coloresLineas.put("L3", "#00FF00"); // verde
        coloresLineas.put("L4", "#FFFF00"); // amarillo
        coloresLineas.put("L5", "#0000FF"); // azul
        coloresLineas.put("L9N", "#FFA500"); // naranja
        coloresLineas.put("L9S", "#FFA500"); // naranja
        coloresLineas.put("L10N", "#B2FFFF"); // celeste
        coloresLineas.put("L10S", "#B2FFFF"); // celeste
        coloresLineas.put("L11", "#00FF00"); // lima
        coloresLineas.put("FM", "#00FF00"); // verde
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.contexto = this.getApplicationContext();
        Configuration.getInstance().load(contexto, PreferenceManager.getDefaultSharedPreferences(contexto));

        //toolbar
        ImageView mapIcon = findViewById(R.id.toolbar_mapa_icon);
        ImageView listaIcon = findViewById(R.id.toolbar_lista_icon);
        TextView text = findViewById(R.id.toolbar_text);

        // Inicializar el RecyclerView y el adaptador
        recyclerViewEstaciones = findViewById(R.id.recycler);
        recyclerViewEstaciones.setLayoutManager(new LinearLayoutManager(this));
        estacionAdapter = new EstacionAdapter(this,estaciones, coloresLineas);
        recyclerViewEstaciones.setAdapter(estacionAdapter);
        listaIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Cargando lista...", Toast.LENGTH_SHORT).show();
                // Mostrar el RecyclerView y ocultar el mapa si es visible
                if (recyclerViewEstaciones.getVisibility() == View.GONE) {
                    recyclerViewEstaciones.setVisibility(View.VISIBLE);
                    // Ocultar el mapa si es visible
                    mapa = findViewById(R.id.mapa);
                    if (mapa != null && mapa.getVisibility() == View.VISIBLE) {
                        mapa.setVisibility(View.GONE);
                    }
                } else {
                    recyclerViewEstaciones.setVisibility(View.GONE);
                }
            }
        });


        mapIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Mostrar un mensaje indicando que se ha hecho clic en el mapa
                Toast.makeText(MainActivity.this, "Click en el mapa...", Toast.LENGTH_SHORT).show();

                // Mostrar el mapa y ocultar el RecyclerView si está visible
                if (mapa.getVisibility() == View.GONE) {
                    mapa.setVisibility(View.VISIBLE);
                    recyclerViewEstaciones.setVisibility(View.GONE);
                }
            }
        });


        this.mapa = this.findViewById(R.id.mapa);
        this.mapController = (MapController) this.mapa.getController();

        //Manager
        this.locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Inicializar la RequestQueue
        requestQueue = Volley.newRequestQueue(this);

        // cargar datos
        cargarDatosEstaciones();

        // Solicitar permisos
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
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
        getMenuInflater().inflate(R.menu.menu_item, menu);
        return true;
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
        this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000l, 0f, this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            this.iniciaLocalitzacio();
        } else {
            Toast.makeText(this, "L'aplicació no pot funcionar sense aquest permís.", Toast.LENGTH_LONG).show();
        }
    }

    private double calcularDistancia(double latEstacion, double lonEstacion) {
        Location estacionLocation = new Location("Estacion");
        estacionLocation.setLatitude(latEstacion);
        estacionLocation.setLongitude(lonEstacion);

        Location usuarioLocation = new Location("Usuario");
        usuarioLocation.setLatitude(latUsuari);
        usuarioLocation.setLongitude(longUsuari);

        // Calcular la distancia entre la estación y el dispositivo
        return usuarioLocation.distanceTo(estacionLocation);
    }


    private void cargarDatosEstaciones() {


        String url = "https://api.tmb.cat/v1/transit/estacions?app_id=c1fb5d9f&app_key=16e6144e43916f5341ba81abdfe90912";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
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
                        String[] lineasSeparadas = lines.split("L");

                        JSONArray coordinates = geometry.getJSONArray("coordinates");
                        double longitude = coordinates.getDouble(0);
                        double latitude = coordinates.getDouble(1);

                        // Construir la lista de líneas con colores
                        StringBuilder lineasColoreadas = new StringBuilder();
                        for (int j = 1; j < lineasSeparadas.length; j++) {
                            String linea = "L" + lineasSeparadas[j];
                            String color = coloresLineas.get(linea);
                            if (color != null) {
                                lineasColoreadas.append("<font color='").append(color).append("'>").append(linea).append("</font>");
                            } else {
                                lineasColoreadas.append(linea);
                            }
                            if (j < lineasSeparadas.length - 1) {
                                lineasColoreadas.append(", ");
                            }
                        }

                        Estacion estacion = new Estacion(id, name, String.valueOf(latitude), String.valueOf(longitude), address, lines);
                        estaciones.add(estacion);

                        // marcador estacion
                        Marker marker = new Marker(mapa);
                        marker.setPosition(new GeoPoint(latitude, longitude));
                        marker.setTitle(name);
                        String description = "<ul><li>LINEAS: " + lineasColoreadas + "</li><li>Coordenadas: " + coordinates + " </li></ul>";
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