package com.lostmobilefinderapp;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class LostMobileAdapter extends RecyclerView.Adapter<LostMobileViewHolder> {

    private Context context;
    private List<LostMobileModel> dataList;

    public LostMobileAdapter(Context context, List<LostMobileModel> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public LostMobileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lost_mobile, parent, false);
        return new LostMobileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LostMobileViewHolder holder, int position) {
        SessionManagement sessionManagement = new SessionManagement(context);
        String username = sessionManagement.getSession();

        if (sessionManagement.getSession().equals("admin") || dataList.get(position).getOwnerName().equals(username)) {
            Glide.with(context).load(dataList.get(position).getLostImage()).into(holder.lostMobileImage);
        }
        holder.lostLocation.setText(dataList.get(position).getLocation());
        holder.lostDescription.setText(dataList.get(position).getDescription());
        holder.ownerName.setText(dataList.get(position).getOwnerName());

        holder.lostRecCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailLostMobileActivity.class);
                intent.putExtra("Image", dataList.get(holder.getAdapterPosition()).getLostImage());
                intent.putExtra("Description", dataList.get(holder.getAdapterPosition()).getDescription());
                intent.putExtra("Location", dataList.get(holder.getAdapterPosition()).getLocation());
                intent.putExtra("OwnerName", dataList.get(holder.getAdapterPosition()).getOwnerName());
                intent.putExtra("Key",dataList.get(holder.getAdapterPosition()).getKey());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void searchDataList(ArrayList<LostMobileModel> searchList){
        dataList = searchList;
        notifyDataSetChanged();
    }
}

class LostMobileViewHolder extends RecyclerView.ViewHolder{

    ImageView lostMobileImage;
    TextView lostLocation, lostDescription, ownerName;
    CardView lostRecCard;
    String username;

    public LostMobileViewHolder(@NonNull View itemView) {
        super(itemView);

        lostMobileImage = itemView.findViewById(R.id.lostMobileImage);
        lostRecCard = itemView.findViewById(R.id.lostRecCard);
        lostDescription = itemView.findViewById(R.id.lostDescription);
        lostLocation = itemView.findViewById(R.id.lostLocation);
        ownerName = itemView.findViewById(R.id.ownerName);
    }
}
