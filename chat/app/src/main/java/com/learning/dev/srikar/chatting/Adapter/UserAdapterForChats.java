package com.learning.dev.srikar.chatting.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.learning.dev.srikar.chatting.Classes.Chat;
import com.learning.dev.srikar.chatting.Classes.UserProfile;
import com.learning.dev.srikar.chatting.MessageActivity;
import com.learning.dev.srikar.chatting.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapterForChats extends RecyclerView.Adapter<UserAdapterForChats.ViewHolder> {

    private FirebaseStorage firebaseStorage;

    private Context mContext;
    private List<UserProfile> mUsers;
    private boolean isOnline;
    String LastMessage;

    public UserAdapterForChats(Context mContext, List<UserProfile> mUsers, boolean isOnline){

        this.mUsers = mUsers;
        this.mContext = mContext;
        this.isOnline = isOnline;
    }


    @NonNull
    @Override
    public UserAdapterForChats.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, viewGroup , false);
        return new UserAdapterForChats.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final UserAdapterForChats.ViewHolder viewHolder, int i) {

        final UserProfile userProfile = mUsers.get(i);
        viewHolder.Username.setText(userProfile.getName());


        firebaseStorage = FirebaseStorage.getInstance();

        StorageReference storageReference = firebaseStorage.getReference();

        storageReference.child(userProfile.getID()).child("Profile Pic").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                Glide.with(mContext.getApplicationContext()).load(uri).into(viewHolder.ProfilePic);
            }

        });

        if (isOnline){
            ShowLastMessage(userProfile.getID(), viewHolder.LastMsg);
        }

        else {
            viewHolder.LastMsg.setVisibility(View.GONE);
        }

        if (isOnline){

            if (userProfile.getStatus().equals("online")){
                viewHolder.online_checker.setVisibility(View.VISIBLE);
            }

            else{
                viewHolder.online_checker.setVisibility(View.GONE);
            }
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext, MessageActivity.class);
                intent.putExtra("Userid", userProfile.getID());
                mContext.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {

        return mUsers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private TextView Username;
        private TextView LastMsg;
        private CircleImageView ProfilePic;
        private CircleImageView online_checker;

        public ViewHolder(View itemView){
            super(itemView);

            Username       = itemView.findViewById(R.id.userItem_Username);
            ProfilePic     = itemView.findViewById(R.id.userItem_ProfilePic);
            online_checker = itemView.findViewById(R.id.user_Item_display_online);
            LastMsg        = itemView.findViewById(R.id.user_Item_ShowLastMessage);
        }
    }

    private  void ShowLastMessage(final String userID, final TextView lastMsg){

        FirebaseUser User = FirebaseAuth.getInstance().getCurrentUser();

        if(User != null){

            LastMessage = "default";
            final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Chats");

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                        Chat chat = snapshot.getValue(Chat.class);
                        if((chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userID)) ||(chat.getReceiver().equals(userID) && chat.getSender().equals(firebaseUser.getUid()))){

                            LastMessage = chat.getMessage();
                        }
                    }

                    switch (LastMessage){

                        case "default": {
                            lastMsg.setText("");
                            break;
                        }

                        default: {
                            lastMsg.setText(LastMessage);
                            break;
                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

    }
}
