package com.example.usersidedemoproject.Model;

public class TaskInfoModel {
    String task_created_by, task_desc, task_name,task_priority,task_start_date,
            task_status,task_time,task_time_estimated, task_uid;

    public TaskInfoModel(String task_created_by, String task_desc,
                         String task_name, String task_priority,
                         String task_start_date, String task_status,
                         String task_time, String task_time_estimated,
                         String task_uid) {
        this.task_created_by = task_created_by;
        this.task_desc = task_desc;
        this.task_name = task_name;
        this.task_priority = task_priority;
        this.task_start_date = task_start_date;
        this.task_status = task_status;
        this.task_time = task_time;
        this.task_time_estimated = task_time_estimated;
        this.task_uid = task_uid;
    }

    public TaskInfoModel() {
    }

    public String getTask_created_by() {
        return task_created_by;
    }

    public void setTask_created_by(String task_created_by) {
        this.task_created_by = task_created_by;
    }

    public String getTask_desc() {
        return task_desc;
    }

    public void setTask_desc(String task_desc) {
        this.task_desc = task_desc;
    }

    public String getTask_name() {
        return task_name;
    }

    public void setTask_name(String task_name) {
        this.task_name = task_name;
    }

    public String getTask_priority() {
        return task_priority;
    }

    public void setTask_priority(String task_priority) {
        this.task_priority = task_priority;
    }

    public String getTask_start_date() {
        return task_start_date;
    }

    public void setTask_start_date(String task_start_date) {
        this.task_start_date = task_start_date;
    }

    public String getTask_status() {
        return task_status;
    }

    public void setTask_status(String task_status) {
        this.task_status = task_status;
    }

    public String getTask_time() {
        return task_time;
    }

    public void setTask_time(String task_time) {
        this.task_time = task_time;
    }

    public String getTask_time_estimated() {
        return task_time_estimated;
    }

    public void setTask_time_estimated(String task_time_estimated) {
        this.task_time_estimated = task_time_estimated;
    }

    public String getTask_uid() {
        return task_uid;
    }

    public void setTask_uid(String task_uid) {
        this.task_uid = task_uid;
    }
}
