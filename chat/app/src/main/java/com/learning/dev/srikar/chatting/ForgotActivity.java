package com.learning.dev.srikar.chatting;

import android.content.DialogInterface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotActivity extends AppCompatActivity {

    private EditText Email;
    private Button ResetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);

        Email = findViewById(R.id.FAemail_id);
        ResetPassword = findViewById(R.id.FAresetPassword);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!Email.getText().toString().isEmpty()){

                    String Email_ID = Email.getText().toString().trim();
                    FirebaseAuth.getInstance().sendPasswordResetEmail(Email_ID).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){

                                showPasswordResetEmailSent();
                            }

                            else{

                                Toast.makeText(ForgotActivity.this, "Failed to send reset link, Please try again later", Toast.LENGTH_LONG).show();
                            }

                        }
                    });
                }

                else{
                    Toast.makeText(ForgotActivity.this, "Enter your registered Email-ID", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void showPasswordResetEmailSent(){

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Password reset link sent");
        builder.setMessage("A link has been sent to your registered eMail.... Check your inbox to reset password");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                finish();

            }
        });

        builder.create().show();
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
