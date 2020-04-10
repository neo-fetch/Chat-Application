package com.learning.dev.srikar.chatting;

import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.learning.dev.srikar.chatting.Adapter.MessageAdapter;
import com.learning.dev.srikar.chatting.Classes.Chat;
import com.learning.dev.srikar.chatting.Classes.Chatlist;
import com.learning.dev.srikar.chatting.Classes.UserProfile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {


    private EditText Message;
    private ImageButton SendMessage;
    private CircleImageView ProfilePic, OnlineDisplay;
    private TextView Username;

    private FirebaseUser firebaseUser;
    private FirebaseStorage firebaseStorage;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    Intent intent;

    private MessageAdapter messageAdapter;
    private List<Chat> mChat;
    private List<Chatlist> chatListArr;

    RecyclerView recyclerView;

    private ValueEventListener seenListner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setupUIV();

        intent = getIntent();
        final String UserId = intent.getStringExtra("Userid");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        final StorageReference storageReference = firebaseStorage.getReference();

        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(UserId);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                final UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                Username.setText(userProfile.getName());


                storageReference.child((UserId)).child("Profile Pic").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        Glide.with(getApplicationContext()).load(uri).into(ProfilePic);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof StorageException){

                            ProfilePic.setImageResource(R.drawable.blank_profilepic);
                        }

                        else {

                            Toast.makeText(MessageActivity.this, "Error : " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();

                        }
                    }
                });

                readMessages(firebaseUser.getUid(), UserId);

                if ( userProfile.getStatus().equals("online") ){

                    OnlineDisplay.setVisibility(View.VISIBLE);
                }

                else{
                    OnlineDisplay.setVisibility(View.GONE);
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MessageActivity.this,"Error : " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        recyclerView = findViewById(R.id.recycler_viewForMessaging);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        seenMessage(UserId);

        SendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String message = Message.getText().toString();
                if(!message.isEmpty()){

                    sendMessage(firebaseUser.getUid(), UserId , message);
                }
                Message.getText().clear();
            }
        });


    }

    private void setupUIV(){

        Message       = findViewById(R.id.MeAmessage);
        SendMessage   = findViewById(R.id.MeAsendMessage);
        Username      = findViewById(R.id.MeAUsername);
        ProfilePic    = findViewById(R.id.MeAprofilePic);
        OnlineDisplay = findViewById(R.id.MeA_displayOnline);

    }

    private void seenMessage(final String userid){

        databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        seenListner = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                    Chat chat = snapshot.getValue(Chat.class);
                    if(chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid)){

                        //TODO : Fix delivered and seen message
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("seen", true);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void sendMessage(final String Sender, final String Receiver, final String Message){

        chatListArr = new ArrayList<>();

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        final HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("Sender", Sender);
        hashMap.put("Receiver", Receiver);
        hashMap.put("Message", Message);
        hashMap.put("seen", false);

        databaseReference.child("Chats").push().setValue(hashMap);

        final DatabaseReference ChatRef = FirebaseDatabase.getInstance().getReference("Chatlist");

        // don't use ChatRef.addValueEventListener() as its going in infinite loop
        ChatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(!dataSnapshot.exists()) {

                    HashMap<String, Object> hashMap1 = new HashMap<>();
                    hashMap1.put("Sender", Sender);
                    hashMap1.put("Receiver", Receiver);
                    ChatRef.push().setValue(hashMap1);
                }

                else{

                    Boolean checker = false;

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                        Chatlist chatlist = snapshot.getValue(Chatlist.class);
                        chatListArr.add(chatlist);
                    }

                    for (int i = 0; i<chatListArr.size(); i++){

                        if( (chatListArr.get(i).getSender().equals(Sender)&&chatListArr.get(i).getReceiver().equals(Receiver)) || (chatListArr.get(i).getSender().equals(Receiver)&&chatListArr.get(i).getReceiver().equals(Sender)) ){

                            checker =true;
                            break;
                        }
                    }

                    if(!checker){

                        HashMap<String, Object> hashMap1 = new HashMap<>();
                        hashMap1.put("Sender", Sender);
                        hashMap1.put("Receiver", Receiver);
                        ChatRef.push().setValue(hashMap1);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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




    private void readMessages(final String MyID, final String UserID){

        mChat = new ArrayList<>();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Chats");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mChat.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                    Chat chat = snapshot.getValue(Chat.class);

                    if((chat.getReceiver().equals(MyID) && chat.getSender().equals(UserID)) ||(chat.getReceiver().equals(UserID) && chat.getSender().equals(MyID))){


                        mChat.add(chat);
                    }

                    messageAdapter =  new MessageAdapter(MessageActivity.this, mChat);
                    recyclerView.setAdapter(messageAdapter);
                }

                seenMessage(UserID);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
        databaseReference.removeEventListener(seenListner);
    }


}
