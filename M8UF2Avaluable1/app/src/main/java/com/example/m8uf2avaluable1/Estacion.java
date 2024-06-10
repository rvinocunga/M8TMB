package com.example.m8uf2avaluable1;
public class Estacion {
    private String id;
    private String nombre;
    private double latitud;
    private double longitud;
    private String direccion;
    private String lineas;

    public Estacion(String id, String nombre, double latitud, double longitud, String direccion, String lineas) {
        this.id = id;
        this.nombre = nombre;
        this.latitud = latitud;
        this.longitud = longitud;
        this.direccion = direccion;
        this.lineas = lineas;
    }

    // Getters y setters

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public double getLatitud() { return latitud; }
    public void setLatitud(double latitud) { this.latitud = latitud; }

    public double getLongitud() { return longitud; }
    public void setLongitud(double longitud) { this.longitud = longitud; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getLineas() { return lineas; }
    public void setLineas(String lineas) { this.lineas = lineas; }


}

