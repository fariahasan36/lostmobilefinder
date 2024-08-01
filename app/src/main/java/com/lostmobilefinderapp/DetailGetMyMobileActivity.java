package com.lostmobilefinderapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionButton;

public class DetailGetMyMobileActivity extends AppCompatActivity {
    DrawerLayout drawerLayout;
    ImageView menu;

    LinearLayout home, getMyPhone, settings, chat, userList, about, logout;

    private TextView detailDesc, detailLocation, detailFinderName;
    private ImageView detailImage;
    String key = "";
    String imageUrl = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_get_my_mobile);

        drawerLayout = findViewById(R.id.drawer_layout);
        menu = findViewById(R.id.menu);
        home = findViewById(R.id.home);
        getMyPhone = findViewById(R.id.getMyPhone);
        settings = findViewById(R.id.settings);
        chat = findViewById(R.id.chat);
        userList = findViewById(R.id.userList);
        about = findViewById(R.id.about);
        logout = findViewById(R.id.logout);

        detailDesc = findViewById(R.id.detailGetMyMobileDescription);
        detailImage = findViewById(R.id.detailGetMyMobileImage);
        detailLocation = findViewById(R.id.detailGetMyMobileLocation);
        detailFinderName = findViewById(R.id.detailGetMyMobileFinderName);

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDrawer(drawerLayout);
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(DetailGetMyMobileActivity.this, MainActivity.class);
            }
        });
        getMyPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(DetailGetMyMobileActivity.this, GetMyLostPhoneActivity.class);
            }
        });
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recreate();
            }
        });
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(DetailGetMyMobileActivity.this, UserActivity.class);
            }
        });
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(DetailGetMyMobileActivity.this, AboutActivity.class);
            }
        });
        userList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(DetailGetMyMobileActivity.this, ListUserActivity.class);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SessionManagement sessionManagement = new SessionManagement(DetailGetMyMobileActivity.this);
                sessionManagement.removeSession();
                redirectActivity(DetailGetMyMobileActivity.this, LoginActivity.class);
            }
        });
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            detailDesc.setText(bundle.getString("Description"));
            detailLocation.setText(bundle.getString("Location"));
            key = bundle.getString("Key");
            imageUrl = bundle.getString("Image");
            detailFinderName.setText(bundle.getString("FinderName"));
            Glide.with(this).load(bundle.getString("Image")).into(detailImage);
        }

    }

    public static void openDrawer(DrawerLayout drawerLayout){
        drawerLayout.openDrawer(GravityCompat.START);
    }
    public static void closeDrawer(DrawerLayout drawerLayout){
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }
    public static void redirectActivity(Activity activity, Class secondActivity){
        Intent intent = new Intent(activity, secondActivity);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.finish();
    }
    @Override
    protected void onPause(){
        super.onPause();
        closeDrawer(drawerLayout);
    }
}