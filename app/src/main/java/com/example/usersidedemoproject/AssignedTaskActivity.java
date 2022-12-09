package com.example.usersidedemoproject;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.usersidedemoproject.Adapters.AssignedTaskAdapter;
import com.example.usersidedemoproject.Model.TaskInfoModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.util.ArrayList;

public class AssignedTaskActivity extends AppCompatActivity {
RecyclerView assignedTaskRecycler;
AssignedTaskAdapter assignedTaskAdapter;
ArrayList<TaskInfoModel> taskInfoModels;
private DatabaseReference usrref;
private FirebaseAuth fireauth;
private String currentuserid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assigned_task);
        usrref=  FirebaseDatabase.getInstance().getReference();
        fireauth= FirebaseAuth.getInstance();
        currentuserid=fireauth.getCurrentUser().getUid();
        taskInfoModels=new ArrayList<>();
        assignedTaskRecycler=findViewById(R.id.assigned_task_recycler);
        assignedTaskAdapter=new AssignedTaskAdapter(this,taskInfoModels);
        LinearLayoutManager lmn=new LinearLayoutManager(this);
        assignedTaskRecycler.setLayoutManager(lmn);
        assignedTaskRecycler.setHasFixedSize(true);
        assignedTaskRecycler.setAdapter(assignedTaskAdapter);
        loadTask();
    }
    private void loadTask(){

        usrref.child("AssignTask").child(currentuserid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren())
                {
                    String task_created_by =ds.child("TaskCreatedBy").getValue().toString();;
                    String task_desc=ds.child("TaskDesc").getValue().toString();
                    String task_name=ds.child("TaskName").getValue().toString();
                    String task_time =ds.child("TaskTime").getValue().toString();
                    String task_priority =ds.child("TaskPriority").getValue().toString();
                    String task_status =ds.child("TaskStatus").getValue().toString();
                    String task_time_estimated =ds.child("TaskTimeEstimated").getValue().toString();
                    String task_uid =ds.child("TaskUid").getValue().toString();
                    String task_start_date =ds.child("TaskStartDate").getValue().toString();
                    taskInfoModels.add(new TaskInfoModel(task_created_by,task_desc,task_name,task_priority
                            ,task_start_date,task_status,task_time,task_time_estimated,task_uid));

                }
                assignedTaskAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}