package com.lostmobilefinderapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListUserActivity extends AppCompatActivity {
    private SearchView searchView;
    private DrawerLayout drawerLayout;
    private ImageView menu;

    private LinearLayout home, getMyPhone, settings, userList, about, chat, logout;
    private RecyclerView recyclerView;
    private ListUserAdapter listUserAdapter;
    private List<UserModel> dataList;
    private String yourName;
    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_user);

        drawerLayout = findViewById(R.id.drawer_layout);
        menu = findViewById(R.id.menu);
        home = findViewById(R.id.home);
        getMyPhone = findViewById(R.id.getMyPhone);
        settings = findViewById(R.id.settings);
        userList = findViewById(R.id.userList);
        chat = findViewById(R.id.chat);
        about = findViewById(R.id.about);
        logout = findViewById(R.id.logout);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDrawer(drawerLayout);
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(ListUserActivity.this, MainActivity.class);
            }
        });
        getMyPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(ListUserActivity.this, GetMyLostPhoneActivity.class);
            }
        });
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(ListUserActivity.this, SettingsActivity.class);
            }
        });
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(ListUserActivity.this, UserActivity.class);
            }
        });
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(ListUserActivity.this, AboutActivity.class);
            }
        });
        userList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recreate();
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SessionManagement sessionManagement = new SessionManagement(ListUserActivity.this);
                sessionManagement.removeSession();
                redirectActivity(ListUserActivity.this, LoginActivity.class);
            }
        });

//        Toolbar toolbar = findViewById(R.id.userToolbar);
//        setSupportActionBar(toolbar);
        SessionManagement sessionManagement = new SessionManagement(ListUserActivity.this);

        String userName = sessionManagement.getSession();
       // getSupportActionBar().setTitle(userName);

     //   listUserAdapter = new ListUserAdapter(this);
        recyclerView = findViewById(R.id.listUserView);

//        recyclerView.setAdapter(listUserAdapter);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchView = findViewById(R.id.searchListUser);
        searchView.clearFocus();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(ListUserActivity.this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);

//        AlertDialog.Builder builder = new AlertDialog.Builder(ListUserActivity.this);
//        builder.setCancelable(false);
//        builder.setView(R.layout.progress_layout);
//        AlertDialog dialog = builder.create();
//        dialog.show();

        dataList = new ArrayList<>();

        listUserAdapter = new ListUserAdapter(ListUserActivity.this, dataList);
        recyclerView.setAdapter(listUserAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listUserAdapter.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String uId = dataSnapshot.getKey();
                    UserModel userModel = dataSnapshot.getValue(UserModel.class);
                    if (userModel != null && userModel.getUsername() != null) {
                        listUserAdapter.add(userModel);
                    }
                    List<UserModel> userModelList = listUserAdapter.getUserModelList();
                    listUserAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchList(newText);
                return true;
            }
        });
    }
    public void searchList(String text){
        ArrayList<UserModel> searchList = new ArrayList<>();
        for (UserModel dataClass: dataList){
            if (dataClass.getUsername().toLowerCase().contains(text.toLowerCase())){
                searchList.add(dataClass);
            }
        }
        listUserAdapter.searchDataList(searchList);
    }
    public static void openDrawer(DrawerLayout drawerLayout) {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public static void closeDrawer(DrawerLayout drawerLayout) {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    public static void redirectActivity(Activity activity, Class secondActivity) {
        Intent intent = new Intent(activity, secondActivity);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer(drawerLayout);
    }
}