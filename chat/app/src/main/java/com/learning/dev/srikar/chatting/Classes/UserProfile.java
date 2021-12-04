package com.learning.dev.srikar.chatting.Classes;

public class UserProfile {

    private String Name, Email, Password, ID, Status;

    public UserProfile(String name, String email, String password, String id, String status) {
        Name = name;
        Email = email;
        Password = password;
        ID = id;
        Status = status;
    }

    public UserProfile(){

    }
    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getID() { return ID; }

    public void setID(String id) { ID = id;}

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }
}
