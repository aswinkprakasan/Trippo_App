package com.example.trippoapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trippoapp.PlaceDetailsActivity;
import com.example.trippoapp.R;
import com.example.trippoapp.model.AdminRecycleClass;

import java.util.List;

public class RecycleReviewAdapter extends RecyclerView.Adapter<MyViewHolder3>{

    private Context context;
    private List<AdminRecycleClass> adminRecycle;

    public RecycleReviewAdapter(Context context, List<AdminRecycleClass> adminRecycle) {
        this.context = context;
        this.adminRecycle = adminRecycle;
    }

    @NonNull
    @Override
    public MyViewHolder3 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder3(LayoutInflater.from(context).inflate(R.layout.admin_recycle_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder3 holder, int position) {

        holder.name.setText(adminRecycle.get(position).getName());
        holder.season.setText(adminRecycle.get(position).getSeason());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PlaceDetailsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("Id", adminRecycle.get(holder.getBindingAdapterPosition()).getId());
                intent.putExtra("Name", adminRecycle.get(holder.getBindingAdapterPosition()).getName());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return adminRecycle.size();
    }
}
class MyViewHolder3 extends RecyclerView.ViewHolder{

    TextView name, season;
    CardView cardView;
    public MyViewHolder3(@NonNull View itemView) {
        super(itemView);

        name = itemView.findViewById(R.id.name);
        season = itemView.findViewById(R.id.season);
        cardView = itemView.findViewById(R.id.card3);

    }
}