package com.example.trippoapp.adapter;

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


import com.example.trippoapp.model.HigherRatingClass;
import com.example.trippoapp.PlaceDetailsActivity;
import com.example.trippoapp.R;

import java.util.List;

public class HigherRatingAdapter extends RecyclerView.Adapter<MyViewHolder1> {

    private Context context;
    private List<HigherRatingClass> ratingClass;

    public HigherRatingAdapter(Context context, List<HigherRatingClass> ratingClass) {
        this.context = context;
        this.ratingClass = ratingClass;
    }

    @NonNull
    @Override
    public MyViewHolder1 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder1(LayoutInflater.from(context).inflate(R.layout.nearby_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder1 holder, int position) {

        holder.name.setText(ratingClass.get(position).getName());
        holder.rating.setText(ratingClass.get(position).getRat());
        holder.imageView.setImageResource(ratingClass.get(position).getPic());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PlaceDetailsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("Id", ratingClass.get(holder.getBindingAdapterPosition()).getId());
                intent.putExtra("Name", ratingClass.get(holder.getBindingAdapterPosition()).getName());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return ratingClass.size();
    }
}

class MyViewHolder1 extends RecyclerView.ViewHolder{
    TextView name, rating;
    CardView cardView;
    ImageView imageView;
    public MyViewHolder1(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.name);
        rating = itemView.findViewById(R.id.rating);
        cardView = itemView.findViewById(R.id.card2);
        imageView = itemView.findViewById(R.id.image);
    }
}
