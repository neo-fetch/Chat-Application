    package com.learning.dev.srikar.chatting;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.learning.dev.srikar.chatting.Classes.UserProfile;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity {

    private CircleImageView ProfilePic;
    private TextView Username;
    private Button StartChatting;
    private ProgressDialog progressDialog;

    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseStorage firebaseStorage;

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);


        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ProfilePic    = findViewById(R.id.UPAprofilePic);
        Username      = findViewById(R.id.UPAname);
        StartChatting = findViewById(R.id.UPAstartChatting);


        intent = getIntent();
        final String UserID = intent.getStringExtra("Userid");

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...please wait!");
        progressDialog.show();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        final StorageReference storageReference = firebaseStorage.getReference();

        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(UserID);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                final UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                Username.setText("Name : " + userProfile.getName());
                storageReference.child((UserID)).child("Profile Pic").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        Glide.with(getApplicationContext()).load(uri).into(ProfilePic);
                        progressDialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        if (e instanceof StorageException){

                            ProfilePic.setImageResource(R.drawable.blank_profilepic);
                            progressDialog.dismiss();

                        }

                        else {

                            progressDialog.dismiss();

                            Toast.makeText(UserProfileActivity.this, "Error : " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();

                        }

                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(UserProfileActivity.this,"Failed to retrieve data...... please try again later", Toast.LENGTH_LONG).show();
            }
        });

        StartChatting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(UserProfileActivity.this, MessageActivity.class);
                intent.putExtra("Userid", UserID);
                finish();
                startActivity(intent);
            }
        });

    }

    @Override
    public void onBackPressed() {

        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) ;
        {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private  void  status(String status){

        databaseReference = firebaseDatabase.getReference("Users").child(firebaseUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status" , status);

        databaseReference.updateChildren(hashMap);

    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");

    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }

}
