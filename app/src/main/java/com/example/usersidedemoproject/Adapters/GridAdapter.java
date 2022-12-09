package com.example.usersidedemoproject.Adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.usersidedemoproject.Model.EventsModelClass;
import com.example.usersidedemoproject.Model.GetAttendanceColor;
import com.example.usersidedemoproject.Model.LeaveDateModel;
import com.example.usersidedemoproject.Model.sunday;
import com.example.usersidedemoproject.R;
import com.google.firebase.auth.FirebaseAuth;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

public class GridAdapter extends ArrayAdapter {
    List<Date> dates;
    Calendar currentdates;
    List<EventsModelClass> events;
    LayoutInflater inflater;
    FirebaseAuth usrauth;
    List<GetAttendanceColor> getAttendance, listCol2;
    List<GetAttendanceColor> getAttendances;
    List<String> sundayNum;
    List<sunday> checkS;
    List<LeaveDateModel> leavemodel;
    String check;

    public GridAdapter(@NonNull Context context, List<Date> dates,
                       Calendar currentdates,
                       List<EventsModelClass> events, List<GetAttendanceColor> getAttendances, ArrayList<LeaveDateModel> leavemodel) {
        super(context, R.layout.calendar_date_layout);
        this.dates = dates;
        this.events = events;
        this.currentdates = currentdates;
        this.getAttendances = getAttendances;
        this.leavemodel = leavemodel;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // getVals();
        Date monthDate = dates.get(position);
        //Log.d("keyyyyyy",listCol.stream().findAny());
        //GetAttendanceColor gc=GetAttendanceColor();
//        Log.d("keyyyyyy", getAttendances.get(position).toString());

        sundayNum = new ArrayList<>();
        checkS = new ArrayList<>();
        Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.setTime(monthDate);
        int DayNo = dateCalendar.get(Calendar.DAY_OF_MONTH);
        int displayMonth = dateCalendar.get(Calendar.MONTH) + 1;
        Log.d("checkdisplaymonth", String.valueOf(displayMonth));
        int getCurrentMonth = dateCalendar.get(Calendar.MONTH);

        Calendar calendarin = Calendar.getInstance();

        int curerntDay = calendarin.get(Calendar.DAY_OF_MONTH);

        int displayYear = dateCalendar.get(Calendar.YEAR);
        int currentMonth = currentdates.get(Calendar.MONTH) + 1;
        int currentYear = currentdates.get(Calendar.YEAR);

        String CYear = String.valueOf(currentYear);
        String CMonth = String.valueOf(currentMonth);
        String CDay = String.valueOf(curerntDay);

        usrauth = FirebaseAuth.getInstance();

        SimpleDateFormat dt1 = new SimpleDateFormat("dd/MM/YYYY", Locale.getDefault());
        System.out.println(dt1.format(monthDate));
        System.out.println(getAttendances.size());
        System.out.println("abcsdsds" + leavemodel.size());
        GetAttendanceColor gc = getAttendances.stream().filter(getAttendanceColor -> {
            LocalDate date = LocalDate.of(Integer.parseInt(getAttendanceColor.getYear()),
                    Integer.parseInt(getAttendanceColor.getMonth()),
                    Integer.parseInt(getAttendanceColor.getDay()));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            System.out.println(formatter.format(date));
            if (dt1.format(monthDate).equals(formatter.format(date))) {
                return true;
            }
            return false;
        }).findFirst().orElse(null);
        //Log.d("fefefdfd", gc==null? "not found":"found");

        SimpleDateFormat dt2 = new SimpleDateFormat("dd-M-yyyy", Locale.getDefault());
        Log.d("date", monthDate.toString());
        LeaveDateModel lc = leavemodel.stream().filter(leaveDateModel -> {
            return isTimeStampInLeaveDate(leaveDateModel.getStartTimestamp(), leaveDateModel.getEndTimestamp(), monthDate);
        }).findFirst().orElse(null);
        Log.d("size", String.valueOf(lc != null));
        View v = convertView;

        if (lc != null) {
            // Log.d("getEndTimeStamp", "True" +   lc.getEndTimestamp());
        }
        // Log.d("getEndTimeStamp", "True" +   lc.getEndTimestamp());


        Resources res = getContext().getResources(); //resource handle
        Drawable blackborderdrawable = res.getDrawable(R.drawable.black_border_card);
        Drawable orangeborderdrawable = res.getDrawable(R.drawable.orange_border_card);
        Drawable borderless_cards = res.getDrawable(R.drawable.borderless_cards);
        Drawable greenBorderDrawable = res.getDrawable(R.drawable.green_border_card);
        Drawable aquaBorderDrawable = res.getDrawable(R.drawable.aqua_border_color);
        // countWeekendDays(displayYear, displayMonth);
        //Log.d("actualsunday",String.valueOf( countWeekendDays(displayYear, displayMonth)));

        Calendar sundaycal = Calendar.getInstance();
        // Note that month is 0-based in calendar, bizarrely.
        sundaycal.set(displayYear, displayMonth - 2, 1);
        int daysInM = sundaycal.getActualMaximum(Calendar.DAY_OF_MONTH);
        int count = 0;
        for (int day = 1; day <= daysInM ; day++) {
            sundaycal.set(displayYear, displayMonth - 1, day);
            int dayOfWeek = sundaycal.get(Calendar.DAY_OF_WEEK);

            if (dayOfWeek == Calendar.SUNDAY) {
                count = day;
                //Log.d("actualsunday",String.valueOf( day));
                sundayNum.add(String.valueOf(day));
            }
        }

        //Log.d("cdate",cday);


        SimpleDateFormat dt3 = new SimpleDateFormat("dd-M-yyyy");


        //Log.d("cdate",cday);
        if (v == null) {
            v = inflater.inflate(R.layout.calendar_date_layout, parent, false);
        }
        if (displayMonth == currentMonth && displayYear == currentYear) {// Log.d("cdate",cday);
            if (lc != null) {
                int DyNo = dateCalendar.get(Calendar.DAY_OF_MONTH);
                String cday = String.valueOf(DyNo) + "-" + String.valueOf(currentMonth) + "-" + String.valueOf(currentYear);
                try {
                    v.findViewById(R.id.date_card).setBackground(orangeborderdrawable);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (gc != null) {
                if (String.valueOf(displayYear).equals(gc.getYear())) {
                    if ( displayMonth==Integer.parseInt(gc.getMonth())) {
                        if (String.valueOf(DayNo).equals(gc.getDay())) {
                            if (gc.getAttendance().equals("Present")) {
                                v.findViewById(R.id.date_card).setBackground(greenBorderDrawable);
                            } else {
                                v.findViewById(R.id.date_card).setBackground(blackborderdrawable);
                            }
                        } else {
                        }
                    } else {
                        v.findViewById(R.id.date_card).setBackground(blackborderdrawable);
                    }
                } else {

                    v.findViewById(R.id.date_card).setBackground(blackborderdrawable);
                }
            } else {
                for (String check : sundayNum) {
                    if (String.valueOf(DayNo).equals(check)) {

                        v.findViewById(R.id.date_card).setBackground(aquaBorderDrawable);
                    }
//                    else {
//
//                        v.findViewById(R.id.date_card).setBackground(blackborderdrawable);
//                    }
                }
                v.findViewById(R.id.date_card).setBackground(blackborderdrawable);
            }
            Log.d("checksssssssssss", "sundayTrueeeeee");
            int ct = 0;
            for (String check : sundayNum) {
                if (String.valueOf(DayNo).equals(check)) {
                    ct++;

                    v.findViewById(R.id.date_card).setBackground(aquaBorderDrawable);
                }
                Log.d("checksssssssssss", "sundayTrueeeeee" + String.valueOf(sundayNum));
            }
        } else {
            v.findViewById(R.id.date_card).setBackground(borderless_cards);
        }

        TextView Day_Number = v.findViewById(R.id.calendar_day);
        TextView EventNumber = v.findViewById(R.id.event_day);
        Day_Number.setText(String.valueOf(DayNo));
        Calendar evenCalendar = Calendar.getInstance();
        ArrayList<String> arrayList = new ArrayList<>();
        for (int i = 0; i < events.size(); i++) {
            evenCalendar.setTime(ConvertStringToDate(events.get(i).getDATE()));
            if (DayNo == evenCalendar.get(Calendar.DAY_OF_MONTH) && displayMonth == evenCalendar.get(Calendar.MONTH) + 1
                    && displayYear == evenCalendar.get(Calendar.YEAR)) {
                arrayList.add(events.get(i).getEVENT());
                EventNumber.setText(arrayList.size() + " Events");
            }
        }
        return v;
    }

    private boolean isTimeStampInLeaveDate(String startTimeStamp, String endTimeStamp, Date currentDate) {
        try {
            return Long.parseLong(startTimeStamp) <= currentDate.getTime() && currentDate.getTime() <= Long.parseLong(endTimeStamp);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private String formatDate(long milliseconds) /* This is your topStory.getTime()*1000 */ {
        DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy' 'HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);
        TimeZone tz = TimeZone.getDefault();
        sdf.setTimeZone(tz);
        System.out.println(calendar.getTime());
        return sdf.format(calendar.getTime());
    }

    /*    public int countWeekendDays(int displayYear, int displayMonth) {
            Calendar sundaycal = Calendar.getInstance();
            // Note that month is 0-based in calendar, bizarrely.
            sundaycal.set(displayYear, displayMonth-1, 1);
            int daysInM = sundaycal.getActualMaximum(Calendar.DAY_OF_MONTH);
            int count = 0;
            for (int day = 1; day <= daysInM; day++) {
                sundaycal.set(displayYear, displayMonth -1, day);
                int dayOfWeek = sundaycal.get(Calendar.DAY_OF_WEEK);

                if (dayOfWeek == Calendar.SUNDAY) {
                    count=day;
                }
            }
            Log.d("sundayabc",String.valueOf(count));
            return count;
        }*/
    private Date ConvertStringToDate(String eventDate) {
        SimpleDateFormat format;
        format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date date = null;
        try {
            date = format.parse(eventDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    @Override
    public int getCount() {
        return dates.size();
    }


    @Override
    public int getPosition(@Nullable Object item) {
        return dates.indexOf(item);
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return dates.get(position);

    }

    public void updateAdapter(List<Date> dates,
                              Calendar currentdates,
                              List<EventsModelClass> events, List<GetAttendanceColor> getAttendances, ArrayList<LeaveDateModel> leavemodel) {
        this.dates = dates;
        this.events = events;
        this.currentdates = currentdates;
        this.getAttendances = getAttendances;
        this.leavemodel = leavemodel;
        notifyDataSetChanged();
    }
}

