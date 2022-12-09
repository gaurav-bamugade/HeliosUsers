package com.example.usersidedemoproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.usersidedemoproject.Adapters.AttendanceReportAdapter;
import com.example.usersidedemoproject.Adapters.LeaveReportAdapter;
import com.example.usersidedemoproject.Model.AttendanceReportData;
import com.example.usersidedemoproject.Model.GetAttendanceColor;
import com.example.usersidedemoproject.Model.LeaveDateModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class GenerateAttendancePdfActivity extends AppCompatActivity {

    private RelativeLayout pdfView;
    Bitmap bitmap;
    ArrayList<String> monthDates;
    TextView employeeName, attendanceMonth;
    RecyclerView attendanceRecycler, leaveRecycler;
    FloatingActionButton createPdf;
    AttendanceReportData attendanceReportData;
    ArrayList<HashMap<String, String>> attendanceList;
    ArrayList<HashMap<String, String>> leaveList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_attendance_pdf);
        if (getIntent().hasExtra("attendance_data")) {
            attendanceReportData = (AttendanceReportData) getIntent().getSerializableExtra("attendance_data");
        }
        if (attendanceReportData == null) {
            Toast.makeText(getApplicationContext(), "Couldn't load Pdf generator!!", Toast.LENGTH_SHORT).show();
            finish();
        }

        pdfView = findViewById(R.id.pdf_view);
        monthDates = new ArrayList<>();
        employeeName = findViewById(R.id.employee_name);
        attendanceMonth = findViewById(R.id.attendance_month);
        createPdf = findViewById(R.id.create_pdf);
        attendanceRecycler = findViewById(R.id.attendance_rec);
        leaveRecycler = findViewById(R.id.leave_rec);
        initData();
        createPdf.setOnClickListener(v -> {
            bitmap = LoadBitMap(pdfView, pdfView.getWidth(), pdfView.getHeight());
            createPdf();
        });
    }

    private void initData() {
        attendanceList = new ArrayList<>();
        leaveList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        for (LeaveDateModel ldm : attendanceReportData.getLeavemodel()) {
            Date date = null;
            try {
                Timestamp ts = new Timestamp(Long.parseLong(ldm.getStartTimestamp()));
                date = new Date(ts.getTime());
                Date startDate = new Date(new Timestamp(Long.parseLong(ldm.getStartTimestamp())).getTime());
                Date endDate = new Date(new Timestamp(Long.parseLong(ldm.getEndTimestamp())).getTime());
                if (isTimeStampInLeaveDate(
                        startDate.getMonth() + 1,
                        endDate.getMonth() + 1,
                        Integer.parseInt(attendanceReportData.getSelectedMonth()))) {
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("start_date", sdf.format(startDate));
                    hashMap.put("end_date", sdf.format(endDate));
                    hashMap.put("reason", ldm.getReason());
                    hashMap.put("start_timestamp", ldm.getStartTimestamp());
                    hashMap.put("end_timestamp", ldm.getEndTimestamp());
                    leaveList.add(hashMap);

                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        for (GetAttendanceColor ga : attendanceReportData.getCmodel()) {
            System.out.println(ga.getMonth());
            System.out.println(attendanceReportData.getSelectedMonth());
            if (Integer.parseInt(attendanceReportData.getSelectedMonth())== Integer.parseInt(ga.getMonth())) {
                HashMap<String, String> attendanceData = new HashMap<String, String>();
                attendanceData.put("date", ga.getDate());
                attendanceData.put("status", ga.getAttendance());
                attendanceList.add(attendanceData);
            }
        }
        attendanceReportData.getDates().removeIf(date1 -> date1.getMonth()+1 != Integer.parseInt(attendanceReportData.getSelectedMonth()));
        attendanceReportData.getDates().forEach(date -> System.out.println("monnn"+ date.toString()));

        for (Date calDate : attendanceReportData.getDates()) {
            HashMap<String, String> attHash = attendanceList.stream().
                    filter(attendanceDataHash -> Objects.equals(attendanceDataHash.get("date"), sdf.format(calDate))).findFirst().orElse(null);
            System.out.println("dddd"+ attHash);
            HashMap<String, String> leaveHash = leaveList.stream().filter(leaveDateHash -> isTimeStampInLeaveDate(leaveDateHash.get("start_timestamp"), leaveDateHash.get("end_timestamp"), calDate)
            ).findFirst().orElse(null);

            Calendar startDate = Calendar.getInstance();
            startDate.set(calDate.getYear(), calDate.getMonth()+1, calDate.getDate());
            boolean isSunday = startDate.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
            if (attHash == null && leaveHash == null && !isSunday) {
                HashMap<String, String> attendanceData = new HashMap<String, String>();
                attendanceData.put("date", sdf.format(calDate));
                attendanceData.put("status", "Absent");
                attendanceList.add(attendanceData);
            }

        }

        employeeName.setText("Employee Name : " + attendanceReportData.getEmployeeName());
        attendanceMonth.setText("Attendance month : " + Month.of(Integer.parseInt(attendanceReportData.getSelectedMonth())).name());
        attendanceList.sort(new Comparator<HashMap<String, String>>() {
            @Override
            public int compare(HashMap<String, String> o1, HashMap<String, String> o2) {
                try {
                    return sdf.parse(o1.get("date")).compareTo(sdf.parse(o2.get("date")));
                } catch (ParseException|NullPointerException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        });
        AttendanceReportAdapter attendanceReportAdapter = new AttendanceReportAdapter(attendanceList, getApplicationContext());
        attendanceRecycler.setAdapter(attendanceReportAdapter);
        attendanceRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        LeaveReportAdapter leaveReportAdapter = new LeaveReportAdapter(leaveList, getApplicationContext());
        leaveRecycler.setAdapter(leaveReportAdapter);
        leaveRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


    }

    private boolean isTimeStampInLeaveDate(int startMonth, int endMonth, int selectedMonth) {
        try {
            return startMonth <= selectedMonth && selectedMonth <= endMonth;
//            return Long.parseLong(startTimeStamp) <= currentDate.getTime() && currentDate.getTime() <= Long.parseLong(endTimeStamp);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isTimeStampInLeaveDate(String startTimeStamp, String endTimeStamp, Date currentDate) {
        try {
            return Long.parseLong(startTimeStamp) <= currentDate.getTime() && currentDate.getTime() <= Long.parseLong(endTimeStamp);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private Bitmap LoadBitMap(View v, int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        v.draw(canvas);
        return bitmap;
    }

    private void createPdf() {
        Resources res = GenerateAttendancePdfActivity.this.getResources();
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        float width = displayMetrics.widthPixels;
        float height = displayMetrics.heightPixels;
        int converWidth = (int) width, converHeight = (int) height;
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(converWidth, converHeight, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();
        paint.setColor(res.getColor(R.color.white));
        canvas.drawPaint(paint);
        bitmap = Bitmap.createScaledBitmap(bitmap, converWidth, converHeight, true);
        canvas.drawBitmap(bitmap, 0, 0, null);
        document.finishPage(page);
        String pdfpath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        String filename=System.currentTimeMillis()+".pdf";
        String filepath=pdfpath+ "/"+filename;
        File file = new File(pdfpath, "/"+filename);
        try {
            document.writeTo(new FileOutputStream(file));
            Toast.makeText(getApplicationContext(), "Pdf saved to "+filepath, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        document.close();
    }
}