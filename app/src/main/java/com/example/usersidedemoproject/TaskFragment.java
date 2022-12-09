package com.example.usersidedemoproject;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.usersidedemoproject.Adapters.HomeTaskAdapter;
import com.example.usersidedemoproject.Adapters.PendingApprovalAdapter;
import com.example.usersidedemoproject.Model.TaskInfoModel;
import com.example.usersidedemoproject.Model.UpdateMessageModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.util.ArrayList;

public class TaskFragment extends Fragment {
    Button b;
    RecyclerView taskRc;
    FloatingActionButton createTaskBtn;
    HomeTaskAdapter homeTaskAdapter;
    ArrayList<TaskInfoModel> taskInfoModel;
    private DatabaseReference usrref,checkRole;
    private FirebaseAuth fireauth;
    private String currentuserid;
    Button testing1,testin2;
    LinearLayout show_no_task_assigned;
    DatabaseReference updates;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_task, container, false);
        getActivity().setTitle("Task");
        checkRole=FirebaseDatabase.getInstance().getReference();
        fireauth= FirebaseAuth.getInstance();

        updates=  FirebaseDatabase.getInstance().getReference();
        taskRc=v.findViewById(R.id.task_recycler);
        currentuserid=fireauth.getCurrentUser().getUid();
        checkRole();
        usrref=  FirebaseDatabase.getInstance().getReference().child("AssignTask");
        //show_no_task_assigned=v.findViewById(R.id.show_no_task_assigned);
        if(usrref!=null)
        {
            load2();
        }
        else
        {
            //show_no_task_assigned.setVisibility(View.VISIBLE);
        }


        taskInfoModel=new ArrayList<>();
        homeTaskAdapter=new HomeTaskAdapter(getContext(),taskInfoModel);
        LinearLayoutManager lmn=new LinearLayoutManager(getContext());
        taskRc.setLayoutManager(lmn);
        taskRc.setHasFixedSize(true);
        taskRc.setAdapter(homeTaskAdapter);
        createTaskBtn=v.findViewById(R.id.create_task);
        createTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getContext(),createTaskActivity.class);
                startActivity(i);
            }
        });
       /* testing1=v.findViewById(R.id.testing1);
        testin2=v.findViewById(R.id.testing2);*/
      /*  testing1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getContext(), PendinApprovalActivity.class);
                startActivity(i);
            }
        });

        testin2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getContext(),ShowLeavesActivity.class);
                startActivity(i);
            }
        });*/

        return v;

    }
    public void checkRole()
    {
        checkRole.child("ExistingUsers").child(currentuserid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String userRole=snapshot.child("UserRole").getValue().toString();
                if(userRole.equals("Manager"))
                {
                    if(createTaskBtn.getVisibility()==View.GONE)
                    {
                        createTaskBtn.setVisibility(View.VISIBLE);
                    }
                    else
                    {

                        createTaskBtn.setVisibility(View.GONE);
                    }
                }
                else
                {
                        createTaskBtn.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void load2()
    {
        usrref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren())
                { taskInfoModel.clear();
                    for(DataSnapshot ds2:ds.getChildren())
                    {
                        String task_created_by =ds2.child("TaskCreatedBy").getValue().toString().trim();
                        String task_desc=ds2.child("TaskDesc").getValue().toString().trim();
                        String task_name=ds2.child("TaskName").getValue().toString().trim();
                        String task_time =ds2.child("TaskTime").getValue().toString().trim();
                        String task_priority =ds2.child("TaskPriority").getValue().toString().trim();
                        String task_status =ds2.child("TaskStatus").getValue().toString().trim();
                        String task_time_estimated =ds2.child("TaskTimeEstimated").getValue().toString().trim();
                        String task_uid =ds2.child("TaskUid").getValue().toString().trim();
                        String task_start_date =ds2.child("TaskStartDate").getValue().toString().trim();
                     /*   if(currentuserid.equals(task_created_by))
                        {
                            show_no_task_assigned.setVisibility(View.GONE);
                            DatabaseReference updates;
                            updates=  FirebaseDatabase.getInstance().getReference();
                            updates.child("AssignTask").child(task_created_by).child(task_uid).child("participants")
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot assign) {
                                            taskInfoModel.clear();
                                            for(DataSnapshot ds3:assign.getChildren())
                                            {
                                                String taskPartyId =ds3.child("UserUid").getValue().toString().trim();

                                                Log.d("preCurrent",""+currentuserid);
                                                if(currentuserid.equals(taskPartyId))
                                                {
                                                    Log.d("true",""+currentuserid.equals(taskPartyId));
                                                    taskInfoModel.add(new TaskInfoModel(task_created_by,task_desc,task_name,task_priority
                                                            ,task_start_date,task_status,task_time,task_time_estimated,task_uid));
                                                    show_no_task_assigned.setVisibility(View.GONE);
                                                }

                                            }
                                            homeTaskAdapter.notifyDataSetChanged();

                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                        }
                        else
                        {*/
                                updates.child("AssignTask").child(task_created_by).child(task_uid).child("participants")
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                for(DataSnapshot ds3:snapshot.getChildren())
                                                {
                                                    String taskPartyId =ds3.child("UserUid").getValue().toString().trim();
                                                    Log.d("pre",""+taskPartyId);
                                                    Log.d("preCurrent",""+currentuserid);
                                                    if(currentuserid.equals(taskPartyId))
                                                    {
                                                        taskInfoModel.add(new TaskInfoModel(task_created_by,task_desc,task_name,task_priority
                                                                ,task_start_date,task_status,task_time,task_time_estimated,task_uid));
                                                        //show_no_task_assigned.setVisibility(View.GONE);
                                                    }
                                                    else if(!snapshot.hasChild(currentuserid))
                                                    {

                                                        //show_no_task_assigned.setVisibility(View.VISIBLE);
                                                    }
                                                }
                                                homeTaskAdapter.notifyDataSetChanged();

                                            }
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                       // }



                    }
                }
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
            }
        });
    }
  /*  private void loadTask(){
       usrref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren())
                {
                    for(DataSnapshot ds2:ds.getChildren())
                    {
                        String task_created_by =ds2.child("TaskCreatedBy").getValue().toString().trim();
                        String task_desc=ds2.child("TaskDesc").getValue().toString().trim();
                        String task_name=ds2.child("TaskName").getValue().toString().trim();
                        String task_time =ds2.child("TaskTime").getValue().toString().trim();
                        String task_priority =ds2.child("TaskPriority").getValue().toString().trim();
                        String task_status =ds2.child("TaskStatus").getValue().toString().trim();
                        String task_time_estimated =ds2.child("TaskTimeEstimated").getValue().toString().trim();
                        String task_uid =ds2.child("TaskUid").getValue().toString().trim();
                        String task_start_date =ds2.child("TaskStartDate").getValue().toString().trim();
                        Log.d("pre","pre1");

                    }


                }
                homeTaskAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }*/
}