package com.example.trippoapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecycleSeasonAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private Context context;
    private List<RecycleSeasonClass> seasonClass;

    public RecycleSeasonAdapter(Context context, List<RecycleSeasonClass> seasonClass) {
        this.context = context;
        this.seasonClass = seasonClass;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.season_recycle_view, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.name.setText(seasonClass.get(position).getName());
        holder.location.setText(seasonClass.get(position).getLocation());

    }

    @Override
    public int getItemCount() {
        return seasonClass.size();
    }
}

class MyViewHolder extends RecyclerView.ViewHolder{

    TextView name, location;
    public MyViewHolder(@NonNull View itemView) {
        super(itemView);

        name = itemView.findViewById(R.id.name);
        location = itemView.findViewById(R.id.location);
    }
}
