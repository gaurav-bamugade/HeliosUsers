package com.example.usersidedemoproject;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.usersidedemoproject.Adapters.EventRecyclerView;
import com.example.usersidedemoproject.Adapters.GridAdapter;
import com.example.usersidedemoproject.Model.AttendanceReportData;
import com.example.usersidedemoproject.Model.EventsModelClass;
import com.example.usersidedemoproject.Model.GetAttendanceColor;
import com.example.usersidedemoproject.Model.LeaveDateModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;
import java.util.UUID;

public class CustomCalendarView extends LinearLayout {
    ImageButton NextButton, PreviousButton;
    TextView CurrentMonth;
    GridView gridView;
    private static final int MAX_CALENDAR_DAYS = 42;
    Calendar calendar = Calendar.getInstance(Locale.getDefault());
    DBHelper dbHelper;

    Context context;
    SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
    SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM", Locale.getDefault());
    SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
    SimpleDateFormat evenDateFormate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    GridAdapter gridadapter;
    List<Date> dates = new ArrayList<>();
    List<EventsModelClass> eventsList = new ArrayList<>();
    AlertDialog alertDialog;
    DatabaseReference attendanceDbRef;
    Button checkInButton;
    String checkIfExists = "";
    int alarmYear, alarmMonth, alaramDay, alarmHour, alarmMinute;
    private FirebaseAuth usrauth;
    int Chour, Cminute;
    ArrayList<GetAttendanceColor> cmodel;
    ArrayList<LeaveDateModel> leavemodel;
    private String currentUserID;
    String selectedMonth = "";

    public CustomCalendarView(Context context) {
        super(context);
    }

    public CustomCalendarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        InitializeLayout();
        setUpCalendar();
        newcheckIn();
        Log.d("erroe", checkIfExists.toString().trim());
        Log.d("erroe", newcheckIn().toString().trim());
        gridadapter = new GridAdapter(context, dates, calendar, eventsList, cmodel, leavemodel);
        gridView.setAdapter(gridadapter);

        PreviousButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar.add(Calendar.MONTH, -1);
                setUpCalendar();
            }
        });
        NextButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar.add(Calendar.MONTH, 1);
                setUpCalendar();
            }
        });

        checkInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("checkifexist", newcheckIn().toString());
                checkIN();

            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(true);
                View addView = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_layout, null);
                EditText EventName = addView.findViewById(R.id.event_name);
                TextView EventTime = addView.findViewById(R.id.eventtime);
                ImageButton SetTime = addView.findViewById(R.id.seteventtime);
                Button AddEvent = addView.findViewById(R.id.add_events);
                CheckBox alarmMe = addView.findViewById(R.id.alarmme);
                Calendar dateCalendar = Calendar.getInstance();
                dateCalendar.setTime(dates.get(position));
                alarmYear = dateCalendar.get(Calendar.YEAR);
                alarmMonth = dateCalendar.get(Calendar.MONTH);
                alaramDay = dateCalendar.get(Calendar.DAY_OF_MONTH);

                SetTime.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TimePickerDialog timePickerDialog = new TimePickerDialog(addView.getContext()
                                , new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                Calendar c = Calendar.getInstance();
                                c.set(Calendar.HOUR_OF_DAY, hourOfDay, minute);
                                c.set(Calendar.MINUTE, minute);
                                c.setTimeZone(TimeZone.getDefault());
                                Chour = hourOfDay;
                                Cminute = minute;

                                Calendar calendar1 = Calendar.getInstance();
                                calendar1.set(0, 0, 0, Chour, Cminute);

                                SimpleDateFormat hFormate = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                                String event_Time = hFormate.format(c.getTime());
                                EventTime.setText(DateFormat.format("hh:mm aa", calendar1));
                                alarmHour = c.get(Calendar.HOUR_OF_DAY);
                                alarmMinute = c.get(Calendar.MINUTE);


                              /*
                                Chour=hourOfDay;
                                Cminute=minute;

                                Calendar calendar1=Calendar.getInstance();
                                calendar1.set(0,0,0,Chour,Cminute);
                                selected_estimated_time.setText(DateFormat.format("hh mm aa",calendar1));*/
                            }
                        }, 12, 0, false
                        );
                        timePickerDialog.updateTime(Chour, Cminute);
                        timePickerDialog.show();
                    }
                });
                String date = evenDateFormate.format(dates.get(position));
                String month = monthFormat.format(dates.get(position));
                String year = yearFormat.format(dates.get(position));
                AddEvent.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (alarmMe.isChecked()) {
                            saveEvent(EventName.getText().toString(), EventTime.getText().toString(), date, month, year, "on");
                            setUpCalendar();
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(alarmYear, alarmMonth, alaramDay, alarmHour, alarmMinute);
                            //setAlarm(calendar,EventName.getText().toString(),EventTime.getText().toString(),getRequestCode(date,EventName.getText().toString(),
                            //    EventTime.getText().toString()));
                            alertDialog.dismiss();
                        } else {
                            saveEvent(EventName.getText().toString(), EventTime.getText().toString(), date, month, year, "off");
                            setUpCalendar();
                            alertDialog.dismiss();
                        }


                    }
                });
                builder.setView(addView);
                alertDialog = builder.create();
                alertDialog.show();

            }
        });
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String date = evenDateFormate.format(dates.get(position));
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(true);
                View showView = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_event_layout, null);
               /* TextView Events=showView.findViewById(R.id.eventname);
                TextView Time=showView.findViewById(R.id.eventtime);
                TextView Date=showView.findViewById(R.id.eventdate);*/
                // CheckBox alarmMe=showView.findViewById(R.id.alarmme);
                RecyclerView rc = showView.findViewById(R.id.EventsRv);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(showView.getContext());
                rc.setLayoutManager(layoutManager);
                rc.setHasFixedSize(true);
                //adapter
                EventRecyclerView eventRc = new EventRecyclerView(showView.getContext()
                        , CollectEventByDate(date));
                rc.setAdapter(eventRc);
                eventRc.notifyDataSetChanged();

                builder.setView(showView);
                alertDialog = builder.create();
                alertDialog.show();
                alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        setUpCalendar();
                    }
                });
                return true;
            }
        });

    }

    private void checkIN() {
        Calendar calendarloc = Calendar.getInstance();
        int DayNo = calendarloc.get(Calendar.DAY_OF_MONTH);
        int currentMonth = calendarloc.get(Calendar.MONTH);
        int currentYear = calendarloc.get(Calendar.YEAR);
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm aa", Locale.ENGLISH);
        String time = timeFormat.format(calendar.getTime());
        UUID uuid = UUID.randomUUID();
        String uuidAsString = uuid.toString();
        FirebaseAuth usrauth;
        usrauth = FirebaseAuth.getInstance();
        DatabaseReference checkInDbRef;
        checkInDbRef = FirebaseDatabase.getInstance().getReference();

        SimpleDateFormat sdf = new SimpleDateFormat("MM", Locale.getDefault());
        String month=sdf.format(calendarloc.getTime());


        int currentMonthlatest = calendar.get(Calendar.MONTH) + 1;

        HashMap<String, Object> checkinHashMap = new HashMap<>();
        checkinHashMap.put("UserUid", usrauth.getCurrentUser().getUid());
        checkinHashMap.put("CheckInStatus", "CHECKED-IN");
        checkinHashMap.put("CheckInTime", time.toString());
        checkinHashMap.put("Month", String.valueOf(month));
        checkinHashMap.put("Year", String.valueOf(currentYear));
        checkinHashMap.put("Attendance", "Present");
        checkinHashMap.put("CheckInId", uuidAsString);
        checkinHashMap.put("Day", String.valueOf(DayNo));
        checkInDbRef.child("Attendance").child(usrauth.getCurrentUser().getUid()).child(uuidAsString).updateChildren(checkinHashMap);

    }

    private void saveEvent(String event, String time, String date, String month, String year, String notify) {
        dbHelper = new DBHelper(context);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        dbHelper.SameEvent(event, time, date, month, year, notify, database);
        dbHelper.close();
        Toast.makeText(context, "Event Saved", Toast.LENGTH_SHORT).show();
    }

    public String newcheckIn() {
        Calendar calendarin = Calendar.getInstance();
        int DayNo = calendarin.get(Calendar.DAY_OF_MONTH);
        int currentMonth = calendarin.get(Calendar.MONTH);
        int currentYear = calendarin.get(Calendar.YEAR);
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm aa", Locale.ENGLISH);
        //  String time=timeFormat.format(calendar.getTime());
        UUID uuid = UUID.randomUUID();
        // String uuidAsString = uuid.toString();
        int currentMonthlatest = calendar.get(Calendar.MONTH) + 1;

        FirebaseAuth usrauth;
        usrauth = FirebaseAuth.getInstance();
        DatabaseReference attendanceDbRef, insideRef;
        attendanceDbRef = FirebaseDatabase.getInstance().getReference();
        insideRef = FirebaseDatabase.getInstance().getReference();
        attendanceDbRef.child("Attendance").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot.hasChild(usrauth.getCurrentUser().getUid())) {
                        insideRef.child("Attendance").child(usrauth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshotInside) {
                                for (DataSnapshot ds2 : snapshotInside.getChildren()) {
                                    String check_if_day_exists = ds2.child("Day").getValue().toString();
                                    String check_if_month_exists = ds2.child("Month").getValue().toString();
                                    String check_if_year_exists = ds2.child("Year").getValue().toString();
                                    int montcheck=Integer.parseInt(check_if_month_exists);

                                    if (ds2.hasChild("Year") && ds2.hasChild("Month") && ds2.hasChild("Day")) {
                                        if (check_if_year_exists.equals(String.valueOf(currentYear)) &&
                                                montcheck==currentMonthlatest &&
                                                check_if_day_exists.equals(String.valueOf(DayNo))) {
                                            checkIfExists = "EXISTS";
                                            checkInButton.setEnabled(false);
                                            checkInButton.setClickable(false);
                                            checkInButton.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.grey));
//                                            checkInButton.setText("CHECKED-IN");
                                        }
                                    } else {
                                        checkIfExists = "NOT-EXISTS";
                                        checkInButton.setClickable(true);
                                        checkInButton.setEnabled(true);
                                        checkInButton.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.greendark));
