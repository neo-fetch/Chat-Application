package com.learning.dev.srikar.chatting;

import android.app.ProgressDialog;
import android.content.Intent;
import androidx.annotation.NonNull;
import com.google.android.material.textfield.TextInputEditText;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.learning.dev.srikar.chatting.Classes.UserProfile;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText Email, Password;
    private Button Login;
    private ProgressDialog progressDialog;
    private TextView ForgotPassword;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);

        setupUIV();

        // TODO : Add phone number with otp login

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(inputValidateEmail()&&inputValidatePassword()){

                    infoValidate(Email.getText().toString().trim(), Password.getText().toString().trim());
                }
            }
        });

        ForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(LoginActivity.this, ForgotActivity.class));
            }
        });

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

    private void setupUIV(){

        Email          = findViewById(R.id.LAemail);
        Password       = findViewById(R.id.LApassword);
        Login          = findViewById(R.id.LAlogin);
        ForgotPassword = findViewById(R.id.LAforgotPassword);

    }

    private boolean inputValidateEmail(){

        if(Email.getText().toString().isEmpty()){

            Email.setError("Enter Email-ID");
            return false;
        }

        else{
            return true;
        }
    }



    private boolean inputValidatePassword(){

        if(Password.getText().toString().isEmpty()){

            Password.setError("Enter Password");
            return false;
        }

        else{
            return true;
        }
    }


    private void infoValidate(String FEmail, final String FPassword){

        progressDialog.setMessage("Loading...please wait!");
        progressDialog.show();



        firebaseAuth.signInWithEmailAndPassword(FEmail, FPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    if(checkEmailVerification()) {


                        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseAuth.getUid());
                        databaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);

                                if (!userProfile.getPassword().equals(FPassword)){

                                    databaseReference.child("password").setValue(FPassword);

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        progressDialog.dismiss();
                        finish();
                        startActivity(new Intent(LoginActivity.this, ChatActivity.class));
                    }

                    else {

                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "Account not verified !  Check your inbox for verification email", Toast.LENGTH_LONG).show();
                        firebaseAuth.signOut();

                    }

                }

                else{

                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Login Failed!", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private Boolean checkEmailVerification(){

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        Boolean emailCheck = firebaseUser.isEmailVerified();
        return emailCheck;
    }

    @Override
    public void onBackPressed() {

        finish();
    }
}
