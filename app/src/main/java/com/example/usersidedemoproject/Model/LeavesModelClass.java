package com.example.usersidedemoproject.Model;

public class LeavesModelClass {
  String  apply_for_leave_id, from,leave_type,no_of_days,reason,remark,status,to,user_uid;

    public LeavesModelClass() {
    }

    public LeavesModelClass(String apply_for_leave_id, String from, String leave_type, String no_of_days, String reason, String remark, String status, String to, String user_uid) {
        this.apply_for_leave_id = apply_for_leave_id;
        this.from = from;
        this.leave_type = leave_type;
        this.no_of_days = no_of_days;
        this.reason = reason;
        this.remark = remark;
        this.status = status;
        this.to = to;
        this.user_uid = user_uid;
    }

    public String getApply_for_leave_id() {
        return apply_for_leave_id;
    }

    public void setApply_for_leave_id(String apply_for_leave_id) {
        this.apply_for_leave_id = apply_for_leave_id;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getLeave_type() {
        return leave_type;
    }

    public void setLeave_type(String leave_type) {
        this.leave_type = leave_type;
    }

    public String getNo_of_days() {
        return no_of_days;
    }

    public void setNo_of_days(String no_of_days) {
        this.no_of_days = no_of_days;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getUser_uid() {
        return user_uid;
    }

    public void setUser_uid(String user_uid) {
        this.user_uid = user_uid;
    }
}
