package com.example.usersidedemoproject;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.usersidedemoproject.Utility.NetworkChangeListener;

public class LeaveDetailsActivity extends AppCompatActivity {
    NetworkChangeListener networkChangeListener=new NetworkChangeListener();
TextView leave_details_annual_leave,leave_details_days,leave_details_start_date,
        leave_details_end_date,leave_details_status,leave_details_remark,leave_details_reason;
String getleave_details_annual_leave,getleave_details_days,getleave_details_start_date,
    getleave_details_end_date,getleave_details_status,getleave_details_remark,getleave_details_reason;
Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_details);
        Resources res=this.getResources() ; //resource handle

        toolbar=findViewById(R.id.leave_details_toolbar);
        toolbar.setTitle("Leave Details");
        toolbar.setTitleTextColor(res.getColor(R.color.white));


        Intent intent = getIntent();
        getleave_details_annual_leave = intent.getStringExtra("leave_type");
        getleave_details_days = intent.getStringExtra("no_of_days");
        getleave_details_start_date = intent.getStringExtra("from");
        getleave_details_end_date = intent.getStringExtra("to");
        getleave_details_status = intent.getStringExtra("status");
        getleave_details_remark = intent.getStringExtra("remark");
        getleave_details_reason = intent.getStringExtra("reason");

        leave_details_annual_leave=findViewById(R.id.leave_details_annual_leave);
        leave_details_days=findViewById(R.id.leave_details_days);
        leave_details_start_date=findViewById(R.id.leave_details_start_date);
        leave_details_end_date=findViewById(R.id.leave_details_end_date);
        leave_details_status=findViewById(R.id.leave_details_status);
        leave_details_remark=findViewById(R.id.leave_details_remark);
        leave_details_reason=findViewById(R.id.leave_details_reason);


        leave_details_annual_leave.setText(getleave_details_annual_leave);
        leave_details_days.setText(getleave_details_days);
        leave_details_start_date.setText(getleave_details_start_date);
        leave_details_end_date.setText(getleave_details_end_date);
        leave_details_status.setText(getleave_details_status);
        leave_details_remark.setText(getleave_details_remark);
        leave_details_reason.setText(getleave_details_reason);

    }
    @Override
    protected void onStart() {
        IntentFilter filter=new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener,filter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }
}