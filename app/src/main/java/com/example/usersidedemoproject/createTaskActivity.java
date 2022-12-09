package com.example.usersidedemoproject;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.usersidedemoproject.Utility.NetworkChangeListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

public class createTaskActivity extends AppCompatActivity implements TaskSelectUserList {
    NetworkChangeListener networkChangeListener=new NetworkChangeListener();
public RelativeLayout status_for_working,select_start_date,select_priority_task,estimated_select_time,select_team;
int day,month,year;
TextView selected_start_date,selected_estimated_time,status_of_working,selected_prioriy;
int Chour,Cminute;
Button assign_task_btn;
ArrayList<String>lm;
EditText task_nameA,task_deskD;
FirebaseAuth usrauth;
    @Override
    public void taskuserList(ArrayList<String> arrayList) {

        for(String arr:arrayList)
        {
            lm.add(arr);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);
        Calendar calendar= Calendar.getInstance();
        lm=new ArrayList<String>();
        status_for_working=findViewById(R.id.status_for_working);
        select_start_date=findViewById(R.id.select_start_date);
        selected_start_date=findViewById(R.id.selected_start_date);
        select_priority_task=findViewById(R.id.select_priority_task);
        estimated_select_time=findViewById(R.id.estimated_select_time);
        selected_estimated_time=findViewById(R.id.selected_estimated_time);
        assign_task_btn=findViewById(R.id.assign_task_btn);
        status_of_working=findViewById(R.id.status_of_working);
        selected_prioriy=findViewById(R.id.selected_prioriy);
        task_nameA=findViewById(R.id.task_name);
        task_deskD=findViewById(R.id.task_Description);

        //select_team=findViewById(R.id.select_team);
       /* select_team.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AssignBlankFragment assignTaskUsersList=new AssignBlankFragment();
                assignTaskUsersList.show(getSupportFragmentManager(),assignTaskUsersList.getTag());
            }
        });*/
        status_for_working.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statusOfTaskFragment statusOfTaskFragment=new statusOfTaskFragment();
               statusOfTaskFragment.show(getSupportFragmentManager(),statusOfTaskFragment.getTag());



            }
        });

        select_start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                year=calendar.get(Calendar.YEAR);
                month=calendar.get(Calendar.MONTH);
                day=calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog=new DatePickerDialog(createTaskActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        selected_start_date.setText(dayOfMonth+"-"+(month+1)+"-"+year);
                    }
                },year,month,day);
                datePickerDialog.setCancelable(true);
                datePickerDialog.show();

            }
        });
        select_priority_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                priorityForTaskFragment priorityFragment=new priorityForTaskFragment();
                priorityFragment.show(getSupportFragmentManager(),priorityFragment.getTag());

            }
        });

        estimated_select_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog=new TimePickerDialog(createTaskActivity.this
                        , new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        Chour=hourOfDay;
                        Cminute=minute;

                        Calendar calendar1=Calendar.getInstance();
                        calendar1.set(0,0,0,Chour,Cminute);
                        selected_estimated_time.setText(DateFormat.format("hh mm aa",calendar1));
                    }
                },12,0,false
                );
                timePickerDialog.updateTime(Chour,Cminute);
                timePickerDialog.show();
            }
        });

        //String taskName=task_name.getText().toString();
       /// String taskDes=task_desk.getText().toString();


        Calendar dateCalendar = Calendar.getInstance(Locale.ENGLISH);
      int  alarmYear=dateCalendar.get(Calendar.YEAR);
       int alarmMonth=dateCalendar.get(Calendar.MONTH);
      int  alaramDay=dateCalendar.get(Calendar.DAY_OF_MONTH);
      String date=String.valueOf(alaramDay)+"/"+String.valueOf(alarmMonth)+"/"+String.valueOf(alarmYear);

      Log.d("showtodaydate",date);

       String statusOfWorking= status_of_working.getText().toString();
       String  selectedStartDate=selected_start_date.getText().toString();
       String selectedPriority=selected_prioriy.getText().toString();
       String selectEstimatedTime=selected_estimated_time.getText().toString();
        usrauth= FirebaseAuth.getInstance();
        DatabaseReference applyForLeavesDbRef,TaskCreatorRole;
        applyForLeavesDbRef= FirebaseDatabase.getInstance().getReference();
        TaskCreatorRole=FirebaseDatabase.getInstance().getReference();
        UUID uuid = UUID.randomUUID();
        String uuidAsString = uuid.toString();


        assign_task_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  if(TextUtils.isEmpty(taskName) && TextUtils.isEmpty(taskDes) && TextUtils.isEmpty(statusOfWorking)
                        && TextUtils.isEmpty(selectedStartDate)
                        && TextUtils.isEmpty(selectedPriority)
                        && TextUtils.isEmpty(selectEstimatedTime))
                {
                    Toast.makeText(createTaskActivity.this,"Please Enter the Required Credentials",Toast.LENGTH_LONG).show();
                }
                if(!taskName.isEmpty() && !taskDes.isEmpty() && !statusOfWorking.isEmpty()
                        && !selectedStartDate.isEmpty()
                        && !selectedPriority.isEmpty()
                        && !selectEstimatedTime.isEmpty())
                {*/

               // }
                if(task_nameA.getText().toString().isEmpty())
                {
                    task_nameA.setError("Required");
                    return;
                }
                if(task_deskD.getText().toString().isEmpty())
                {
                    task_deskD.setError("Required");
                    return;
                }
                if(status_of_working.getText().toString().isEmpty())
                {
                    status_of_working.setError("Required");
                    return;
                }
                if(selected_prioriy.getText().toString().isEmpty())
                {
                    selected_prioriy.setError("Required");
                    return;
                }
                if(selected_start_date.getText().toString().isEmpty())
                {
                    selected_start_date.setError("Required");
                    return;
                }
                if(selected_estimated_time.getText().toString().isEmpty())
                {
                    selected_estimated_time.setError("Required");
                    return;
                }
                else
                {  String timestamp=""+System.currentTimeMillis();
                    Log.d("chckk","true");
               HashMap<String,Object> checkinHashMap=new HashMap<>();
                checkinHashMap.put("TaskCreatedBy",usrauth.getCurrentUser().getUid());
                checkinHashMap.put("TaskDesc",task_deskD.getText().toString());
                checkinHashMap.put("TaskName",task_nameA.getText().toString());
                checkinHashMap.put("TaskTime",date.toString());
                checkinHashMap.put("TaskPriority",selected_prioriy.getText().toString());
                checkinHashMap.put("TaskStatus",status_of_working.getText().toString());
                checkinHashMap.put("TaskTimeEstimated",selected_estimated_time.getText().toString());
                checkinHashMap.put("TaskUid",uuidAsString);
                checkinHashMap.put("TaskStartDate",selected_start_date.getText().toString());
                applyForLeavesDbRef.child("AssignTask").child(usrauth.getCurrentUser().getUid()).child(uuidAsString).setValue(checkinHashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        TaskCreatorRole.child("ExistingUsers").child(usrauth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String TaskCreatorRole=snapshot.child("UserRole").getValue().toString();

                                HashMap<String,Object> participantMap=new HashMap<>();
                                participantMap.put("TaskRole","Creator");
                                participantMap.put("UserRole",TaskCreatorRole);
                                participantMap.put("UserUid",usrauth.getCurrentUser().getUid());
                                participantMap.put("TimeStamp",timestamp);
                                applyForLeavesDbRef.child("AssignTask").child(usrauth.getCurrentUser().getUid())
                                        .child(uuidAsString).child("participants").child(usrauth.getCurrentUser().getUid())
                                        .updateChildren(participantMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(createTaskActivity.this, "Assigned Task Successfully", Toast.LENGTH_SHORT).show();
                                                SendUserToMainActivity();
                                               // finish();
                                            }
                                        });

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });





                    }
                });
                Log.d("gettext",task_nameA.getText().toString());
                }


            }
        });



    }
    public void getStatusData(String status){
        status_of_working.setText(status);
        Resources res = createTaskActivity.this.getResources(); //resource handle
        if(status.equals("Stuck"))
        {
            status_for_working.setBackgroundColor(res.getColor(R.color.red));
        }
        else if(status.equals("working on it"))
        {
            status_for_working.setBackgroundColor(res.getColor(R.color.orange));
        }
        else if(status.equals("done"))
        {
            status_for_working.setBackgroundColor(res.getColor(R.color.green));
        }
        else if(status.equals("waiting for review"))
        {
            status_for_working.setBackgroundColor(res.getColor(R.color.colorBlue));
        }
        Log.d("status",status);
    }

    public void getPriorityData(String priority){
        selected_prioriy.setText(priority);
        Resources res = createTaskActivity.this.getResources(); //resource handle
        if(priority.equals("high"))
        {
            select_priority_task.setBackgroundColor(res.getColor(R.color.red));
        }
        else if(priority.equals("medium"))
        {
            select_priority_task.setBackgroundColor(res.getColor(R.color.orange));
        }
        else if(priority.equals("low"))
        {
            select_priority_task.setBackgroundColor(res.getColor(R.color.colorBlue));
        }
        Log.d("priority",priority);
    }

    private void SendUserToMainActivity() {
        Intent mainintent= new Intent(createTaskActivity.this,MainActivity.class);
       // mainintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainintent);
        finish();
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