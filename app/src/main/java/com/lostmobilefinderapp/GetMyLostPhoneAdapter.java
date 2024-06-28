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

import java.util.List;

public class GetMyLostPhoneAdapter extends RecyclerView.Adapter<GetMyLostPhoneViewHolder>{
    private Context context;
    private List<FoundMobileModel> dataList;

    public GetMyLostPhoneAdapter(Context context, List<FoundMobileModel> dataList, String finderName) {
        this.context = context;
        this.dataList = dataList;
    }
    @NonNull
    @Override
    public GetMyLostPhoneViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.get_my_phone, parent, false);
        return new GetMyLostPhoneViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GetMyLostPhoneViewHolder holder, int position) {
        Glide.with(context).load(dataList.get(position).getFoundImage()).into(holder.getMyMobileImage);
        holder.getMyMobileLocation.setText(dataList.get(position).getLocation());
        holder.getMyMobileDescription.setText(dataList.get(position).getDescription());
        holder.getMyMobileFinderName.setText(dataList.get(position).getFinderName());

        
        holder.getMyMobileRecCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailGetMyMobileActivity.class);
                intent.putExtra("Image", dataList.get(holder.getAdapterPosition()).getFoundImage());
                intent.putExtra("Description", dataList.get(holder.getAdapterPosition()).getDescription());
                intent.putExtra("Location", dataList.get(holder.getAdapterPosition()).getLocation());
                intent.putExtra("FinderName", dataList.get(holder.getAdapterPosition()).getFinderName());
                intent.putExtra("Key",dataList.get(holder.getAdapterPosition()).getKey());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

//    public void searchDataList(ArrayList<FoundMobileClass> searchList){
//        dataList = searchList;
//        notifyDataSetChanged();
//    }
}
class GetMyLostPhoneViewHolder extends RecyclerView.ViewHolder{

    ImageView getMyMobileImage;
    TextView getMyMobileLocation, getMyMobileDescription;
    CardView getMyMobileRecCard;
    TextView getMyMobileFinderName;

    public GetMyLostPhoneViewHolder(@NonNull View itemView) {
        super(itemView);

        getMyMobileImage = itemView.findViewById(R.id.getMyMobileImage);
        getMyMobileRecCard = itemView.findViewById(R.id.getMyMobileRecCard);
        getMyMobileDescription = itemView.findViewById(R.id.getMyMobileDescription);
        getMyMobileLocation = itemView.findViewById(R.id.getMyMobileLocation);
        getMyMobileFinderName = itemView.findViewById(R.id.getMyMobileFinderName);
    }
}
