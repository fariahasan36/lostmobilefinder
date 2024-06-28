package com.lostmobilefinderapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CreateLostMobileActivity extends AppCompatActivity {

    //start menu
    DrawerLayout drawerLayout;
    ImageView menu;
    LinearLayout home, getMyPhone, settings, share, about, logout;
    Button postLostMobile, postFindMobile;
    //end menu

    ImageView createLostImage;
    Button saveLostButton;
    EditText createLostLocation, createLostDescription;
    private String imageURL;
    Uri uri;
    private String hash1, hash2;
    private String getHash1, getHash2;
    private String finderName;
    private BitmapDrawable foundDrawable, lostDrawable;
    private double similarityPercentage;
    private Mat img1, img2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_lost_mobile);

        //start menu
        drawerLayout = findViewById(R.id.drawer_layout);
        menu = findViewById(R.id.menu);
        home = findViewById(R.id.home);
        getMyPhone = findViewById(R.id.getMyPhone);
        settings = findViewById(R.id.settings);
        share = findViewById(R.id.share);
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
                redirectActivity(CreateLostMobileActivity.this, MainActivity.class);
            }
        });
        getMyPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(CreateLostMobileActivity.this, GetMyLostPhoneActivity.class);
            }
        });
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(CreateLostMobileActivity.this, SettingsActivity.class);
            }
        });
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(CreateLostMobileActivity.this, AboutActivity.class);
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectActivity(CreateLostMobileActivity.this, ShareActivity.class);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SessionManagement sessionManagement = new SessionManagement(CreateLostMobileActivity.this);
                sessionManagement.removeSession();
                redirectActivity(CreateLostMobileActivity.this, LoginActivity.class);
            }
        });
        //end menu
        createLostImage = findViewById(R.id.createLostMobileImage);
        createLostLocation = findViewById(R.id.createLostMobileLocation);
        createLostDescription = findViewById(R.id.createLostMobileDescription);
        saveLostButton = findViewById(R.id.saveLostButton);

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            uri = data.getData();
                            createLostImage.setImageURI(uri);
                        } else {
                            Toast.makeText(CreateLostMobileActivity.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
        createLostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });
        saveLostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveLostData();
            }
        });

        if (OpenCVLoader.initDebug()) Log.d("LOADED", "SUCCESS");
        else Log.d("LOADED", "FAILURE");

        img1 = new Mat();
        img2 = new Mat();
    }

    public void saveLostData() {

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Lost Mobile Images")
                .child(uri.getLastPathSegment());

        AlertDialog.Builder builder = new AlertDialog.Builder(CreateLostMobileActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete()) ;
                Uri urlImage = uriTask.getResult();
                imageURL = urlImage.toString();
                createLostData();
                loadLostImageAndProcess();
                dialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
            }
        });
    }
    public void createLostData() {

        String location = createLostLocation.getText().toString();
        String description = createLostDescription.getText().toString();
        SessionManagement sessionManagement = new SessionManagement(CreateLostMobileActivity.this);

        LostMobileModel dataClass = new LostMobileModel(location, description, imageURL, sessionManagement.getSession());

        // We are changing the child from title to currentDate,
        // because we will be updating title as well and it may affect child value.
        // String currentDate = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

        FirebaseDatabase.getInstance().getReference("lostMobile").child(sessionManagement.getSession())
                .setValue(dataClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(CreateLostMobileActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CreateLostMobileActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadLostImageAndProcess() {
        Log.d("TAG", "imageURL imageURL" + imageURL);

        Glide.with(this)
                .asBitmap()
                .load(imageURL)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        // Set the loaded bitmap to the ImageView
                        //   lostImageViewToCheckSimilarity.setImageBitmap(resource);
                        //Mat img1 = new Mat();
                        Utils.bitmapToMat(resource, img1);

                        Log.d("Tag", "img1 = ");
                        hash1 = calculateHash(img1);
                        Log.d("Tag", "hash1 = " + hash1);
                        processHash1(hash1);
                        Log.d("TAG", "processHash1 is called");
                        setFoundImage();
                        Log.d("TAG", "set found image");
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

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    FoundMobileModel foundMobileModel = snapshot.getValue(FoundMobileModel.class);
                    loadFoundImageAndProcess(foundMobileModel);
                }
                Log.d("TAG", "Similarity Percentage:  " + similarityPercentage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors.
                Log.d("TAG", "Error fetching data", databaseError.toException());
            }
        });
    }

    private void processHash1(String setHash1) {
        this.getHash1 = setHash1;
        Log.d("hash1", "hash1 set=" + this.getHash1);
    }

    private void loadFoundImageAndProcess(FoundMobileModel foundMobileModel) {
        Glide.with(getApplicationContext())
                .asBitmap()
                .load(foundMobileModel.getFoundImage())
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        // Set the loaded bitmap to the ImageView
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
        Log.d("TAG", "hash2 set=" + this.getHash2);
        //checkSimilarity(getHash1, getHash2);
        this.similarityPercentage = calculateSimilarityPercentage(hash1, this.getHash2);
        Log.d("TAG", "Similarity Percentage Main:  " + similarityPercentage);
        Log.d("TAG", "hash1:  " + hash1);
        Log.d("TAG", "get Hash1:  " + this.getHash1);

        if (this.similarityPercentage > 60) {
            SessionManagement sessionManagement = new SessionManagement(CreateLostMobileActivity.this);

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
            Query checkUserDatabase = reference.orderByChild("username").equalTo(sessionManagement.getSession());

            checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (snapshot.exists()) {
                        String nameFromDB = snapshot.child(sessionManagement.getSession()).child("name").getValue(String.class);
                        String emailFromDB = snapshot.child(sessionManagement.getSession()).child("email").getValue(String.class);
                        //   String usernameFromDB = snapshot.child(sessionManagement.getSession()).child("username").getValue(String.class);

                        String subject = "Found Your Lost Mobile";
                        String message = "Hi " + nameFromDB + "," + "\n\nA phone has been found that matches the description of your lost phone. Please contact " + foundMobileModel.getFinderName() + ". The finder may be able to help you with more information. Thank you.\n\nCheers!\nLost Mobile Finder Team";
                        Log.d("TAG", "EmailHelper is calling");

                        EmailHelper.sendEmail(CreateLostMobileActivity.this, emailFromDB, subject, message);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
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

    //start menu
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
    //end menu
}