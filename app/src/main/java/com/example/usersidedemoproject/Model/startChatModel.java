package com.example.usersidedemoproject.Model;

public class startChatModel {
    public String name, role, image,uid,chatUid;

    public startChatModel() {
    }

    public startChatModel(String name, String role, String image, String uid) {
        this.name = name;
        this.role = role;
        this.image = image;
        this.uid = uid;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return role;
    }

    public void setStatus(String status) {
        this.role = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;

    }
}



