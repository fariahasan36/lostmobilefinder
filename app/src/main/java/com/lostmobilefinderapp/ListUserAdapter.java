package com.lostmobilefinderapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class ListUserAdapter extends RecyclerView.Adapter<ListUserAdapter.ListUserViewHolder> {
    // get help from  Android Studio Tutorial
    // Signup a user
    private Context context;
    private List<UserModel> userModelList;
    private DatabaseReference reference;

    public ListUserAdapter(Context context, List<UserModel> userModelList) {
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
    public ListUserAdapter.ListUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_user_row, parent, false);
        return new ListUserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListUserAdapter.ListUserViewHolder holder, int position) {
        UserModel userClass = userModelList.get(position);
        holder.name.setText(userClass.getName());
        holder.username.setText(userClass.getUsername());
        holder.email.setText(userClass.getEmail());
        Glide.with(context).load(userClass.getUserImage()).into(holder.imageView);

        SessionManagement sessionManagement = new SessionManagement(context);
        String username = sessionManagement.getSession();

//        holder.deleteUserButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
                if (sessionManagement.getSession().equals("admin")) {
                    holder.deleteUserButton.setVisibility(View.VISIBLE);
                } else {
                    holder.deleteUserButton.setVisibility(View.INVISIBLE);
                }
//            }
//        });

        holder.deleteUserButton.setOnClickListener(new View.OnClickListener() {
                                               @Override
                                               public void onClick(View v) {
//                                                   Intent intent = new Intent(context, ChatActivity.class);
//                                                   intent.putExtra("username", userClass.getUsername());
//                                                   intent.putExtra("name", userClass.getName());
//                                                   intent.putExtra("email", userClass.getEmail());
//                                                   context.startActivity(intent);

                                                   reference = FirebaseDatabase.getInstance().getReference("users");

                                                   reference.child(userClass.getUsername()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                       @Override
                                                       public void onComplete(@NonNull Task<Void> task) {
                                                           if (task.isSuccessful()) {
                                                               // Data successfully deleted
                                                               Toast.makeText(context, "User " + userClass.getUsername() + " deleted successfully.", Toast.LENGTH_SHORT).show();
                                                           } else {
                                                               // Failed to delete data
                                                               Toast.makeText(context, "Failed to delete data.", Toast.LENGTH_SHORT).show();
                                                           }
                                                       }
                                                   });
                                                   Intent intent = new Intent(context, ListUserActivity.class);
                                                   context.startActivity(intent);

                                               }
                                           }
        );
    }

    @Override
    public int getItemCount() {
        return userModelList.size();
    }
    public void searchDataList(ArrayList<UserModel> searchList){
        userModelList = searchList;
        notifyDataSetChanged();
    }

    public List<UserModel> getUserModelList(){
        return userModelList;
    }

    public class ListUserViewHolder extends RecyclerView.ViewHolder {
        private TextView name, email, username;
        private FloatingActionButton deleteUserButton;
        private ImageView imageView;
        public ListUserViewHolder(@NonNull View itemView){
            super(itemView);
            name = itemView.findViewById(R.id.list_user_name);
            username = itemView.findViewById(R.id.list_user_username);
            email = itemView.findViewById(R.id.list_user_email);
            imageView = itemView.findViewById(R.id.list_user_image);
            deleteUserButton = itemView.findViewById(R.id.deleteUserButton);
        }
    }
}

