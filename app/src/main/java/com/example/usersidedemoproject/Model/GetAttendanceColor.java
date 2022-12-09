package com.example.usersidedemoproject.Model;

import java.io.Serializable;

public class GetAttendanceColor implements Serializable {
    String day,month,attendance,year;



    public GetAttendanceColor() {
    }
    public GetAttendanceColor(String day, String month, String attendance, String year) {
        System.out.println(day);
        this.day = day;
        this.month = month;
        this.attendance = attendance;
        this.year = year;

    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getAttendance() {
        return attendance;
    }

    public void setAttendance(String attendance) {
        this.attendance = attendance;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public  String getDate(){
        return getDay()+"/"+getMonth()+"/"+getYear();
    }
}



  /*  Date monthDate= (Date) dates;
    Calendar dateCalendar=Calendar.getInstance();
       dateCalendar.setTime(monthDate);
    int DayNo=dateCalendar.get(Calendar.DAY_OF_MONTH);
    int displayMonth=
    int displayYear=dateCalendar.get(Calendar.YEAR); dateCalendar.get(Calendar.MONTH)+1;*/

/*    Calendar dateCalendar=Calendar.getInstance();
    int DayNo=dateCalendar.get(Calendar.DAY_OF_MONTH);
    int displayYear=dateCalendar.get(Calendar.YEAR);
    int displayMonth= dateCalendar.get(Calendar.MONTH)+1;*/
  /*   int i=0;
        for(i=0;i<= dates.size();i++)
        {
            dateCalendar.setTime(dates.get(i));



            System.out.println(displayYear);
        }*/


   /* public String day(){
        if(String.valueOf(DayNo).equals(day))
        {
            return year;
        }
        return "0";
    }*/