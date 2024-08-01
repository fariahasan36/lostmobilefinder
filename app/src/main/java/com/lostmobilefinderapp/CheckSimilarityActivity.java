package com.lostmobilefinderapp;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

public class CheckSimilarityActivity extends AppCompatActivity {
    private static final String TAG = "CheckSimilarityActivity";
    String foundImageUrl, lostImageUrl;
    ImageView foundImageViewToCheckSimilarity, lostImageViewToCheckSimilarity;
    private Bitmap foundImageBitmap, lostImageBitmap;
    private String hash1, hash2;
    private String getHash1, getHash2;
    private BitmapDrawable foundDrawable, lostDrawable;
    private TextView txtLostImageUrl, txtFoundImageUrl;
    private ListView listViewFoundMobile;
    private List<FoundMobileModel> foundMobileModelArrayList = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private double similarityPercentage;

    // get help from  https://opencv.org/
    // Find the similarity of lost and found images
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_similarity);
        if (OpenCVLoader.initDebug()) Log.d("LOADED", "SUCCESS");
        else Log.d("LOADED", "FAILURE");

        BitmapDrawable bitmapDrawable = (BitmapDrawable) ContextCompat.getDrawable(this, R.drawable.my_phone_1);
        Bitmap bitmap = bitmapDrawable.getBitmap();

        BitmapDrawable bitmapDrawable2 = (BitmapDrawable) ContextCompat.getDrawable(this, R.drawable.nokia);
        Bitmap bitmap2 = bitmapDrawable2.getBitmap();

        Mat img1 = new Mat();
        Utils.bitmapToMat(bitmap, img1);

        Mat img2 = new Mat();
        Utils.bitmapToMat(bitmap2, img2);

        hash1 = calculateHash(img1);
        hash2 = calculateHash(img2);

        similarityPercentage = calculateSimilarityPercentage(hash1, hash2);

        Log.d(TAG, "Similarity Percentage Main5:  " + similarityPercentage);

//        lostImageViewToCheckSimilarity = findViewById(R.id.lostImageToCheckSimilarity);
//        foundImageViewToCheckSimilarity = findViewById(R.id.foundImageToCheckSimilarity);
//
//        txtLostImageUrl = findViewById(R.id.lostImageUrl);
//        txtFoundImageUrl = findViewById(R.id.foundImageUrl);
//
//        listViewFoundMobile = findViewById(R.id.listViewFoundMobile);
//
//        SessionManagement sessionManagement = new SessionManagement(CheckSimilarityActivity.this);
//        String username = sessionManagement.getSession();
//        DatabaseReference referenceLostMobile = FirebaseDatabase.getInstance().getReference("lostMobile").child(username);
//
//        referenceLostMobile.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                if (snapshot.exists()) {
//                    // Remove the redundant child(username) calls
//                    String descriptionFromDB = snapshot.child("description").getValue(String.class);
//                    String locationFromDB = snapshot.child("location").getValue(String.class);
//                    String lostImageUrlFromDB = snapshot.child("lostImage").getValue(String.class);
//
//                    // Set the value to the TextView
//                    txtLostImageUrl.setText(lostImageUrlFromDB);
//                    lostImageUrl = lostImageUrlFromDB;
//                    loadLostImageAndProcess(lostImageUrl);
//
//                    setFoundImage();
//                }
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
    }
    private void setFoundImage() {
        DatabaseReference referenceFoundMobile = FirebaseDatabase.getInstance().getReference("foundMobile");

        referenceFoundMobile.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                foundMobileModelArrayList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String childName = snapshot.getKey();

                    FoundMobileModel foundMobileModel = snapshot.getValue(FoundMobileModel.class);
                    foundMobileModelArrayList.add(foundMobileModel);

                    String foundImageUrlFromDB = foundMobileModel.getFoundImage();
                    txtFoundImageUrl.setText(foundImageUrlFromDB);
                    Log.d(TAG, "txtFoundImageUrl" + foundImageUrlFromDB);

                    loadFoundImageAndProcess(foundImageUrlFromDB);
                }
                Log.d(TAG, "Similarity Percentage:  " + similarityPercentage);

                List<String> foundMobileInfoList = new ArrayList<>();
                for (FoundMobileModel foundMobileModel : foundMobileModelArrayList) {
                    foundMobileInfoList.add(foundMobileModel.getFoundImage() + " - " + foundMobileModel.getLocation());

                }

               //  Populate the ListView with user information
                 adapter = new ArrayAdapter<>(CheckSimilarityActivity.this, android.R.layout.simple_list_item_1, foundMobileInfoList);
                listViewFoundMobile.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors.
                Log.d(TAG, "Error fetching data", databaseError.toException());
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
                        lostImageViewToCheckSimilarity.setImageBitmap(resource);

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
    private void processHash1(String setHash1) {
        this.getHash1 = setHash1;
        Log.d(TAG, "hash1 set=" + this.getHash1);

    }
    private void loadFoundImageAndProcess(String foundImageUrl) {

        Glide.with(getApplicationContext())
                .asBitmap()
                .load(foundImageUrl)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        // Set the loaded bitmap to the ImageView
                        foundImageViewToCheckSimilarity.setImageBitmap(resource);
                        Mat img2 = new Mat();
                        Utils.bitmapToMat(resource, img2);

                        hash2 = calculateHash(img2);
                        processHash2(hash2);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        // Handle placeholder if needed
                    }
                });
    }
    private void processHash2(String setHash2) {
        this.getHash2 = setHash2;
        Log.d(TAG, "hash2 set=" + this.getHash2);
        checkSimilarity(getHash1, getHash2);
    }

    private void checkSimilarity(String hash1, String hash2) {
        this.similarityPercentage = calculateSimilarityPercentage(hash1, hash2);
        Log.d(TAG, "Similarity Percentage Main:  " + similarityPercentage);
    }

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
}