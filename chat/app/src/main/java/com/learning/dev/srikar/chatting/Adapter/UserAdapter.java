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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.learning.dev.srikar.chatting.R;
import com.learning.dev.srikar.chatting.Classes.UserProfile;
import com.learning.dev.srikar.chatting.UserProfileActivity;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private FirebaseStorage firebaseStorage;

    private Context mContext;
    private List<UserProfile> mUsers;
    private boolean isOnline;


    public UserAdapter(Context mContext, List<UserProfile> mUsers, boolean isOnline){

        this.mUsers = mUsers;
        this.mContext = mContext;
        this.isOnline = isOnline;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, viewGroup , false);
        return new UserAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {

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



       if (isOnline) {

            if (userProfile.getStatus().equals("online")) {

                viewHolder.online_checker.setVisibility(View.VISIBLE);

            }
            else {

                viewHolder.online_checker.setVisibility(View.GONE);
            }
       }

       viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               Intent intent = new Intent(mContext, UserProfileActivity.class);
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
        private CircleImageView ProfilePic;
        private CircleImageView online_checker;


        public ViewHolder(View itemView){
            super(itemView);

            Username       = itemView.findViewById(R.id.userItem_Username);
            ProfilePic     = itemView.findViewById(R.id.userItem_ProfilePic);
            online_checker = itemView.findViewById(R.id.user_Item_display_online);

        }
    }



}
