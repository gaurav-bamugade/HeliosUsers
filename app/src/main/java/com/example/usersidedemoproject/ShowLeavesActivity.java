package com.example.usersidedemoproject;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.usersidedemoproject.Adapters.ApplyForLeavesPastAdapter;
import com.example.usersidedemoproject.Adapters.ApplyForLeavesRejecetedAdapter;
import com.example.usersidedemoproject.Adapters.ApplyForLeavesUpcomingAdapter;
import com.example.usersidedemoproject.Model.LeavesModelClass;
import com.example.usersidedemoproject.Utility.NetworkChangeListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;


import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ShowLeavesActivity extends AppCompatActivity  implements AdapterView.OnItemSelectedListener{
    NetworkChangeListener networkChangeListener=new NetworkChangeListener();
ImageButton upcoming_img_arrow1,past_img_arrow1,rejected_img_arrow1;
RecyclerView upcoming_rc,past_rc,rejected_rc;
Button apply_for_leaves;

private Dialog apply_leave_dialogue;
ImageButton leaveFromDateCalendarPicker,leaveToDateCalendarPicker,dialogue_cancel_button;
Button dialogue_bottom_cancel_button,dialogue_submit_button;
TextView leaveFromDateEditText,leaveToDateEditText;
EditText dialogue_enter_reason;
TextView dialogue_spinner_string;
Spinner dialogue_leave_type_spinner;
int day,month,year;
FirebaseAuth usrauth;
String no_leave_days="";
private DatabaseReference usrref;
private FirebaseAuth fireauth;
private String currentuserid;
ApplyForLeavesPastAdapter applyForLeavesPastAdapter;
    ApplyForLeavesUpcomingAdapter applyForLeavesUpcomingAdapter;
    ApplyForLeavesRejecetedAdapter applyForLeavesRejecetedAdapter;
ArrayList<LeavesModelClass> applyForLeavesPastModel,applyForLeavesUpcomingModel,applyForLeavesRejectedModel;
Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_leaves);
        initialize();
        setCategoryDialog();
        //loadPastRecycler();
        currentuserid=fireauth.getCurrentUser().getUid();
        usrref=  FirebaseDatabase.getInstance().getReference().child("ApplyForLeave").child(currentuserid);
        if(usrref!=null)
        {
            loadPastRecycler();
            loadRejectedRecycler();
            loadUpcomingRecycler();
        }
        else
        {

        }
        toolbar=findViewById(R.id.show_leaves_details_toolbar);
        Resources res=this.getResources() ; //resource handle
        toolbar.setTitle("Leaves");
        toolbar.setTitleTextColor(res.getColor(R.color.white));
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        //actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_24);
        actionbar.setDisplayShowCustomEnabled(true);
        upcoming_img_arrow1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(upcoming_rc.getVisibility()==View.GONE)
                {
                    upcoming_rc.setVisibility(View.VISIBLE);
                    upcoming_img_arrow1.setImageResource(R.drawable.ic_arrow_up_24);

                }
                else
                {
                    if(upcoming_rc.getVisibility()==View.VISIBLE)
                    {
                        upcoming_rc.setVisibility(View.GONE);
                        upcoming_img_arrow1.setImageResource(R.drawable.ic_arrow_down_24);

                    }
                }
            }
        });
        past_img_arrow1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(past_rc.getVisibility()==View.GONE)
                {
                    past_rc.setVisibility(View.VISIBLE);
                    past_img_arrow1.setImageResource(R.drawable.ic_arrow_up_24);

                }
                else
                {
                    if(past_rc.getVisibility()==View.VISIBLE)
                    {
                        past_rc.setVisibility(View.GONE);
                        past_img_arrow1.setImageResource(R.drawable.ic_arrow_down_24);

                    }
                }
            }
        });
        rejected_img_arrow1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rejected_rc.getVisibility()==View.GONE)
                {
                    rejected_rc.setVisibility(View.VISIBLE);
                    rejected_img_arrow1.setImageResource(R.drawable.ic_arrow_up_24);

                }
                else
                {
                    if(rejected_rc.getVisibility()==View.VISIBLE)
                    {
                        rejected_rc.setVisibility(View.GONE);
                        rejected_img_arrow1.setImageResource(R.drawable.ic_arrow_down_24);
                    }
                }
            }
        });
        apply_for_leaves.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                apply_leave_dialogue.show();
            }
        });

    }
    public void initialize()
    {

        upcoming_img_arrow1=findViewById(R.id.upcoming_img_arrow1);
        past_img_arrow1=findViewById(R.id.past_img_arrow1);
        rejected_img_arrow1=findViewById(R.id.rejected_img_arrow1);

        upcoming_rc=findViewById(R.id.upcoming_rc);
        past_rc=findViewById(R.id.past_rc);
        rejected_rc=findViewById(R.id.rejected_rc);

        apply_for_leaves=findViewById(R.id.apply_for_leaves_btn);
        applyForLeavesPastModel=new ArrayList<>();
        applyForLeavesUpcomingModel=new ArrayList<>();
        applyForLeavesRejectedModel=new ArrayList<>();
        fireauth= FirebaseAuth.getInstance();
        currentuserid=fireauth.getCurrentUser().getUid();


         //////UpcomingRecycler
        LinearLayoutManager upcming=new LinearLayoutManager(ShowLeavesActivity.this);

        upcoming_rc.setLayoutManager(upcming);
        upcoming_rc.setHasFixedSize(true);
        applyForLeavesUpcomingAdapter=new ApplyForLeavesUpcomingAdapter(ShowLeavesActivity.this,applyForLeavesUpcomingModel);
        upcoming_rc.setAdapter(applyForLeavesUpcomingAdapter);

        //PastRecycler
        LinearLayoutManager past=new LinearLayoutManager(ShowLeavesActivity.this);
        past_rc.setLayoutManager(past);
        past_rc.setHasFixedSize(true);
        applyForLeavesPastAdapter=new ApplyForLeavesPastAdapter(ShowLeavesActivity.this,applyForLeavesPastModel);
        past_rc.setAdapter(applyForLeavesPastAdapter);

        //////// rejected Recycler
        LinearLayoutManager rejected=new LinearLayoutManager(ShowLeavesActivity.this);
        rejected_rc.setLayoutManager(rejected);
        rejected_rc.setHasFixedSize(true);
        applyForLeavesRejecetedAdapter=new ApplyForLeavesRejecetedAdapter(ShowLeavesActivity.this,applyForLeavesRejectedModel);
        rejected_rc.setAdapter(applyForLeavesRejecetedAdapter);


    }
    private void loadPastRecycler(){
        usrref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                applyForLeavesPastModel.clear();
                for(DataSnapshot ds:snapshot.getChildren())
                {
                        String apply_for_leave_id =ds.child("ApplyForLeaveId").getValue().toString();;
                        String from=ds.child("From").getValue().toString();
                        String leave_type=ds.child("LeaveType").getValue().toString();
                        String no_of_days=ds.child("NoOfDays").getValue().toString();
                        String reason =ds.child("Reason").getValue().toString();
                        String remark =ds.child("Remark").getValue().toString();
                        String status =ds.child("Status").getValue().toString();
                        String to =ds.child("To").getValue().toString();
                        String user_uid =ds.child("UserUid").getValue().toString();
                        if(currentuserid.equals(user_uid))
                        {
                            if(status.equals("Approved"))
                            {
                                applyForLeavesPastModel.add(new LeavesModelClass(apply_for_leave_id,from,
                                        leave_type,no_of_days,reason,remark,status,to,user_uid));
                            }
                        }

                }
                applyForLeavesPastAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
    private void loadUpcomingRecycler(){
        usrref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                applyForLeavesUpcomingModel.clear();
                for(DataSnapshot ds:snapshot.getChildren())
                {
                    String apply_for_leave_id =ds.child("ApplyForLeaveId").getValue().toString();;
                    String from=ds.child("From").getValue().toString();
                    String leave_type=ds.child("LeaveType").getValue().toString();
                    String no_of_days=ds.child("NoOfDays").getValue().toString();
                    String reason =ds.child("Reason").getValue().toString();
                    String remark =ds.child("Remark").getValue().toString();
                    String status =ds.child("Status").getValue().toString();
                    String to =ds.child("To").getValue().toString();
                    String user_uid =ds.child("UserUid").getValue().toString();

                    if(currentuserid.equals(user_uid))
                    {
                        if(status.equals("Pending"))
                        {
                            applyForLeavesUpcomingModel.add(new LeavesModelClass(apply_for_leave_id,from,
                                    leave_type,no_of_days,reason,remark,status,to,user_uid));
                        }
                    }
                }
                applyForLeavesPastAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
    private void loadRejectedRecycler(){
        usrref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                applyForLeavesRejectedModel.clear();
                for(DataSnapshot ds:snapshot.getChildren())
                {
                    String apply_for_leave_id =ds.child("ApplyForLeaveId").getValue().toString();;
                    String from=ds.child("From").getValue().toString();
                    String leave_type=ds.child("LeaveType").getValue().toString();
                    String no_of_days=ds.child("NoOfDays").getValue().toString();
                    String reason =ds.child("Reason").getValue().toString();
                    String remark =ds.child("Remark").getValue().toString();
                    String status =ds.child("Status").getValue().toString();
                    String to =ds.child("To").getValue().toString();
                    String user_uid =ds.child("UserUid").getValue().toString();

                    if(currentuserid.equals(user_uid))
                    {
                        if(status.equals("Rejected"))
                        {
                            applyForLeavesRejectedModel.add(new LeavesModelClass(apply_for_leave_id,from,
                                    leave_type,no_of_days,reason,remark,status,to,user_uid));
                        }
                    }

                }
                applyForLeavesPastAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
    private void setCategoryDialog() {
        Calendar calendar= Calendar.getInstance();
        SimpleDateFormat FromToDateFormate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        apply_leave_dialogue =new Dialog(this);
        apply_leave_dialogue.setContentView(R.layout.add_category_dialog);
        apply_leave_dialogue.getWindow().setBackgroundDrawable(getDrawable(R.drawable.rounded_box));
        apply_leave_dialogue.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        apply_leave_dialogue.setCancelable(true);

        leaveFromDateCalendarPicker=apply_leave_dialogue.findViewById(R.id.dialogue_from_date_calendar_picker);
        leaveToDateCalendarPicker=apply_leave_dialogue.findViewById(R.id.dialogue_to_date_calendar_picker);
        dialogue_cancel_button=apply_leave_dialogue.findViewById(R.id.close_dialogue_top_button);
        dialogue_bottom_cancel_button=apply_leave_dialogue.findViewById(R.id.dialogue_cancel_btm_button);

        leaveFromDateEditText=apply_leave_dialogue.findViewById(R.id.dialogue_from_date_edit_text);
        leaveToDateEditText=apply_leave_dialogue.findViewById(R.id.dialogue_to_date_edit_text);
        dialogue_enter_reason=apply_leave_dialogue.findViewById(R.id.dialogue_enter_reason);
        dialogue_leave_type_spinner=apply_leave_dialogue.findViewById(R.id.dialogue_pick_leave_type);
        dialogue_spinner_string=apply_leave_dialogue.findViewById(R.id.dialogue_spinner_string);
        dialogue_submit_button=apply_leave_dialogue.findViewById(R.id.dialogue_submit_button);

        leaveFromDateCalendarPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                year=calendar.get(Calendar.YEAR);
                month=calendar.get(Calendar.MONTH);
                day=calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog=new DatePickerDialog(ShowLeavesActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        leaveFromDateEditText.setText(dayOfMonth+"-"+(month+1)+"-"+year);
                    }
                },year,month,day);
                datePickerDialog.setCancelable(true);
                datePickerDialog.show();

            }
        });
        leaveToDateCalendarPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                year=calendar.get(Calendar.YEAR);
                month=calendar.get(Calendar.MONTH);
                day=calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog=new DatePickerDialog(ShowLeavesActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        leaveToDateEditText.setText(dayOfMonth+"-"+(month+1)+"-"+year);
                    }
                },year,month,day);
                datePickerDialog.setCancelable(true);
                datePickerDialog.show();

            }
        });
        dialogue_cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                apply_leave_dialogue.cancel();
            }
        });
        dialogue_bottom_cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                apply_leave_dialogue.cancel();
            }
        });

        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(this,R.array.leave_types,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dialogue_leave_type_spinner.setAdapter(adapter);
        dialogue_leave_type_spinner.setOnItemSelectedListener(this);




        SimpleDateFormat myformat=new SimpleDateFormat("DD-M-yyyy");


        dialogue_submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(ShowLeavesActivity.this, "no_leave_days"+no_leave_days, Toast.LENGTH_SHORT).show();

                if(leaveFromDateEditText.getText().toString().isEmpty())
                {
                    leaveFromDateEditText.setError("Required");
                    return;
                }
                if(leaveToDateEditText.getText().toString().isEmpty())
                {
                    leaveToDateEditText.setError("Required");
                    return;
                }

                if(dialogue_enter_reason.getText().toString().isEmpty())
                {
                    dialogue_enter_reason.setError("Required");
                    return;
                }
                else
                {
                    try{
                        Date date1=myformat.parse(leaveFromDateEditText.getText().toString());
                        Date date2=myformat.parse(leaveToDateEditText.getText().toString());
                        long diff=date2.getTime()-date1.getTime();
                        System.out.println("Days :"+TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));
                        no_leave_days=String.valueOf(TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));
                        if(TextUtils.isEmpty(leaveFromDateEditText.getText().toString()) && TextUtils.isEmpty(leaveToDateEditText.getText().toString())
                                && TextUtils.isEmpty(dialogue_spinner_string.getText().toString()) && TextUtils.isEmpty(dialogue_enter_reason.getText().toString())){
                            Toast.makeText(ShowLeavesActivity.this,"Please Enter the Required Credentials",Toast.LENGTH_LONG).show();
                        }
                        if(!leaveFromDateEditText.getText().toString().isEmpty() && !leaveToDateEditText.getText().toString().isEmpty()
                                && !dialogue_spinner_string.getText().toString().isEmpty() && !dialogue_enter_reason.getText().toString().isEmpty())
                        {

                            SimpleDateFormat dt3 = new SimpleDateFormat("dd-M-yyyy");
                            String timestamp=""+System.currentTimeMillis();
                            usrauth= FirebaseAuth.getInstance();
                            DatabaseReference applyForLeavesDbRef,managerApproval;
                            managerApproval=FirebaseDatabase.getInstance().getReference();
                            applyForLeavesDbRef= FirebaseDatabase.getInstance().getReference();
                            UUID uuid = UUID.randomUUID();
                            String uuidAsString = uuid.toString();

                          /*  Timestamp  tstart = new Timestamp(((java.util.Date)df.parse(leaveFromDateEditText.getText().toString())).getTime());
                            Timestamp  tend = new Timestamp(((java.util.Date)df.parse(leaveToDateEditText.getText().toString())).getTime());*/

                            Date startDate= dt3.parse(leaveFromDateEditText.getText().toString());

                            Calendar calendarStart = Calendar.getInstance();
                            calendarStart.setTime(startDate);

                            Date startEnd= dt3.parse(leaveToDateEditText.getText().toString());
                            Calendar calendarEnd= Calendar.getInstance();
                            calendarEnd.setTime(startEnd);
                            long startDt=calendarStart.getTimeInMillis();
                            long End=calendarEnd.getTimeInMillis();
                            Log.d("startToEnd",String.valueOf(startDt)+"///"+String.valueOf(End));
;                           HashMap<String,Object> checkinHashMap=new HashMap<>();
                            checkinHashMap.put("UserUid",""+usrauth.getCurrentUser().getUid());
                            checkinHashMap.put("LeaveType",""+dialogue_spinner_string.getText().toString());
                            checkinHashMap.put("NoOfDays",""+no_leave_days.toString());
                            checkinHashMap.put("From",""+leaveFromDateEditText.getText().toString());
                            checkinHashMap.put("To",""+leaveToDateEditText.getText().toString());
                            checkinHashMap.put("Status","Pending");
                            checkinHashMap.put("Remark","NA");
                            checkinHashMap.put("Reason",""+dialogue_enter_reason.getText().toString());
                            checkinHashMap.put("ApplyForLeaveId",""+uuidAsString.toString());
                            checkinHashMap.put("StartTimestamp",""+String.valueOf(startDt));
                            checkinHashMap.put("EndTimestamp",""+String.valueOf(End));
                            checkinHashMap.put("CurrentTime",""+timestamp.toString());

                            applyForLeavesDbRef.child("ApplyForLeave").child(usrauth.getCurrentUser().getUid()).child(uuidAsString).updateChildren(checkinHashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    UUID newUUId = UUID.randomUUID();
                                    String newid = newUUId.toString();
                                    HashMap<String,Object> pendingApprove=new HashMap<>();
                                    pendingApprove.put("ApprovalId",""+newid.toString());
                                    pendingApprove.put("ApproveType","Leave Approval");
                                    pendingApprove.put("Status","Pending");
                                    pendingApprove.put("ApprovalTaskLeaveId",""+uuidAsString.toString());
                                    pendingApprove.put("Reason",""+dialogue_enter_reason.getText().toString());
                                    pendingApprove.put("Remark","NA");
                                    pendingApprove.put("StartDate",""+leaveFromDateEditText.getText().toString());
                                    pendingApprove.put("EndDate",""+leaveToDateEditText.getText().toString());
                                    pendingApprove.put("SenderUID",""+usrauth.getCurrentUser().getUid());
                                    pendingApprove.put("CurrentTime",""+timestamp.toString());

                                    managerApproval.child("PendingApprovals").child(newid).updateChildren(pendingApprove).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            apply_leave_dialogue.dismiss();
                                        }
                                    });

                                }
                            });
                        }
                    }
                    catch (Exception e)
                    {
                    }
                    Log.d("dialogue_info_check",leaveFromDateEditText.getText().toString());
                    Log.d("dialogue_info_check",leaveToDateEditText.getText().toString());
                    Log.d("dialogue_info_check",dialogue_spinner_string.getText().toString());
                    Log.d("dialogue_info_check",dialogue_enter_reason.getText().toString());
                    Log.d("dialogue_info_check",no_leave_days);
                }

            }
        });
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(position==0)
        {
            Toast.makeText(ShowLeavesActivity.this,"Please choose option",Toast.LENGTH_SHORT).show();
        }
        else
        {
            String spinnerString=parent.getItemAtPosition(position).toString();
            dialogue_spinner_string.setText(spinnerString);

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent i=new Intent(this,MainActivity.class);
                startActivity(i);
                return true;
        }
        return super.onOptionsItemSelected(item);
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

     /*    if(leaveFromDateEditText.getText()==null || leaveFromDateEditText.getText().toString().isEmpty())
                {
                    leaveFromDateEditText.setError("Required");
                    return;
                }
                if(leaveToDateEditText.getText()==null || leaveToDateEditText.getText().toString().isEmpty())
                {
                    leaveToDateEditText.setError("Required");
                    return;
                }
                if(dialogue_spinner_string.getText()==null || dialogue_spinner_string.getText().toString().isEmpty())
                {
                    dialogue_spinner_string.setError("Required");
                    return;
                }*/



