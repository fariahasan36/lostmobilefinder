package com.lostmobilefinderapp;

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

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class FoundMobileAdapter extends RecyclerView.Adapter<FoundMobileViewHolder> {
    private Context context;
    private List<FoundMobileModel> dataList;

    public FoundMobileAdapter(Context context, List<FoundMobileModel> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public FoundMobileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.found_mobile, parent, false);
        return new FoundMobileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoundMobileViewHolder holder, int position) {
        SessionManagement sessionManagement = new SessionManagement(context);
        String username = sessionManagement.getSession();
        if (sessionManagement.getSession().equals("admin") || dataList.get(position).getFinderName().equals(username)) {
            Glide.with(context).load(dataList.get(position).getFoundImage()).into(holder.foundMobileImage);
        }
        holder.foundLocation.setText(dataList.get(position).getLocation());
        holder.foundDescription.setText(dataList.get(position).getDescription());
        holder.finderName.setText(dataList.get(position).getFinderName());

        holder.foundRecCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailFoundMobileActivity.class);
                intent.putExtra("FinderName", dataList.get(holder.getAdapterPosition()).getFinderName());
                intent.putExtra("Image", dataList.get(holder.getAdapterPosition()).getFoundImage());
                intent.putExtra("Description", dataList.get(holder.getAdapterPosition()).getDescription());
                intent.putExtra("Location", dataList.get(holder.getAdapterPosition()).getLocation());
                intent.putExtra("Key", dataList.get(holder.getAdapterPosition()).getKey());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void searchDataList(ArrayList<FoundMobileModel> searchList) {
        dataList = searchList;
        notifyDataSetChanged();
    }
}

class FoundMobileViewHolder extends RecyclerView.ViewHolder {

    ImageView foundMobileImage;
    TextView foundLocation, foundDescription, finderName;
    CardView foundRecCard;
    String username;

    public FoundMobileViewHolder(@NonNull View itemView) {
        super(itemView);

        foundMobileImage = itemView.findViewById(R.id.foundMobileImage);
        foundRecCard = itemView.findViewById(R.id.foundRecCard);
        foundDescription = itemView.findViewById(R.id.foundDescription);
        foundLocation = itemView.findViewById(R.id.foundLocation);
        finderName = itemView.findViewById(R.id.finderName);
    }
}