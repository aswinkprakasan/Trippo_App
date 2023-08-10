package com.example.trippoapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trippoapp.model.RecycleSeasonClass;

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
        holder.imageView.setImageResource(seasonClass.get(position).getPic());

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PlaceDetailsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("Id", seasonClass.get(holder.getBindingAdapterPosition()).getId());
                intent.putExtra("Name", seasonClass.get(holder.getBindingAdapterPosition()).getName());
                intent.putExtra("Location", seasonClass.get(holder.getBindingAdapterPosition()).getLocation());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return seasonClass.size();
    }
}

class MyViewHolder extends RecyclerView.ViewHolder{

    TextView name, location;
    CardView card;
    ImageView imageView;
    public MyViewHolder(@NonNull View itemView) {
        super(itemView);

        name = itemView.findViewById(R.id.name);
        location = itemView.findViewById(R.id.location);
        card = itemView.findViewById(R.id.card1);
        imageView = itemView.findViewById(R.id.image);
    }
}
