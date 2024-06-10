package com.example.m8uf2avaluable1;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class EstacionAdapter extends RecyclerView.Adapter<EstacionAdapter.EstacionViewHolder> {

    private List<Estacion> mData;
    private LayoutInflater mInflater;
    private Context context;
    // Constructor
    public EstacionAdapter(List<Estacion> estacionesList, Context context) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.mData = estacionesList;
    }

    @Override
    public int getItemCount(){
        return mData.size();
    }

    @NonNull
    @Override
    public EstacionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.list_element, null);
        return new EstacionAdapter.EstacionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final EstacionAdapter.EstacionViewHolder holder, final int position) {
        holder.bindData(mData.get(position));
    }

    public void setItems(List<Estacion> items) {
        mData = items;
    }

    public class EstacionViewHolder extends RecyclerView.ViewHolder {
        ImageView iconImage;
        TextView name, address, status;

        public EstacionViewHolder(@NonNull View itemView) {
            super(itemView);
            iconImage = itemView.findViewById(R.id.iconImageView);
            name = itemView.findViewById(R.id.nameTextView);
            address = itemView.findViewById(R.id.addressTextView);
            status = itemView.findViewById(R.id.statusTextView);
        }

        void bindData(final Estacion item) {
            //iconImage.setColorFilter(Color.parseColor(item.getColor()), PorterDuff.Mode.SRC_IN);
            name.setText(item.getName());
            address.setText(item.getAddress());

            status.setText(item.getStatus());
        }



    }

}