//btn=findViewById(R.id.arrowBtn);
// btn.setImageResource(R.drawable.ic_arrow_down_24);
// img=findViewById(R.id.imgss);

        /*btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(img.getVisibility()==View.GONE)
                {
                    img.setVisibility(View.VISIBLE);
                    btn.setImageResource(R.drawable.ic_arrow_up_24);
                    check="clicked";
                }
                else
                {
                    if(img.getVisibility()==View.VISIBLE)
                    {
                        img.setVisibility(View.GONE);
                        btn.setImageResource(R.drawable.ic_arrow_down_24);
                        check="not-clicked";
                    }
                }

              *//*  if(check.equals("not-clicked"))
                {
                    img.setVisibility(View.VISIBLE);
                    btn.setImageResource(R.drawable.ic_arrow_up_24);
                    check="clicked";
                }
                else
                {
                    if(check.equals("clicked"))
                    {
                        img.setVisibility(View.GONE);
                        btn.setImageResource(R.drawable.ic_arrow_down_24);
                        check="not-clicked";
                    }
                }*//*


            }
        });*/







/*


/*  DateFormat df2 = new SimpleDateFormat("DD-mm-yyyy", Locale.getDefault());
                            Date lFromDate1 = df2.parse(leaveFromDateEditText.getText().toString());
                            System.out.println("gpsdate :" + lFromDate1);
                            Timestamp fromTS1 = new Timestamp(lFromDate1.getTime());
                            Log.d("checkdatesboth",tstart.getTime()+" //"+tend.getTime()+" //"
                                    +leaveFromDateEditText.getText().toString());






                            Date date = new Date(leaveFromDateEditText.getText().toString());
                            Timestamp ts=new Timestamp(date.getTime());
                            SimpleDateFormat formatter = new SimpleDateFormat("DD/MM/yyyy");
                            System.out.println(formatter.format(ts));*/
