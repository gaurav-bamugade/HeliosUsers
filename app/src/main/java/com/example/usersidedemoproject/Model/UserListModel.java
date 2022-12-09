package com.example.usersidedemoproject.Model;

public class UserListModel {
String  user_name,user_uid,user_role;

    public UserListModel(String user_name, String user_uid, String user_role) {
        this.user_name = user_name;
        this.user_uid = user_uid;
        this.user_role = user_role;
    }

    public String getUser_role() {
        return user_role;
    }

    public void setUser_role(String user_role) {
        this.user_role = user_role;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_uid() {
        return user_uid;
    }

    public void setUser_uid(String user_uid) {
        this.user_uid = user_uid;
    }

    public UserListModel() {
    }
}
