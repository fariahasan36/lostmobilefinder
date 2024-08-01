package com.lostmobilefinderapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class GetMyLostPhoneActivity extends AppCompatActivity {

    private static final String TAG = "CheckSimilarityActivity";
    private String foundImageUrl, lostImageUrl;
    private Bitmap foundImageBitmap, lostImageBitmap;
    private String hash1, hash2;
    private String getHash1, getHash2;
    private String finderName;
    private BitmapDrawable foundDrawable, lostDrawable;
    private List<FoundMobileModel> foundMobileModelArrayList = new ArrayList<>();
    private double similarityPercentage;
    private RecyclerView recyclerView;
    private ArrayList<FoundMobileModel> dataList;
    private GetMyLostPhoneAdapter adapter;
    final private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("foundMobile");
    DrawerLayout drawerLayout;
    ImageView menu;
    LinearLayout home, getMyPhone, settings, chat, userList, about, logout;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_my_lost_phone);
        //start of menu
        drawerLayout = findViewById(R.id.drawer_layout);
        menu = findViewById(R.id.menu);
        home = findViewById(R.id.home);
        getMyPhone = findViewById(R.id.getMyPhone);
        settings = findViewById(R.id.settings);
        chat = findViewById(R.id.chat);
        userList = findViewById(R.id.userList);
        about = findViewById(R.id.about);
        logout = findViewById(R.id.logout);

        recyclerView = findViewById(R.id.getLostMobileView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dataList = new ArrayList<>();
        adapter = new GetMyLostPhoneAdapter(this, dataList, finderName);
        recyclerView.setAdapter(adapter);

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDrawer(drawerLayout);
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(GetMyLostPhoneActivity.this, MainActivity.class);
            }
        });
        getMyPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recreate();
            }
        });
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(GetMyLostPhoneActivity.this, SettingsActivity.class);
            }
        });
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(GetMyLostPhoneActivity.this, UserActivity.class);
            }
        });
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(GetMyLostPhoneActivity.this, AboutActivity.class);
            }
        });
        userList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(GetMyLostPhoneActivity.this, ListUserActivity.class);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SessionManagement sessionManagement = new SessionManagement(GetMyLostPhoneActivity.this);
                sessionManagement.removeSession();
                redirectActivity(GetMyLostPhoneActivity.this, LoginActivity.class);
            }
        });

        if (OpenCVLoader.initDebug()) Log.d("LOADED", "SUCCESS");
        else Log.d("LOADED", "FAILURE");

        SessionManagement sessionManagement = new SessionManagement(GetMyLostPhoneActivity.this);
        String username = sessionManagement.getSession();
        DatabaseReference referenceLostMobile = FirebaseDatabase.getInstance().getReference("lostMobile").child(username);

        referenceLostMobile.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Remove the redundant child(username) calls
                    String descriptionFromDB = snapshot.child("description").getValue(String.class);
                    String locationFromDB = snapshot.child("location").getValue(String.class);
                    String lostImageUrlFromDB = snapshot.child("lostImage").getValue(String.class);

                    // Set the value to the TextView
                    lostImageUrl = lostImageUrlFromDB;
                    Log.d(TAG, "lostImageUrl come from get lost mobile" + lostImageUrl);
                    Log.d(TAG, "lostImageUrl" + lostImageUrl);
                    loadLostImageAndProcess(lostImageUrl);
                    setFoundImage();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadLostImageAndProcess(String lostImageUrl) {
        Glide.with(this)
                .asBitmap()
                .load(lostImageUrl)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        // Set the loaded bitmap to the ImageView
                        //   lostImageViewToCheckSimilarity.setImageBitmap(resource);
                        Mat img1 = new Mat();
                        Utils.bitmapToMat(resource, img1);

                        hash1 = calculateHash(img1);
                        processHash1(hash1);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        // Handle placeholder if needed
                    }
                });
    }

    private void setFoundImage() {
        DatabaseReference referenceFoundMobile = FirebaseDatabase.getInstance().getReference("foundMobile");

        referenceFoundMobile.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                foundMobileModelArrayList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    FoundMobileModel foundMobileModel = snapshot.getValue(FoundMobileModel.class);
                    foundMobileModelArrayList.add(foundMobileModel);
//
//                    String foundImageUrlFromDB = foundMobileModel.getFoundImage();
//                    Log.d(TAG, "foundImageUrlFromDB" + foundImageUrlFromDB);

                    loadFoundImageAndProcess(foundMobileModel);
                }
                Log.d(TAG, "Similarity Percentage:  " + similarityPercentage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors.
                Log.d(TAG, "Error fetching data", databaseError.toException());
            }
        });
    }

    private void processHash1(String setHash1) {
        this.getHash1 = setHash1;
        Log.d(TAG, "hash1 set=" + this.getHash1);
    }

    private void loadFoundImageAndProcess(FoundMobileModel foundMobileModel) {

        Glide.with(getApplicationContext())
                .asBitmap()
                .load(foundMobileModel.getFoundImage())
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        // Set the loaded bitmap to the ImageView
                        Mat img2 = new Mat();
                        Utils.bitmapToMat(resource, img2);

                        hash2 = calculateHash(img2);
                        processHash2(hash2, foundMobileModel);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        // Handle placeholder if needed
                    }
                });
    }

    private void processHash2(String setHash2, FoundMobileModel foundMobileModel) {
        this.getHash2 = setHash2;
        Log.d(TAG, "hash2 set=" + this.getHash2);
        //checkSimilarity(getHash1, getHash2);
        this.similarityPercentage = calculateSimilarityPercentage(hash1, this.getHash2);
        Log.d(TAG, "Similarity Percentage Main:  " + similarityPercentage);

//        AlertDialog.Builder builder = new AlertDialog.Builder(GetMyLostPhoneActivity.this);
//        builder.setCancelable(false);
//        builder.setView(R.layout.progress_layout);
//        AlertDialog dialog = builder.create();
//        dialog.show();

        if (this.similarityPercentage > 60) {
            this.finderName = foundMobileModel.getFinderName();
            dataList.add(foundMobileModel);
            adapter.notifyDataSetChanged();
            //dialog.dismiss();
        }

    }

    //    private void checkSimilarity(String hash1, String hash2) {
//        this.similarityPercentage = calculateSimilarityPercentage(hash1, hash2);
//        Log.d(TAG, "Similarity Percentage Main:  " + similarityPercentage);
//    }
// Function to calculate Average Hash
    public static String calculateHash(Mat image) {
        Imgproc.resize(image, image, new Size(8, 8));
        Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2GRAY);

        Scalar mean = Core.mean(image);
        Mat hash = new Mat(8, 8, CvType.CV_8U, new Scalar(0));

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (image.get(i, j)[0] >= mean.val[0]) {
                    hash.put(i, j, 1);
                }
            }
        }

        StringBuilder hashString = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                hashString.append((int) hash.get(i, j)[0]);
            }
        }
        return hashString.toString();
    }

    // Function to calculate similarity percentage
    public static double calculateSimilarityPercentage(String hash1, String hash2) {
        int hammingDistance = 0;
        for (int i = 0; i < hash1.length(); i++) {
            if (hash1.charAt(i) != hash2.charAt(i)) {
                hammingDistance++;
            }
        }
        return (1 - (double) hammingDistance / (hash1.length())) * 100;
    }

    //start of menu
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
    //end of menu
}