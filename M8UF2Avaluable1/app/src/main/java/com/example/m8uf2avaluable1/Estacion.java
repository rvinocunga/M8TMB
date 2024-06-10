package com.example.m8uf2avaluable1;

public class Estacion {
    private String name;
    private String address;
    private String status;

    public Estacion(String name, String address, String status) {
        this.name = name;
        this.address = address;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}