package com.example.trippoapp.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trippoapp.AdminDetailedActivity;
import com.example.trippoapp.PlaceDetailsActivity;
import com.example.trippoapp.R;
import com.example.trippoapp.model.AdminRecycleClass;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class RecycleAdminAdapter extends RecyclerView.Adapter<MyViewHolder2> {
    private Context context;
    private List<AdminRecycleClass> adminRecycle;

    public RecycleAdminAdapter(Context context, List<AdminRecycleClass> adminRecycle) {
        this.context = context;
        this.adminRecycle = adminRecycle;
    }

    @NonNull
    @Override
    public MyViewHolder2 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder2(LayoutInflater.from(context).inflate(R.layout.admin_recycle_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder2 holder, int position) {

        holder.name.setText(adminRecycle.get(position).getName());
        holder.season.setText(adminRecycle.get(position).getSeason());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AdminDetailedActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("name", adminRecycle.get(holder.getBindingAdapterPosition()).getName());
                intent.putExtra("season", adminRecycle.get(holder.getBindingAdapterPosition()).getSeason());
                intent.putExtra("id", adminRecycle.get(holder.getBindingAdapterPosition()).getId());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return adminRecycle.size();
    }
}

class MyViewHolder2 extends RecyclerView.ViewHolder{

    TextView name, season;
    CardView cardView;
    public MyViewHolder2(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.name);
        season = itemView.findViewById(R.id.season);
        cardView = itemView.findViewById(R.id.card3);
    }
}
