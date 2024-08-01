package com.lostmobilefinderapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    // get help from  Android Studio Tutorial
    // Signup a user
    private Context context;
    private List<UserModel> userModelList;

    public UserAdapter(Context context, List<UserModel> userModelList) {
        this.context = context;
        this.userModelList = userModelList;
    }

    public void add(UserModel userClass){
        userModelList.add(userClass);
    }

    public void clear(){
        userModelList.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserAdapter.UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_row, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.UserViewHolder holder, int position) {
        UserModel userClass = userModelList.get(position);
        holder.name.setText(userClass.getName());
        holder.username.setText(userClass.getUsername());
        holder.email.setText(userClass.getEmail());
        Glide.with(context).load(userClass.getUserImage()).into(holder.imageView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
                                               @Override
                                               public void onClick(View v) {
                                                   Intent intent = new Intent(context, ChatActivity.class);
                                                   intent.putExtra("username", userClass.getUsername());
                                                   intent.putExtra("name", userClass.getName());
                                                   intent.putExtra("email", userClass.getEmail());
                                                   context.startActivity(intent);
                                               }
                                           }
        );
    }

    @Override
    public int getItemCount() {
        return userModelList.size();
    }

    public List<UserModel> getUserModelList(){
        return userModelList;
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        private TextView name, email, username;
        private ImageView imageView;
        public UserViewHolder(@NonNull View itemView){
            super(itemView);
            name = itemView.findViewById(R.id.chat_user_name);
            username = itemView.findViewById(R.id.chat_user_username);
            email = itemView.findViewById(R.id.chat_user_email);
            imageView = itemView.findViewById(R.id.chat_user_Image);
        }
    }
}
