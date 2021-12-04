package com.learning.dev.srikar.chatting.Fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.learning.dev.srikar.chatting.Adapter.UserAdapterForChats;
import com.learning.dev.srikar.chatting.Classes.Chatlist;
import com.learning.dev.srikar.chatting.Classes.UserProfile;
import com.learning.dev.srikar.chatting.R;

import java.util.ArrayList;
import java.util.List;


public class ChatFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView EmptyChats;
    private UserAdapterForChats userAdapter;
    private List<UserProfile> mUsers;

    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;

    private List<Chatlist> chatList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        EmptyChats = view.findViewById(R.id.EmptyChats);

        recyclerView = view.findViewById(R.id.recycler_viewForSeeingChats);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        chatList = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference("Chatlist");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                chatList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                    Chatlist chatlist = snapshot.getValue(Chatlist.class);
                    chatList.add(chatlist);
                }

                DisplayUserswithMessage();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return view;
    }

    private void DisplayUserswithMessage(){

        mUsers = new ArrayList<>();
         databaseReference = FirebaseDatabase.getInstance().getReference("Users");
         databaseReference.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                 mUsers.clear();
                 for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                     UserProfile userProfile = snapshot.getValue(UserProfile.class);

                     for (Chatlist chatlist : chatList){

                         if((userProfile.getID().equals(chatlist.getSender()) || userProfile.getID().equals(chatlist.getReceiver())) &&!userProfile.getID().equals(firebaseUser.getUid())){

                             if(chatlist.getReceiver().equals(firebaseUser.getUid())||chatlist.getSender().equals(firebaseUser.getUid())){

                                 if(mUsers.size()!=0){

                                     for (int i=0; i<mUsers.size(); i++){

                                         if( userProfile != (mUsers.get(i))){


                                             mUsers.add(userProfile);
                                         }
                                     }
                                 }

                                 else{

                                     mUsers.add(userProfile);
                                 }
                             }
                         }
                     }
                 }



                 if(mUsers.size() == 0) {

                     EmptyChats.setVisibility(View.VISIBLE);
                     EmptyChats.setText("Swipe left to start chatting ------>");
                 }

                 else {
                     EmptyChats.setVisibility(View.GONE);
                 }

                 userAdapter = new UserAdapterForChats(getContext(), mUsers, true);
                 recyclerView.setAdapter(userAdapter);
             }

             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {

             }
         });
    }



}