/*if(leaveFromDateEditText.getText()==null || leaveFromDateEditText.getText().toString().isEmpty())
        {
        if(leaveToDateEditText.getText()==null || leaveToDateEditText.getText().toString().isEmpty())
        {
        if(dialogue_spinner_string.getText()==null || dialogue_spinner_string.getText().toString().isEmpty())
        {
        dialogue_spinner_string.setError("Required");
        return;
        }

        leaveToDateEditText.setError("Required");
        return;
        }
        leaveFromDateEditText.setError("Required");
        return;
        }

        if(dialogue_enter_reason.getText()==null || dialogue_enter_reason.getText().toString().isEmpty()
        && leaveFromDateEditText.getText()==null || leaveFromDateEditText.getText().toString().isEmpty()
        && leaveToDateEditText.getText()==null || leaveToDateEditText.getText().toString().isEmpty()
        && dialogue_spinner_string.getText()==null || dialogue_spinner_string.getText().toString().isEmpty())
        {
        dialogue_enter_reason.setError("Required");
        leaveFromDateEditText.setError("Required");
        leaveToDateEditText.setError("Required");
        dialogue_spinner_string.setError("Required");
        return;
        }
        else
        {
        usrauth= FirebaseAuth.getInstance();
        DatabaseReference applyForLeavesDbRef;
        applyForLeavesDbRef= FirebaseDatabase.getInstance().getReference();
        UUID uuid = UUID.randomUUID();
        String uuidAsString = uuid.toString();

        HashMap<String,Object> checkinHashMap=new HashMap<>();
        checkinHashMap.put("user_uid",usrauth.getCurrentUser().getUid());
        checkinHashMap.put("leave_type",leave_type_string);
        checkinHashMap.put("no_of_days",no_leave_days.toString());
        checkinHashMap.put("from",fromDateString.toString());
        checkinHashMap.put("to",fromToString.toString());
        checkinHashMap.put("status","Pending");
        checkinHashMap.put("remark","NA");
        checkinHashMap.put("reason",leave_reason_string.toString());
        checkinHashMap.put("apply_for_leave_id",uuidAsString.toString());

        applyForLeavesDbRef.child("ApplyForLeave").child(usrauth.getCurrentUser().getUid()).child(uuidAsString).updateChildren(checkinHashMap);

        }*/
