package com.example.usersidedemoproject.Model;

public class PendingApproveModel {
    String approvalId,approvalTaskLeaveId,approveType,currentTime,endDate,reason,remark,senderUID,startDate,status;

    public PendingApproveModel(String approvalId, String approvalTaskLeaveId, String approveType, String currentTime, String endDate, String reason, String remark, String senderUID, String startDate, String status) {
        this.approvalId = approvalId;
        this.approvalTaskLeaveId = approvalTaskLeaveId;
        this.approveType = approveType;
        this.currentTime = currentTime;
        this.endDate = endDate;
        this.reason = reason;
        this.remark = remark;
        this.senderUID = senderUID;
        this.startDate = startDate;
        this.status = status;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getApprovalId() {
        return approvalId;
    }

    public void setApprovalId(String approvalId) {
        this.approvalId = approvalId;
    }

    public String getApprovalTaskLeaveId() {
        return approvalTaskLeaveId;
    }

    public void setApprovalTaskLeaveId(String approvalTaskLeaveId) {
        this.approvalTaskLeaveId = approvalTaskLeaveId;
    }

    public String getApproveType() {
        return approveType;
    }

    public void setApproveType(String approveType) {
        this.approveType = approveType;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getSenderUID() {
        return senderUID;
    }

    public void setSenderUID(String senderUID) {
        this.senderUID = senderUID;
    }


}
