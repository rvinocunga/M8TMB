package com.example.m8uf2avaluable1;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

public class EstacionAdapter extends RecyclerView.Adapter<EstacionAdapter.MyViewHolder> {
    private Context context;
    private List<Estacion> estaciones;
    private Map<String, String> coloresLineas;

    public EstacionAdapter(Context context, List<Estacion> estaciones, Map<String, String> coloresLineas) {
        this.context = context;
        this.estaciones = estaciones;
        this.coloresLineas = coloresLineas;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Estacion estacion = estaciones.get(position);
        holder.nombreView.setText(estacion.getNombre());
        holder.coordenadasView.setText(estacion.getLatitud() + ", " + estacion.getLongitud());

        // Obtener las líneas separadas y colorearlas dinámicamente
        String[] lineasSeparadas = estacion.getLineas().split("L");
        StringBuilder lineasHTML = new StringBuilder();
        for (int i = 1; i < lineasSeparadas.length; i++) {
            String linea = "L" + lineasSeparadas[i].trim();
            String color = coloresLineas.get(linea);
            if (color != null) {
                lineasHTML.append("<font color='").append(color).append("'>").append(linea).append("</font>");
            } else {
                lineasHTML.append(linea);
            }
            if (i < lineasSeparadas.length - 1) {
                lineasHTML.append(", ");
            }
        }
        holder.lineasView.setText(Html.fromHtml("Líneas: " + lineasHTML.toString()));
    }

    @Override
    public int getItemCount() {
        return estaciones.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nombreView, coordenadasView, lineasView;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            nombreView = itemView.findViewById(R.id.nombreEstacionTextView);
            coordenadasView = itemView.findViewById(R.id.coordenadasTextView);
            lineasView = itemView.findViewById(R.id.lineasTextView);
        }
    }
}
