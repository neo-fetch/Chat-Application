package com.learning.dev.srikar.chatting;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputEditText;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.learning.dev.srikar.chatting.Classes.UserProfile;

import java.io.IOException;

public class RegistrationActivity extends AppCompatActivity {

    private TextInputEditText Name, Email, Password,PasswordConf;
    private ImageView ProfilePic;
    private Button Register;
    private ProgressDialog progressDialog;

    private static int PICK_IMG = 123;
    private Uri imagePath;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;

    String user_email, user_password, user_name, user_ID;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode == PICK_IMG && resultCode == RESULT_OK && data.getData() != null){
            imagePath = data.getData();

           try{
               Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagePath);
               ProfilePic.setImageBitmap(bitmap);
           }
           catch (IOException e){
               Toast.makeText(RegistrationActivity.this, "Error : " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
           }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        setupUIV();

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        // TODO : Add registration via phone number

        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(inputValidateName()&& inputValidateEmail()&& inputValidatePassword()&& inputValidatePassword2() && PasswordChecker()){

                    user_name     = Name.getText().toString();
                    user_email    = Email.getText().toString().trim();
                    user_password = Password.getText().toString().trim();

                    progressDialog.setMessage("Loading...please wait!");
                    progressDialog.show();

                    firebaseAuth.createUserWithEmailAndPassword(user_email, user_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()){

                                sendEmailVerification();
                                progressDialog.dismiss();
                            }

                            else{

                                progressDialog.dismiss();

                                if (task.getException() instanceof FirebaseNetworkException){
                                    Toast.makeText(RegistrationActivity.this, "Your device is offline... connect to the internet and try again", Toast.LENGTH_LONG).show();
                                }
                                else {

                                    Toast.makeText(RegistrationActivity.this,"Registration Failed. Error : " + task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();

                                }


                            }
                        }
                    });
                }
            }
        });

        ProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"), PICK_IMG);
            }
        });


    }


    private void setupUIV(){

        Name         = findViewById(R.id.RAname);
        Email        = findViewById(R.id.RAemail);
        Password     = findViewById(R.id.RApassword);
        PasswordConf = findViewById(R.id.RApassword2);
        Register     = findViewById(R.id.RAregister);
        ProfilePic   = findViewById(R.id.RAprofilePic);
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

    private boolean inputValidateName(){

        if(Name.getText().toString().isEmpty()){

            Name.setError("Enter your name");
            return false;
        }

        else{
            return true;
        }
    }

    private boolean PasswordChecker(){

        String pw1, pw2;

        pw1 = Password.getText().toString();
        pw2 = PasswordConf.getText().toString();

        if(pw1.equals(pw2)){
            return true;
        }

        else{
            Toast.makeText(RegistrationActivity.this, "Passwords don't match .... Please check ", Toast.LENGTH_LONG).show();
            return false;
        }

    }

    private boolean inputValidatePassword(){

        if(Password.getText().toString().isEmpty()){

            Password.setError("Enter Password");
            return false;
        }

        else if(Password.length()<6){
            Password.setError("Password should be more than 6 characters");
            return false;
        }

        else{
            return true;
        }
    }

    private boolean inputValidatePassword2(){

        if(PasswordConf.getText().toString().isEmpty()){

            PasswordConf.setError("Enter Password");
            return false;
        }

        else if(PasswordConf.length()<6){
            PasswordConf.setError("Password should be more than 6 characters");
            return false;
        }

        else{
            return true;
        }
    }

    private boolean inputProfilePicValidate(){
        if (imagePath == null){
            return false;
        }

        else {
            return true;
        }
    }

    /*private boolean inputProfilePicValidate(){
        if(imagePath==null){
            Toast.makeText(RegistrationActivity.this,"Please Upload Profile Pic!", Toast.LENGTH_LONG).show();
            return false;
        }
        else{
            return true;
        }
    }*/

    private void sendEmailVerification(){

        final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser!=null){
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){

                        sendUserData();
                        Toast.makeText(RegistrationActivity.this, "Registration Successful.....check your inbox for Verification Link", Toast.LENGTH_LONG).show();
                        firebaseAuth.signOut();
                        finish();
                        startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
                    }

                    else{

                        if(task.getException() instanceof FirebaseNetworkException){
                            Toast.makeText(RegistrationActivity.this, "Your device is offline... connect to the internet and try again", Toast.LENGTH_LONG).show();
                        }

                        else if (task.getException() instanceof FirebaseAuthUserCollisionException){

                            Toast.makeText(RegistrationActivity.this, "Account has been made before.... check your inbox for verification email", Toast.LENGTH_LONG).show();

                        }

                        else if (task.getException() instanceof FirebaseAuthWeakPasswordException){

                            Toast.makeText(RegistrationActivity.this, "The entered password is too weak.... try another one", Toast.LENGTH_LONG).show();

                        }

                        else {
                            Toast.makeText(RegistrationActivity.this, "Error : " + task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();

                        }
                    }
                }
            });
        }
    }

    private void sendUserData() {

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference();
        user_ID = firebaseAuth.getUid();
        UserProfile userProfile = new UserProfile(user_name, user_email, user_password, user_ID, "online");
        myRef.child("Users").child(firebaseAuth.getUid()).setValue(userProfile).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(RegistrationActivity.this, "Error : " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });

        if (inputProfilePicValidate()){

            StorageReference imageReference = storageReference.child(firebaseAuth.getUid()).child("Profile Pic");
            final UploadTask uploadTask = imageReference.putFile(imagePath);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(RegistrationActivity.this, "Upload of profilePic Failed..... Error : " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }



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

    @Override
    public void onBackPressed() {

        finish();
    }

}
