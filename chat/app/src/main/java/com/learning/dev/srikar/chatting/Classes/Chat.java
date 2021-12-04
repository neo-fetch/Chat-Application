package com.learning.dev.srikar.chatting.Classes;

public class Chat {

    private String Sender, Receiver, Message;
    private boolean Seen;

    public Chat(String sender, String receiver, String message, boolean seen) {
        Sender = sender;
        Receiver = receiver;
        Message = message;
        Seen = seen;
    }

    public Chat(){

    }

    public String getSender() {
        return Sender;
    }

    public void setSender(String sender) {
        Sender = sender;
    }

    public String getReceiver() {
        return Receiver;
    }

    public void setReceiver(String receiver) {
        Receiver = receiver;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public boolean isSeen() {
        return Seen;
    }

    public void setSeen(boolean seen) {
        Seen = seen;
    }
}
