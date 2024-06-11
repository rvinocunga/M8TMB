
package com.example.m8uf2avaluable1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EstacionAdapter extends RecyclerView.Adapter<MyViewHolder> {
    Context context;
    List<Estacion> estaciones;

    public EstacionAdapter(Context context, List<Estacion> estaciones) {
        this.context = context;
        this.estaciones = estaciones;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_view,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.nombreView.setText(estaciones.get(position).getNombre());
        holder.coordenadasView.setText(estaciones.get(position).getLatitud() + estaciones.get(position).getLongitud());
        holder.lineasView.setText(estaciones.get(position).getLineas());
    }

    @Override
    public int getItemCount() {
        return estaciones.size();
    }
}

