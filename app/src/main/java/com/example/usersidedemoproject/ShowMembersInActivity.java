package com.example.usersidedemoproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SearchView;

import com.example.usersidedemoproject.Adapters.TaskAssignUserListAdapter;
import com.example.usersidedemoproject.Adapters.TaskMemberListAdapter;
import com.example.usersidedemoproject.Model.MemberInTaskModel;
import com.example.usersidedemoproject.Model.UserListModel;
import com.example.usersidedemoproject.Utility.NetworkChangeListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.util.ArrayList;

public class ShowMembersInActivity extends AppCompatActivity {
    NetworkChangeListener networkChangeListener=new NetworkChangeListener();
RecyclerView member_in_task_rc;
ArrayList<MemberInTaskModel> memberInTaskModels;
TaskMemberListAdapter taskMemberListAdapter;
DatabaseReference memberListRef,usrrefabc;
String taskId;
Button add_member_button;
private Dialog apply_leave_dialogue;
RecyclerView selectTeam;
ArrayList<UserListModel> userListModels;
TaskAssignUserListAdapter adapter;
private FirebaseAuth fireauth;
private String currentuserid;
SearchView dialogueSearchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_members_in);

        Intent i=getIntent();
        taskId=i.getStringExtra("task_id");
        memberListRef= FirebaseDatabase.getInstance().getReference();
        member_in_task_rc=findViewById(R.id.member_in_task_rc);
        memberInTaskModels=new ArrayList<>();
        taskMemberListAdapter=new TaskMemberListAdapter(this,memberInTaskModels);
        fireauth= FirebaseAuth.getInstance();
        currentuserid=fireauth.getCurrentUser().getUid();
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        member_in_task_rc.setLayoutManager(linearLayoutManager);
        member_in_task_rc.setAdapter(taskMemberListAdapter);
        member_in_task_rc.setHasFixedSize(true);
        add_member_button=findViewById(R.id.add_member_button);
        add_member_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                apply_leave_dialogue.show();
            }
        });
        loadData();
        setCategoryDialog();
        checkIfManager();
    }
    public void checkIfManager()
    {
        memberListRef.child("AssignTask").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren())
                {
                    for(DataSnapshot ds2:ds.getChildren())
                    {
                        String task_created_by =ds2.child("TaskCreatedBy").getValue().toString().trim();
                        if(currentuserid.equals(task_created_by))
                        {
                            add_member_button.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            add_member_button.setVisibility(View.GONE);

                        }


                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void loadData()
    {
        memberListRef.child("AssignTask").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren())
                {
                    for(DataSnapshot ds2:ds.getChildren())
                    {
                        String DbTaskId=ds2.child("TaskUid").getValue().toString();
                        String createdBy=ds2.child("TaskCreatedBy").getValue().toString();
                        if(DbTaskId.equals(taskId))
                        {
                            memberListRef.child("AssignTask").child(createdBy).child(taskId).child("participants")
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot participant) {
                                            memberInTaskModels.clear();
                                            for(DataSnapshot members:participant.getChildren())
                                            {
                                                String taskRole=members.child("TaskRole").getValue().toString();
                                                String timeStamp=members.child("TimeStamp").getValue().toString();
                                                String userRole=members.child("UserRole").getValue().toString();
                                                String userUid=members.child("UserUid").getValue().toString();
                                                memberInTaskModels.add(new MemberInTaskModel(taskRole,timeStamp,userRole,userUid));
                                            }
                                            taskMemberListAdapter.notifyDataSetChanged();

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

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
    private void setCategoryDialog() {

        userlist();
        apply_leave_dialogue =new Dialog(this);
        apply_leave_dialogue.setContentView(R.layout.add_member_dialogue);
        apply_leave_dialogue.getWindow().setBackgroundDrawable(this.getDrawable(R.drawable.rounded_box));
        apply_leave_dialogue.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        apply_leave_dialogue.setCancelable(true);
        userListModels=new ArrayList<>();
        adapter=new TaskAssignUserListAdapter(this,userListModels);
        selectTeam=apply_leave_dialogue.findViewById(R.id.user_lists);
        dialogueSearchView=apply_leave_dialogue.findViewById(R.id.search_members);
        /*dialogueSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });*/

        LinearLayoutManager lmn=new LinearLayoutManager(this);
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
                    String unrole =ds.child("UserRole").getValue().toString();
                    userListModels.add(new UserListModel(uname,uuid,unrole));
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
            }
        });

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