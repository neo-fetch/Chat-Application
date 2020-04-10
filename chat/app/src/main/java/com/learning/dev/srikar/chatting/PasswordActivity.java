package com.learning.dev.srikar.chatting;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.annotation.NonNull;
import com.google.android.material.textfield.TextInputEditText;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.learning.dev.srikar.chatting.Classes.UserProfile;

import java.util.HashMap;

public class PasswordActivity extends AppCompatActivity {

    private TextInputEditText oldPassword, newPassword, newPassword1;
    private Button changePassword;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private String NewPassword, OldPassword;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        setupUIV();

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        progressDialog = new ProgressDialog(PasswordActivity.this);

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog.setMessage("Loading...please wait!");
                progressDialog.show();

                final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseAuth.getUid());

                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        final UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);

                        if (inputValidate()){

                            OldPassword = oldPassword.getText().toString().trim();
                            NewPassword = newPassword.getText().toString().trim();

                            if(userProfile.getPassword().equals(OldPassword)){

                                AuthCredential credential = EmailAuthProvider.getCredential(userProfile.getEmail(), userProfile.getPassword());

                                firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if(task.isSuccessful()){

                                            firebaseUser.updatePassword(NewPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    if (task.isSuccessful()){

                                                        firebaseAuth.signOut();
                                                        progressDialog.dismiss();
                                                        showPasswordChangedSuccessful();
                                                    }
                                                }
                                            });
                                        }

                                    }
                                });
                            }

                            else{

                                progressDialog.dismiss();
                                oldPassword.setError("Incorrect password entered, please check");
                            }

                        }

                        else{

                            progressDialog.dismiss();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });




    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void setupUIV(){

        oldPassword    = findViewById(R.id.PAoldPassword);
        newPassword    = findViewById(R.id.PAnewPassword);
        newPassword1   = findViewById(R.id.PAnewPassword1);
        changePassword = findViewById(R.id.PAchangePassword);
    }

    private boolean inputValidate(){

        if(oldPassword.getText().toString().isEmpty()){

            oldPassword.setError("Enter password");
            return false;
        }

        else if(oldPassword.getText().toString().length() < 6){

            oldPassword.setError("Password should be more than 6 characters");
            return false;
        }

        else if (newPassword.getText().toString().isEmpty()){

            newPassword.setError("Enter new password");
            return false;
        }

        else if (newPassword.getText().toString().length() < 6){

            newPassword.setError("Password should be more than 6 characters");
            return false;
        }

        else if (newPassword1.getText().toString().isEmpty()) {

            newPassword1.setError("Enter new again password");
            return false;
        }

        else if (newPassword1.getText().toString().length() < 6){

            newPassword1.setError("Password should be more than 6 characters");
            return false;
        }

        else if(!newPassword.getText().toString().equals(newPassword1.getText().toString())){

            showNotMatchingAlert();
            return false;
        }

        else{

            return true;
        }
    }


    private void showNotMatchingAlert (){

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Alert!");
        builder.setMessage("Entered new passwords don't match. Please check and try again ");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

            }
        });

        builder.create().show();
    }



    private void showPasswordChangedSuccessful(){

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Alert!");
        builder.setMessage("Your account password has been changed. You will have to login again. Press OK to continue");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent i = new Intent(PasswordActivity.this, LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
        });

        builder.create().show();
    }

    private  void  status(String status){

        if(firebaseAuth.getCurrentUser() != null){

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseAuth.getUid());

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("status" , status);

            databaseReference.updateChildren(hashMap);
        }
    }

    @Override
    protected void onPause() {
        status("offline");
        super.onPause();
    }

    @Override
    protected void onResume() {
        status("online");
        super.onResume();
    }
}
