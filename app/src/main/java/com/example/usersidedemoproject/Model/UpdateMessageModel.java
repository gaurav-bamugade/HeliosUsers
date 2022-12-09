package com.example.usersidedemoproject.Model;

public class UpdateMessageModel {
    String from,type,timestamp,message,docname;
    public UpdateMessageModel() {
    }

    public UpdateMessageModel(String from, String type, String timestamp, String message, String docname) {
        this.from = from;
        this.type = type;
        this.timestamp = timestamp;
        this.message = message;
        this.docname = docname;
    }

    public String getDocname() {
        return docname;
    }

    public void setDocname(String docname) {
        this.docname = docname;
    }

    public String getFrom() {
        return from;
    }



    public void setFrom(String from) {
        this.from = from;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
