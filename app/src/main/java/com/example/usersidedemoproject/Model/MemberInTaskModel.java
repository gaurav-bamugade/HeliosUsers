package com.example.usersidedemoproject.Model;

public class MemberInTaskModel {
    String TaskRole,TimeStamp,UserRole,UserUid;

    public MemberInTaskModel(String taskRole, String timeStamp, String userRole, String userUid) {
        TaskRole = taskRole;
        TimeStamp = timeStamp;
        UserRole = userRole;
        UserUid = userUid;
    }

    public String getTaskRole() {
        return TaskRole;
    }

    public void setTaskRole(String taskRole) {
        TaskRole = taskRole;
    }

    public String getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        TimeStamp = timeStamp;
    }

    public String getUserRole() {
        return UserRole;
    }

    public void setUserRole(String userRole) {
        UserRole = userRole;
    }

    public String getUserUid() {
        return UserUid;
    }

    public void setUserUid(String userUid) {
        UserUid = userUid;
    }
}
