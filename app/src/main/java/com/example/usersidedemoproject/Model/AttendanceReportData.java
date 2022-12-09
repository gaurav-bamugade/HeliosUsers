package com.example.usersidedemoproject.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AttendanceReportData implements Serializable {
    String currentDate = "";
     List<Date> dates;
    ArrayList<GetAttendanceColor> cmodel;
    ArrayList<LeaveDateModel> leavemodel;
    String employeeName,selectedMonth;
    public AttendanceReportData(){
        dates=new ArrayList<>();
    }
    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getSelectedMonth() {
        return selectedMonth;
    }

    public void setSelectedMonth(String selectedMonth) {
        this.selectedMonth = selectedMonth;
    }

    public ArrayList<GetAttendanceColor> getCmodel() {
        return cmodel;
    }

    public void setCmodel(ArrayList<GetAttendanceColor> cmodel) {
        this.cmodel = cmodel;
    }

    public ArrayList<LeaveDateModel> getLeavemodel() {
        return leavemodel;
    }

    public void setLeavemodel(ArrayList<LeaveDateModel> leavemodel) {
        this.leavemodel = leavemodel;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    public List<Date> getDates() {
        return dates;
    }

    public void setDates( List<Date> dates) {
        this.dates = dates;
    }
}
