package com.example.usersidedemoproject.Model;

import java.io.Serializable;

public class LeaveDateModel implements Serializable {
    String CurrentTime;
    String EndTimestamp;
    String From;
    String NoOfDays;
    String StartTimestamp;
    String To;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    String reason;

    public LeaveDateModel(String currentTime, String endTimestamp, String from, String noOfDays, String startTimestamp, String to,String reason) {
        CurrentTime = currentTime;
        EndTimestamp = endTimestamp;
        From = from;
        NoOfDays = noOfDays;
        StartTimestamp = startTimestamp;
        To = to;
       this.reason=reason;
    }

    public String getCurrentTime() {
        return CurrentTime;
    }

    public void setCurrentTime(String currentTime) {
        CurrentTime = currentTime;
    }

    public String getEndTimestamp() {
        return EndTimestamp;
    }

    public void setEndTimestamp(String endTimestamp) {
        EndTimestamp = endTimestamp;
    }

    public String getFrom() {
        return From;
    }

    public void setFrom(String from) {
        From = from;
    }

    public String getNoOfDays() {
        return NoOfDays;
    }

    public void setNoOfDays(String noOfDays) {
        NoOfDays = noOfDays;
    }

    public String getStartTimestamp() {
        return StartTimestamp;
    }

    public void setStartTimestamp(String startTimestamp) {
        StartTimestamp = startTimestamp;
    }

    public String getTo() {
        return To;
    }

    public void setTo(String to) {
        To = to;
    }
}
