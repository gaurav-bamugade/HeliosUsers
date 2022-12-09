package com.example.usersidedemoproject.Model;

public class GroupChatMessages
{
    String message,sender,timeStamp,type,docname;

    public GroupChatMessages()
    {
    }

    public GroupChatMessages(String message, String sender, String timeStamp, String type, String docname) {
        this.message = message;
        this.sender = sender;
        this.timeStamp = timeStamp;
        this.type = type;
        this.docname = docname;
    }

    public String getDocname() {
        return docname;
    }

    public void setDocname(String docname) {
        this.docname = docname;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
