package com.lostmobilefinderapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class UpdateMyProfileActivity extends AppCompatActivity {
    //    String updateUsername;
    Button updateMyProfileButton;
    DrawerLayout drawerLayout;
    ImageView menu, myImage;
    String imageUrl, key;
    TextView updateMyName, updateMyEmail, updateMyPassword;
    LinearLayout home, getMyPhone, settings, chat, share, about, logout;
    //    FloatingActionButton editMyProfile;
    Uri uri;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_my_profile);

        drawerLayout = findViewById(R.id.drawer_layout);
        menu = findViewById(R.id.menu);
        home = findViewById(R.id.home);
        getMyPhone = findViewById(R.id.getMyPhone);
        settings = findViewById(R.id.settings);
        chat = findViewById(R.id.chat);
        share = findViewById(R.id.share);
        about = findViewById(R.id.about);
        logout = findViewById(R.id.logout);

        updateMyName = findViewById(R.id.updateMyName);
        updateMyEmail = findViewById(R.id.updateMyEmail);
        updateMyPassword = findViewById(R.id.updateMyPassword);
        updateMyProfileButton = findViewById(R.id.updateMyProfileButton);
//        myImage = findViewById(R.id.updateMyImage);


        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDrawer(drawerLayout);
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(UpdateMyProfileActivity.this, MainActivity.class);
            }
        });
        getMyPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(UpdateMyProfileActivity.this, GetMyLostPhoneActivity.class);
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
                redirectActivity(UpdateMyProfileActivity.this, UserActivity.class);
            }
        });
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(UpdateMyProfileActivity.this, AboutActivity.class);
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(UpdateMyProfileActivity.this, ShareActivity.class);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SessionManagement sessionManagement = new SessionManagement(UpdateMyProfileActivity.this);
                sessionManagement.removeSession();
                redirectActivity(UpdateMyProfileActivity.this, LoginActivity.class);
            }
        });

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            // Glide.with(UpdateFoundMobileActivity.this).load(bundle.getString("Image")).into(updateFoundImage);
            updateMyName.setText(bundle.getString("Name"));
            updateMyEmail.setText(bundle.getString("Email"));
            key = bundle.getString("Key");
        }

        Log.d("UserKey", "UserKey" + key);

        updateMyProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SessionManagement sessionManagement = new SessionManagement(UpdateMyProfileActivity.this);

                // Get the Firebase database reference
                databaseReference = FirebaseDatabase.getInstance().getReference("users").child(key);

                // Retrieve input values
                String name = updateMyName.getText().toString();
                String email = updateMyEmail.getText().toString();
                String password = updateMyPassword.getText().toString();

                // Validate inputs
                if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(UpdateMyProfileActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create UserModel object
                UserModel helperClass = new UserModel(name, email, sessionManagement.getSession(), password);

                // Update the user's profile in Firebase
                databaseReference.setValue(helperClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(UpdateMyProfileActivity.this, "Your profile updated successfully!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(UpdateMyProfileActivity.this, SettingsActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(UpdateMyProfileActivity.this, "Failed to update profile. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
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