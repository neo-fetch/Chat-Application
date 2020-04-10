package com.learning.dev.srikar.chatting.Adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.learning.dev.srikar.chatting.Classes.Chat;
import com.learning.dev.srikar.chatting.R;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {


    public FirebaseUser firebaseUser;

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;



    public Context mContext;
    public List<Chat> mChat;

    public MessageAdapter(Context mContext, List<Chat> mChat){

        this.mChat= mChat;
        this.mContext = mContext;
    }


    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        if (i == MSG_TYPE_RIGHT) {

            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, viewGroup , false);
            return new MessageAdapter.ViewHolder(view);
        }

        else{

            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, viewGroup , false);
            return new MessageAdapter.ViewHolder(view);
        }


    }

    @Override
    public void onBindViewHolder(@NonNull final MessageAdapter.ViewHolder viewHolder, int i) {


        Chat chat = mChat.get(i);

        viewHolder.ShowMessage.setText(chat.getMessage());

        if(i == mChat.size()-1){

            if(chat.isSeen()){

                viewHolder.SeenMessage.setText("Seen");
            }

            else {
                viewHolder.SeenMessage.setText("Delivered");
            }
        }

        else {
            viewHolder.SeenMessage.setVisibility(View.GONE);
        }


    }


    @Override
    public int getItemCount() {

        return mChat.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView ShowMessage, SeenMessage;

        public ViewHolder(View itemView){
            super(itemView);

            ShowMessage = itemView.findViewById(R.id.ShowMessage);
            SeenMessage = itemView.findViewById(R.id.Msg_seen);

        }
    }

    @Override
    public int getItemViewType(int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        if(mChat.get(position).getSender().equals(firebaseUser.getUid())){

            return MSG_TYPE_RIGHT;
        }

        else {
            return MSG_TYPE_LEFT;
        }

    }
}
