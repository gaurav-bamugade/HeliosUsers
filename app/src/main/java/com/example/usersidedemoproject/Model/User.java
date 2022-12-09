package com.example.usersidedemoproject.Model;

public class User
{
    private String image,name,status,uid;

    public User()
    {
    }

    public User(String image, String name, String status, String uid) {
        this.image = image;
        this.name = name;
        this.status = status;
        this.uid = uid;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