//                                        checkInButton.setText("CHECK-IN");
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                } else {
                    //Toast.makeText(getContext(), "Doesnt exist", Toast.LENGTH_SHORT).show();
                    checkIfExists = "NOT-EXISTS";
                    checkInButton.setClickable(true);
                    checkInButton.setEnabled(true);
                    checkInButton.setText("CHECK-IN");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        return checkIfExists;
    }

    public CustomCalendarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    private ArrayList<EventsModelClass> CollectEventByDate(String date) {
        ArrayList<EventsModelClass> arrayList = new ArrayList<>();
        dbHelper = new DBHelper(context);
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = dbHelper.ReadEvents(date, database);
        while (cursor.moveToNext()) {
            String event = cursor.getString(cursor.getColumnIndexOrThrow(DBStructure.EVENT));
            String time = cursor.getString(cursor.getColumnIndexOrThrow(DBStructure.TIME));
            String Date = cursor.getString(cursor.getColumnIndexOrThrow(DBStructure.DATE));
            String month = cursor.getString(cursor.getColumnIndexOrThrow(DBStructure.MONTH));
            String Year = cursor.getString(cursor.getColumnIndexOrThrow(DBStructure.YEAR));
            EventsModelClass events = new EventsModelClass(event, time, Date, month, Year);
            arrayList.add(events);
        }
        cursor.close();
        dbHelper.close();
        return arrayList;
    }

    private void InitializeLayout() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.calendar_custom_layout, this);
        NextButton = v.findViewById(R.id.forward_Button);
        PreviousButton = v.findViewById(R.id.previous_Button);
        CurrentMonth = v.findViewById(R.id.currentMonth);
        gridView = v.findViewById(R.id.grid_view);
        checkInButton = v.findViewById(R.id.check_In);
        checkInButton.setText("CHECK-IN");
        usrauth = FirebaseAuth.getInstance();
    }

    private void setUpCalendar() {
        String currentDate = dateFormat.format(calendar.getTime());
        CurrentMonth.setText(currentDate);
        selectedMonth = new SimpleDateFormat("MM", Locale.ENGLISH).format(calendar.getTime());
        dates.clear();
        Calendar monthsCalendar = (Calendar) calendar.clone();
        monthsCalendar.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayOfMonth = monthsCalendar.get(Calendar.DAY_OF_WEEK) - 1;
        monthsCalendar.add(Calendar.DAY_OF_MONTH, -firstDayOfMonth);
        monthsCalendar.set(Calendar.HOUR_OF_DAY, 0);
        monthsCalendar.set(Calendar.MINUTE, 0);
        monthsCalendar.set(Calendar.SECOND, 0);
        monthsCalendar.set(Calendar.MILLISECOND, 0);
        // CollectEventPerMonth(monthFormat.format(calendar.getTime()), yearFormat.format(calendar.getTime()));
        while (dates.size() < MAX_CALENDAR_DAYS) {
            dates.add(monthsCalendar.getTime());
            monthsCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        cmodel = new ArrayList<>();
        leavemodel = new ArrayList<>();
        usrauth = FirebaseAuth.getInstance();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("Attendance").child(Objects.requireNonNull(usrauth.getCurrentUser()).getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds1 : snapshot.getChildren()) {
                    //cmodel.clear();
                    String user_attendance_day = Objects.requireNonNull(ds1.child("Day").getValue()).toString();
                    String user_attendance_month = Objects.requireNonNull(ds1.child("Month").getValue()).toString();
                    String user_attendance_year = Objects.requireNonNull(ds1.child("Year").getValue()).toString();
                    String user_attendance_attendance = Objects.requireNonNull(ds1.child("Attendance").getValue()).toString();

                 /*  getAttendanceColor gc=new getAttendanceColor();
                    gc.setAttendance(attendanceFromDb);
                    gc.setMonth(monthFromDb);
                    gc.setDay(dayFromDb);
                    gc.setYear(yearFromDb);*/
                    //cmodel.add(gc);
                    //Log.d("fefdw",dates.toString());
                    cmodel.add(new GetAttendanceColor(user_attendance_day, user_attendance_month, user_attendance_attendance, user_attendance_year));
                    //  gridadapter = new GridAdapter(context, dates, calendar, eventsList,User_attendance_day,User_attendance_month,User_attendance_year,User_attendance_attendance);

                    DatabaseReference leaveRef = FirebaseDatabase.getInstance().getReference();
                    leaveRef.child("ApplyForLeave").child(Objects.requireNonNull(usrauth.getCurrentUser()).getUid())
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    leavemodel.clear();
                                    for (DataSnapshot leave : snapshot.getChildren()) {

                                        //follow std on  db keys variable name
                                        String CurrentTime = leave.child("CurrentTime").getValue().toString();
                                        String EndTimestamp = leave.child("EndTimestamp").getValue().toString();
                                        String From = leave.child("From").getValue().toString();
                                        String NoOfDays = leave.child("NoOfDays").getValue().toString();
                                        String StartTimestamp = leave.child("StartTimestamp").getValue().toString();
                                        String To = leave.child("To").getValue().toString();
                                        String status = leave.child("Status").getValue().toString();
                                        String reason = leave.child("LeaveType").getValue().toString();
                                        Log.d("status", status);
                                        if (status.equals("Approved")) {
                                            leavemodel.add(new LeaveDateModel(CurrentTime, EndTimestamp, From, NoOfDays, StartTimestamp, To,reason));
                                        }

                                    }
                                    Log.d("leave_mode", String.valueOf(leavemodel.size()));
                                    gridadapter.updateAdapter(dates, calendar, eventsList, cmodel, leavemodel);
                                    gridadapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                }
                System.out.println(cmodel.size());
                //gridView.setAdapter(gridadapter);
                // gridadapter = new GridAdapter(context, dates, calendar, eventsList);
                gridadapter.updateAdapter(dates, calendar, eventsList, cmodel, leavemodel);
                gridadapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    public AttendanceReportData getAttendanceData() {
        AttendanceReportData attendanceReportData = new AttendanceReportData();
        attendanceReportData.setDates(dates);
        attendanceReportData.setLeavemodel(leavemodel);
        attendanceReportData.setCmodel(cmodel);
        attendanceReportData.setSelectedMonth(selectedMonth);
        attendanceReportData.setEmployeeName(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName());
        return attendanceReportData;
    }
}

