package com.learning.dev.srikar.chatting;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateProfilePicActivity extends AppCompatActivity {

    private CircleImageView ProfilePic;
    private Button UpdateProfilePic;

    private FirebaseAuth firebaseAuth;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private static int PICK_IMG = 123;
    private Uri imagePath;

    private ProgressDialog progressDialog, uploadingDPprogressDialogue;
    private boolean hasOnlineImage  = false;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == PICK_IMG && resultCode == RESULT_OK && data.getData() != null) {
            imagePath = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagePath);
                ProfilePic.setImageBitmap(bitmap);
            } catch (IOException e) {
                Toast.makeText(UpdateProfilePicActivity.this, "Error : " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile_pic);

        setupUIV();

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        final Context applicationContext = this;
        progressDialog = new ProgressDialog(applicationContext);
        progressDialog.setMessage("Loading...please wait");
        progressDialog.show();



        // TODO : also add remove profile picture on the menu in this activity

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        storageReference.child(firebaseAuth.getUid()).child("Profile Pic").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                Glide.with(getApplicationContext()).load(uri).into(ProfilePic);
                hasOnlineImage = true;
                progressDialog.dismiss();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                if (e instanceof StorageException) {

                    ProfilePic.setImageResource(R.drawable.blank_profilepic);
                    hasOnlineImage = false;
                    progressDialog.dismiss();

                }

                else if (e instanceof FirebaseNetworkException) {

                    progressDialog.dismiss();
                    Toast.makeText(UpdateProfilePicActivity.this, "Yor device is not connected to the internet... try again after connecting!", Toast.LENGTH_LONG).show();
                    // TODO : Add FirebaseNetworkException in every picture loading thing

                }
            }
        });


        ProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMG);
            }
        });

        UpdateProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                uploadingDPprogressDialogue = new ProgressDialog(applicationContext);
                uploadingDPprogressDialogue.setMessage("Loading...please wait");
                uploadingDPprogressDialogue.show();
                updateProfilePic();
            }
        });


    }

    private void setupUIV() {

        ProfilePic = findViewById(R.id.UPPprofilePic);
        UpdateProfilePic = findViewById(R.id.UPPupdateProfilePic);

    }

    private void updateProfilePic() {

        if (imagePath != null) {

            StorageReference imageReference = storageReference.child(firebaseAuth.getUid()).child("Profile Pic");
            final UploadTask uploadTask = imageReference.putFile(imagePath);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Toast.makeText(UpdateProfilePicActivity.this, "Profile picture updated successfully!", Toast.LENGTH_LONG).show();
                    uploadingDPprogressDialogue.dismiss();
                    refreshApp();

                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(UpdateProfilePicActivity.this, "Upload of profilePic Failed..... Error : " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    uploadingDPprogressDialogue.dismiss();
                }
            });

        }

        else {
            Toast.makeText(UpdateProfilePicActivity.this, "New profile picture not selected.... click on the pic to select!", Toast.LENGTH_LONG).show();
            uploadingDPprogressDialogue.dismiss();

        }



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_updateprofilepic_removepic, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {

            case R.id.UPPremoveProfilePic: { //Here its wrong

                if ( imagePath != null || hasOnlineImage) {

                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setCancelable(false);
                    builder.setTitle("Alert!");
                    builder.setMessage("Are you sure you want to remove your current Profile picture?");
                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            removeDP();

                        }
                    }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            dialogInterface.dismiss();

                        }
                    });

                    builder.create().show();

                    break;
                }

                else {
                    Toast.makeText(UpdateProfilePicActivity.this, "No dp selected..... so can't remove anything ;)", Toast.LENGTH_LONG).show();
                }

                break;
            }

            case android.R.id.home: {

                finish();
                break;

            }

        }

        return super.onOptionsItemSelected(item);
    }

    private void removeDP() {

        storageReference.child(firebaseAuth.getUid()).child("Profile Pic").delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                imagePath = null;
                Toast.makeText(UpdateProfilePicActivity.this, "Profile pic removed successfully", Toast.LENGTH_LONG).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                int errorCode = ((StorageException) e).getErrorCode();

                if (errorCode == StorageException.ERROR_OBJECT_NOT_FOUND) {

                    imagePath = null;
                    ProfilePic.setImageResource(R.drawable.blank_profilepic);

                }

                else {

                    Toast.makeText(UpdateProfilePicActivity.this, "Error : " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();

                }

            }
        });

    }

    private void refreshApp(){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
        //TODO : the app get exited here.... see properly
    }

}
