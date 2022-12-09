package com.example.usersidedemoproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ApprovalDetailsActivity extends AppCompatActivity {
    TextView approval_details_type,approval_arrive_time,approval_details_days,approval_details_start_date
            ,approval_details_status,approval_details_end_date,approval_details_remark,reason_details_reason;
    Toolbar toolbar;
    String approvalId,approvalTaskLeaveId,approveType,endDate,currentTime,reason,remark,senderUID,startDate,status;
    Button details_approve_button,details_reject_button;
    private DatabaseReference usrref,approveRef,pendingRef,leaveRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approval_details);
        Resources res=this.getResources() ; //resource handle

        toolbar=findViewById(R.id.leave_details_toolbar);
        toolbar.setTitle("Leave Details");
        toolbar.setTitleTextColor(res.getColor(R.color.white));
        Intent intent = getIntent();
        approvalId = intent.getStringExtra("approvalId");
        approvalTaskLeaveId = intent.getStringExtra("approvalTaskLeaveId");
        approveType = intent.getStringExtra("approveType");
        endDate = intent.getStringExtra("endDate");
        currentTime = intent.getStringExtra("currentTime");
        reason = intent.getStringExtra("reason");
        remark = intent.getStringExtra("remark");
        senderUID = intent.getStringExtra("senderUID");
        startDate = intent.getStringExtra("startDate");
        status = intent.getStringExtra("status");
        approveRef=  FirebaseDatabase.getInstance().getReference().child("ApplyForLeave");
        pendingRef=FirebaseDatabase.getInstance().getReference().child("PendingApprovals");
        leaveRef=FirebaseDatabase.getInstance().getReference().child("ApplyForLeave");

        initialize();
    }
    public void initialize()
    {
        approval_details_type=findViewById(R.id.approval_details_type);
        approval_arrive_time=findViewById(R.id.approval_arrive_time);
        approval_details_start_date=findViewById(R.id.approval_details_start_date);
        approval_details_status=findViewById(R.id.approval_details_status);
        approval_details_end_date=findViewById(R.id.approval_details_end_date);
       // approval_details_days=findViewById(R.id.approval_details_days);
        approval_details_remark=findViewById(R.id.approval_details_remark);
        reason_details_reason=findViewById(R.id.reason_details_reason);
        details_approve_button=findViewById(R.id.details_approve_button);
        details_reject_button=findViewById(R.id.details_reject_button);
        Calendar cal=Calendar.getInstance();
        cal.setTimeInMillis(Long.parseLong(currentTime));


      /*  SimpleDateFormat currentDate= new SimpleDateFormat("MMM, dd, yyyy");
        String saveCurrentDate=currentDate.format(cal.getTime());*/

        SimpleDateFormat CurrentTime= new SimpleDateFormat("dd-MM-yyyy hh: mm: a");
        String saveCurrentTime=CurrentTime.format(cal.getTime());


        approval_details_type.setText(approveType);
        approval_arrive_time.setText(saveCurrentTime);
        approval_details_start_date.setText(startDate);
        approval_details_status.setText(status);
        approval_details_end_date.setText(endDate);
        approval_details_remark.setText(remark);
        reason_details_reason.setText(reason);
        //approval_details_days.setText();


        details_approve_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(approveType.equals("Leave Approval"))
                {
                    approveRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot ds:snapshot.getChildren())
                            {
                                for(DataSnapshot ds2:ds.getChildren())
                                {
                                    String userId=ds2.child("UserUid").getValue().toString().trim();
                                    String ApplyForLeaveId=ds2.child("ApplyForLeaveId").getValue().toString().trim();
                                    if(approvalTaskLeaveId.equals(ApplyForLeaveId))
                                    {
                                        HashMap<String,Object> msgMap=new HashMap<>();
                                        msgMap.put("Status","Approved");
                                        leaveRef.child(userId).child(ApplyForLeaveId).updateChildren(msgMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                pendingRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot pensnapshot) {

                                                        for(DataSnapshot pending:pensnapshot.getChildren())
                                                        {
                                                            String approvId=pending.child("ApprovalTaskLeaveId").getValue().toString().trim();
                                                            String pendingApproveId=pending.child("ApprovalId").getValue().toString().trim();
                                                            if(approvalTaskLeaveId.equals(approvId))
                                                            {
                                                                pendingRef.child(pendingApproveId).updateChildren(msgMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        Toast.makeText(ApprovalDetailsActivity.this, "Approved Succcessfully", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                            }
                                                        }

                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });
                                            }
                                        });
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
              /*  else if(pmodel.getApproveType().equals("Task Approval"))
                {

                }*/

            }
        });
        details_reject_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(approveType.equals("Leave Approval"))
                {
                    approveRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot ds:snapshot.getChildren())
                            {
                                for(DataSnapshot ds2:ds.getChildren())
                                {
                                    String userId=ds2.child("UserUid").getValue().toString().trim();
                                    String ApplyForLeaveId=ds2.child("ApplyForLeaveId").getValue().toString().trim();
                                    if(approvalTaskLeaveId.equals(ApplyForLeaveId))
                                    {
                                        HashMap<String,Object> msgMap=new HashMap<>();
                                        msgMap.put("Status","Rejected");
                                        leaveRef.child(userId).child(ApplyForLeaveId).updateChildren(msgMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                pendingRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot pensnapshot) {

                                                        for(DataSnapshot pending:pensnapshot.getChildren())
                                                        {
                                                            String approvId=pending.child("ApprovalTaskLeaveId").getValue().toString().trim();
                                                            String pendingApproveId=pending.child("ApprovalId").getValue().toString().trim();
                                                            if(approvalTaskLeaveId.equals(approvId))
                                                            {
                                                                pendingRef.child(pendingApproveId).updateChildren(msgMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        Toast.makeText(ApprovalDetailsActivity.this, "Rejected", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                            }
                                                        }

                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });
                                            }
                                        });
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                /*else if(pmodel.getApproveType().equals("Task Approval"))
                {

                }*/

            }
        });

    }
}