package com.example.m8uf2avaluable1;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyViewHolder extends RecyclerView.ViewHolder {
    TextView nombreView, coordenadasView, lineasView;
    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        nombreView = itemView.findViewById(R.id.nombreEstacionTextView);
        coordenadasView = itemView.findViewById(R.id.coordenadasTextView);
        lineasView = itemView.findViewById(R.id.lineasTextView);
    }
}
