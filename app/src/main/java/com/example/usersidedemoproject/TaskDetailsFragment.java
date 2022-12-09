package com.example.usersidedemoproject;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.usersidedemoproject.Adapters.TaskAssignUserListAdapter;
import com.example.usersidedemoproject.Model.TaskInfoModel;
import com.example.usersidedemoproject.Model.UserListModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class TaskDetailsFragment extends Fragment {
TextView status_of_working,selected_start_date,
    selected_prioriy,selected_estimated_time;
RelativeLayout statusforworking,select_priority_task;
EditText details_assigned_task_name,details_assigned_task_desc;
    private DatabaseReference load_profile_database_ref;
    String myDataFromActivity;
    String   currentUserID;
    private DatabaseReference usrref,usrrefabc;
    private FirebaseAuth fireauth,newAuth;
    private String currentuserid;
    RelativeLayout select_team,select_start_date,estimated_select_time;
    private Dialog apply_leave_dialogue;
    RecyclerView selectTeam;
    ArrayList<UserListModel> userListModels;
    TaskAssignUserListAdapter adapter;
    SearchView dialogueSearchView;
    int Chour,Cminute;
    int day,month,year;
    Button assign_task_update_btn;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v=inflater.inflate(R.layout.fragment_task_details, container, false);
        details_assigned_task_name=v.findViewById(R.id.details_assigned_task_name);
        details_assigned_task_desc=v.findViewById(R.id.details_assigned_task_desc);
        status_of_working=v.findViewById(R.id.status_of_working);
        selected_start_date=v.findViewById(R.id.selected_start_date);
        selected_prioriy=v.findViewById(R.id.selected_prioriy);
        selected_estimated_time=v.findViewById(R.id.selected_estimated_time);
        select_priority_task=v.findViewById(R.id.select_priority_task);
        select_start_date=v.findViewById(R.id.select_start_date);
        estimated_select_time=v.findViewById(R.id.estimated_select_time);
        assign_task_update_btn=v.findViewById(R.id.assign_task_update_btn);
        CreatedTaskDetailsUpdates activity = (CreatedTaskDetailsUpdates) getActivity();
        myDataFromActivity = activity.getData();
        Calendar calendar= Calendar.getInstance();
        loadTask(myDataFromActivity);
        //setCategoryDialog();
        checkIfCreator();
        statusforworking=v.findViewById(R.id.status_for_working);
        statusforworking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statusOfTaskFragment statusOfTaskFragment=new statusOfTaskFragment();
                statusOfTaskFragment.show(getActivity().getSupportFragmentManager(),statusOfTaskFragment.getTag());
            }
        });
        Resources res = getContext().getResources(); //resource handle
        getParentFragmentManager().setFragmentResultListener("dataFrom1", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                String data=result.getString("stk");
                Log.d("clicked","taskdetaisldata"+data);
                status_of_working.setText(data);

                if(data.equals("Stuck"))
                {
                    status_of_working.setBackgroundColor(res.getColor(R.color.red));
                }
                else if(data.equals("working on it"))
                {
                    status_of_working.setBackgroundColor(res.getColor(R.color.orange));
                }
                else if(data.equals("done"))
                {
                    status_of_working.setBackgroundColor(res.getColor(R.color.green));
                }
                else if(data.equals("waiting for review"))
                {
                    status_of_working.setBackgroundColor(res.getColor(R.color.colorBlue));
                }
            }
        });

        getParentFragmentManager().setFragmentResultListener("dataFrom2", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                String data=result.getString("prio");
                Log.d("clicked","taskdetaisldata"+data);
                selected_prioriy.setText(data);
                if(data.equals("high"))
                {
                    select_priority_task.setBackgroundColor(res.getColor(R.color.red));
                }
                else if(data.equals("medium"))
                {
                    select_priority_task.setBackgroundColor(res.getColor(R.color.orange));
                }
                else if(data.equals("low"))
                {
                    select_priority_task.setBackgroundColor(res.getColor(R.color.colorBlue));
                }
            }
        });

        select_team=v.findViewById(R.id.select_team_icon);
        select_team.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              Intent i=new Intent(getContext(), ShowMembersInActivity.class);
              i.putExtra("task_id",myDataFromActivity);
              startActivity(i);
            }
        });

        select_priority_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                priorityForTaskFragment priorityFragment=new priorityForTaskFragment();
                priorityFragment.show(getActivity().getSupportFragmentManager(),priorityFragment.getTag());
            }
        });
        estimated_select_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog=new TimePickerDialog(getContext()
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
        select_start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                year=calendar.get(Calendar.YEAR);
                month=calendar.get(Calendar.MONTH);
                day=calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog=new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        selected_start_date.setText(dayOfMonth+"-"+(month+1)+"-"+year);
                    }
                },year,month,day);
                datePickerDialog.setCancelable(true);
                datePickerDialog.show();

            }
        });

        assign_task_update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(details_assigned_task_name.getText().toString().isEmpty())
                {
                    details_assigned_task_name.setError("Required");
                    return;
                }
                if(details_assigned_task_desc.getText().toString().isEmpty())
                {
                    details_assigned_task_desc.setError("Required");
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
                {
                    usrrefabc=  FirebaseDatabase.getInstance().getReference();
                    newAuth= FirebaseAuth.getInstance();
                    currentuserid=fireauth.getCurrentUser().getUid();
                    usrrefabc.child("AssignTask").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                            for(DataSnapshot ds:snapshot.getChildren())
                            {
                                for(DataSnapshot ds2:ds.getChildren())
                                {
                                    String task_created_by =ds2.child("TaskCreatedBy").getValue().toString().trim();
                                    String task_uid =ds2.child("TaskUid").getValue().toString().trim();

                                    DatabaseReference updates,applyForLeavesDbRef;
                                    applyForLeavesDbRef=  FirebaseDatabase.getInstance().getReference();
                                    updates=  FirebaseDatabase.getInstance().getReference();
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
                                                            HashMap<String,Object> checkinHashMap=new HashMap<>();
                                                            checkinHashMap.put("TaskPriority",selected_prioriy.getText().toString());
                                                            checkinHashMap.put("TaskStatus",status_of_working.getText().toString());
                                                            checkinHashMap.put("TaskTimeEstimated",selected_estimated_time.getText().toString());
                                                            checkinHashMap.put("TaskStartDate",selected_start_date.getText().toString());

                                                            checkinHashMap.put("TaskName",details_assigned_task_name.getText().toString());
                                                            checkinHashMap.put("TaskDesc",details_assigned_task_desc.getText().toString());

                                                            applyForLeavesDbRef.child("AssignTask").child(task_created_by).child(task_uid).updateChildren(checkinHashMap);

                                                        }
                                                    }
                                                }
                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull @NotNull DatabaseError error) {
                        }
                    });
                }

            }
        });
        return v;
    }

    private void checkIfCreator()
    {
        usrrefabc=  FirebaseDatabase.getInstance().getReference();
        fireauth= FirebaseAuth.getInstance();
        currentuserid=fireauth.getCurrentUser().getUid();

        usrrefabc.child("AssignTask").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren())
                {
                    for(DataSnapshot ds2:ds.getChildren())
                    {
                        String task_created_by =ds2.child("TaskCreatedBy").getValue().toString().trim();
                        if(currentuserid.equals(task_created_by))
                        {
                            select_priority_task.setClickable(true);
                            select_team.setClickable(true);
                            select_start_date.setClickable(true);
                            estimated_select_time.setClickable(true);

                            details_assigned_task_desc.setClickable(true);
                            details_assigned_task_name.setClickable(true);

                        }
                        else
                        {
                            select_priority_task.setClickable(false);
                            select_priority_task.setEnabled(false);

                            select_team.setClickable(false);
                            select_team.setEnabled(false);

                            select_start_date.setClickable(false);
                            select_start_date.setEnabled(false);

                            estimated_select_time.setClickable(false);
                            estimated_select_time.setEnabled(false);

                            details_assigned_task_desc.setClickable(false);
                            details_assigned_task_desc.setEnabled(false);

                            details_assigned_task_name.setClickable(false);
                            details_assigned_task_name.setEnabled(false);

                            statusforworking.setClickable(true);
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void loadTask(String task_id){

        usrref=  FirebaseDatabase.getInstance().getReference();
        fireauth= FirebaseAuth.getInstance();
        currentuserid=fireauth.getCurrentUser().getUid();
        Resources res = getContext().getResources(); //resource handle
        usrref.child("AssignTask").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren())
                {
                    for(DataSnapshot ds2:ds.getChildren())
                    {
                        String taskid =ds2.child("TaskUid").getValue().toString();
                        String task_name =ds2.child("TaskName").getValue().toString();
                        String task_desc=ds2.child("TaskDesc").getValue().toString();
                        String task_priority =ds2.child("TaskPriority").getValue().toString();
                        String task_start_date =ds2.child("TaskStartDate").getValue().toString();
                        String task_status=ds2.child("TaskStatus").getValue().toString();
                        String task_time=ds2.child("TaskTime").getValue().toString();
                        String task_time_estimated =ds2.child("TaskTimeEstimated").getValue().toString();
                        if(taskid.equals(task_id))
                        {
                            details_assigned_task_name.setText(task_name);
                            details_assigned_task_desc.setText(task_desc);
                            status_of_working.setText(task_status);
                            selected_start_date.setText(task_start_date);
                            selected_prioriy.setText(task_priority);
                            selected_estimated_time.setText(task_time_estimated);

                            if(task_priority.equals("high"))
                            {
                                select_priority_task.setBackgroundColor(res.getColor(R.color.red));
                            }
                            else if(task_priority.equals("medium"))
                            {
                                select_priority_task.setBackgroundColor(res.getColor(R.color.orange));
                            }
                            else if(task_priority.equals("low"))
                            {
                                select_priority_task.setBackgroundColor(res.getColor(R.color.colorBlue));
                            }


                            if(task_status.equals("stuck"))
                            {
                                status_of_working.setBackgroundColor(res.getColor(R.color.red));
                            }
                            else if(task_status.equals("working on it"))
                            {
                                status_of_working.setBackgroundColor(res.getColor(R.color.orange));
                            }
                            else if(task_status.equals("done"))
                            {
                                status_of_working.setBackgroundColor(res.getColor(R.color.green));
                            }
                            else if(task_status.equals("waiting for review"))
                            {
                                status_of_working.setBackgroundColor(res.getColor(R.color.colorBlue));
                            }

                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
            }
        });
    }
    public void getStatusData(String status){
        status_of_working.setText(status);
        Log.d("status",status);
    }

  /*  private void setCategoryDialog() {

        userlist();
        apply_leave_dialogue =new Dialog(getContext());
        apply_leave_dialogue.setContentView(R.layout.add_member_dialogue);
        apply_leave_dialogue.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.rounded_box));
        apply_leave_dialogue.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        apply_leave_dialogue.setCancelable(true);
        userListModels=new ArrayList<>();
        adapter=new TaskAssignUserListAdapter(getContext(),userListModels);
        selectTeam=apply_leave_dialogue.findViewById(R.id.user_lists);
        dialogueSearchView=apply_leave_dialogue.findViewById(R.id.search_members);
        *//*dialogueSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });*//*

        LinearLayoutManager lmn=new LinearLayoutManager(getContext());
        selectTeam.setLayoutManager(lmn);
        selectTeam.setAdapter(adapter);
        selectTeam.setHasFixedSize(true);



    }

    public void userlist()
    {
        usrrefabc=  FirebaseDatabase.getInstance().getReference();
        usrrefabc.child("ExistingUsers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren())
                {
                    String uuid =ds.child("UserUid").getValue().toString();
                    String uname =ds.child("UserName").getValue().toString();
                    userListModels.add(new UserListModel(uname,uuid));
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
            }
        });

    }*/

}